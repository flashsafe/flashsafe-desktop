package ru.flashsafe.core.old.storage.rest;

import java.io.IOException;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import ru.flashsafe.core.FlashSafeRegistry;
import ru.flashsafe.core.event.ApplicationStopEvent;
import ru.flashsafe.core.event.FlashSafeEventService;
import ru.flashsafe.core.old.storage.rest.data.AuthData;
import ru.flashsafe.core.old.storage.rest.data.AuthResponse;

/**
 * Authentication filter which uses FlashSafe device to provide auth data.
 * 
 * @author Andrew
 *
 */
@Priority(Priorities.AUTHENTICATION)
public class FlashSafeAuthClientFilter implements ClientRequestFilter {

    private static final String ACCESS_TOKEN_PARAMETER = "access_token";

    private static final String ID_PARAMETER = "id";

    private static final String AUTH_URL = "auth";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FlashSafeAuthClientFilter.class);

    private final String clientId;
    
    private final String clientSecret;
    
    private final Client client;

    private final WebTarget authTarget;

    private AuthData currentAuthData;

    @Inject
    public FlashSafeAuthClientFilter(FlashSafeEventService eventService) {
        clientId = FlashSafeRegistry.readProperty(FlashSafeRegistry.USER_ID);
        clientSecret = FlashSafeRegistry.readProperty(FlashSafeRegistry.SECRET);
        client = ClientBuilder.newBuilder().register(JacksonFeature.class).register(ContentTypeFixerFilter.class).build();
        String storageAddress = FlashSafeRegistry.getStorageAddress();
        authTarget = client.target(storageAddress).path(AUTH_URL);
        eventService.registerSubscriber(this);
    }
    
    @Subscribe
    public void handleApplicationStopEvent(ApplicationStopEvent event) {
        LOGGER.info("Handling application stop event");
        client.close();
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        String query = requestContext.getUri().getQuery();
        if (query == null || !query.contains(ACCESS_TOKEN_PARAMETER)) {
            synchronized (this) {
                if (isAuthDataInvalid()) {
                    AuthData authData = doAuth();
                    currentAuthData = authData;
                }
                //TODO change apply call
                applyAccessTokenToRequest(requestContext, currentAuthData.getToken());
            }
        }
    }

    private static void applyAccessTokenToRequest(ClientRequestContext requestContext, String token) {
        String query = requestContext.getUri().getQuery();
        String accessTokenParameter = (query == null) ? "?" : "&";
        accessTokenParameter += ACCESS_TOKEN_PARAMETER + "=" + token;
        requestContext.setUri(URI.create(requestContext.getUri().toString() + accessTokenParameter));
    }
    
    private AuthData doAuth() {
        return doAuth(false);
    }

    private AuthData doAuth(boolean exitOnFail) {
        AuthResponse authResponse = authTarget.request(MediaType.APPLICATION_JSON_TYPE).post(
                Entity.form(new Form(ID_PARAMETER, clientId)), AuthResponse.class);

        AuthData authData = authResponse.getAuthData();
        String hash = md5(authData.getToken() + getSecret() + authData.getTimestamp());

        Form form = new Form(ID_PARAMETER, clientId);
        form.param(ACCESS_TOKEN_PARAMETER, hash);
        /* add try-catch and try again - just temporary workaround - back-end do something weird when you request it first time */
        try {
            AuthResponse authResponse2 = authTarget.request(MediaType.APPLICATION_JSON_TYPE).post(Entity.form(form), AuthResponse.class);
            return authResponse2.getAuthData();
        } catch (ProcessingException e) {
            LOGGER.info("Weird thing just happened", e);
            if (exitOnFail) {
                throw e;
            }
            /* try one more time */
            return doAuth(true);
        }

    }

    private boolean isAuthDataInvalid() {
        if (currentAuthData == null) {
            return true;
        }
        long currentTime = System.currentTimeMillis();
        long timeout = currentTime + currentAuthData.getTimeout() * 1000;
        return (timeout - currentTime) <= 0;
    }

    private static String md5(final String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException nsae) {
            throw new RuntimeException("Error on generate MD5", nsae);
        }
    }

    private String getSecret() {
        return clientSecret;
    }
}
