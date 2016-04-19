package ru.flashsafe.dev;

import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import ru.flashsafe.common.ssl.SSLService;

public class SSLServiceDevImplementation implements SSLService {

    @Override
    public SSLContext sslContext() throws GeneralSecurityException {
        SSLContext sslContext;
        sslContext = SSLContext.getInstance("TLS");
        TrustManager[] trustAllCerts = { new InsecureTrustManager() };
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        return sslContext;
    }

    @Override
    public HostnameVerifier hostnameVerifier() {
        return new AllHostsValidHostnameVerifier();
    }
    
    private static class AllHostsValidHostnameVerifier implements HostnameVerifier {

        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };
    
    private static class InsecureTrustManager implements X509TrustManager {
        /**
         * {@inheritDoc}
         */
        @Override
        public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
            // Everyone is trusted!
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
            // Everyone is trusted!
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

}
