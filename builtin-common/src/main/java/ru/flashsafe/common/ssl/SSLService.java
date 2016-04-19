package ru.flashsafe.common.ssl;

import java.security.GeneralSecurityException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

public interface SSLService {

    SSLContext sslContext() throws GeneralSecurityException;
    
    HostnameVerifier hostnameVerifier();
    
}
