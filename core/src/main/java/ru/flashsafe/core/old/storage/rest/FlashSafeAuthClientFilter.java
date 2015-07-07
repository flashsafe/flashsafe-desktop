package ru.flashsafe.core.old.storage.rest;

import java.io.IOException;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.jackson.JacksonFeature;

import ru.flashsafe.core.old.storage.rest.data.AuthData;
import ru.flashsafe.core.old.storage.rest.data.AuthResponse;

@Priority(Priorities.AUTHENTICATION)
public class FlashSafeAuthClientFilter implements ClientRequestFilter {

    private static final String ACCESS_TOKEN_PARAMETER = "access_token";

    private static final String ID_PARAMETER = "id";

    private static final String AUTH_URL = "auth.php";

    private final Client client;

    private final WebTarget authTarget;

    private AuthData currentAuthData;

    public FlashSafeAuthClientFilter() {
        client = ClientBuilder.newBuilder().register(JacksonFeature.class).register(ContentTypeFixerFilter.class).build();
        authTarget = client.target("https://flashsafe-alpha.azurewebsites.net").path(AUTH_URL);
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
        AuthResponse authResponse = authTarget.request(MediaType.APPLICATION_JSON_TYPE).post(Entity.form(new Form(ID_PARAMETER, "1")), AuthResponse.class);
        
            AuthData authData = authResponse.getAuthData();
            String hash = md5(authData.getToken() + getSecret() + authData.getTimestamp());

            Form form = new Form(ID_PARAMETER, "1");
            form.param(ACCESS_TOKEN_PARAMETER, hash);
            AuthResponse authResponse2 = authTarget.request(MediaType.APPLICATION_JSON_TYPE).post(Entity.form(form), AuthResponse.class);

               System.out.println(authResponse2);
                return authResponse2.getAuthData();

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
        return "open123458";
    }
}
