package ru.flashsafe.core.old.storage.rest;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;
import static java.net.HttpURLConnection.HTTP_OK;

/**
 * 
 * 
 * @author Andrew
 *
 */
@Provider
public class ContentTypeFixerFilter implements ClientResponseFilter {

    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
        if (!requestContext.getAcceptableMediaTypes().contains(MediaType.APPLICATION_JSON_TYPE)) {
            return;
        }
        if (responseContext.getStatus() == HTTP_OK && contentTypeIsNotJson(responseContext)) {
            responseContext.getHeaders().remove(HttpHeaders.CONTENT_TYPE);
            responseContext.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        }
    }

    private static boolean contentTypeIsNotJson(ClientResponseContext responseContext) {
        return !MediaType.APPLICATION_JSON.equals(responseContext.getHeaderString(HttpHeaders.CONTENT_TYPE));
    }

}
