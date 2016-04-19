package ru.flashsafe.application;

import java.security.GeneralSecurityException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.flashsafe.application.model.JsonApplication;
import ru.flashsafe.application.model.JsonChecksum;
import ru.flashsafe.common.application.Application;
import ru.flashsafe.common.fv.checksum.Checksum;
import ru.flashsafe.common.ssl.SSLService;
import ru.flashsafe.common.version.Version;
import ru.flashsafe.updater.UpdateSources;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class RemoteApplicationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteApplicationService.class);
    
    private static final String APPLICATIONS_API_PATH = "application";
    
    private static final String CHECKSUM = "checksum"; 
    
    private final Client restClient;
    
    private final WebTarget applicationsTarget;
    
    private final SSLService sslService;
    
    @Inject
    RemoteApplicationService(SSLService sslService) {
        this.sslService = sslService;
        try {
            SSLContext sslContext = this.sslService.sslContext();
            HostnameVerifier hostnameVerifier = this.sslService.hostnameVerifier();
            restClient = ClientBuilder.newBuilder().register(JacksonFeature.class).sslContext(sslContext).hostnameVerifier(hostnameVerifier).build();
            applicationsTarget = restClient.target(UpdateSources.FLASH_SO.updateSourceURL).path(APPLICATIONS_API_PATH);
        } catch (GeneralSecurityException e) {
            LOGGER.error("Error", e);
            throw new RuntimeException(e);
        }

    }
    
    public Application findApplication(String applicationId) {
        return applicationsTarget.path(applicationId).request(MediaType.APPLICATION_JSON_TYPE).get(JsonApplication.class);
    }
    
    public Checksum getChecksum(String applicationId, Version version) {
        return applicationsTarget.path(applicationId).path(version.toString()).path(CHECKSUM).request(MediaType.APPLICATION_JSON_TYPE)
                .get(JsonChecksum.class);
    }
    
}
