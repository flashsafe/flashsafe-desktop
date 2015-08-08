package ru.flashsafe.core.old.storage.rest;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.HttpHeaders;

public class FlashSafeStubFilter implements ClientRequestFilter {

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        System.out.println("Fileter!!!!!!!!!!!!!");
        requestContext.getHeaders().add(HttpHeaders.CONTENT_LENGTH, 15239659);
    }


}
