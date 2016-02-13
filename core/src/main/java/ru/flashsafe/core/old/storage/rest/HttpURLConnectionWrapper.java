package ru.flashsafe.core.old.storage.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.security.Permission;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * 
 * Use only for test purposes!
 * 
 * @author Andrew
 *
 */
@Deprecated
public class HttpURLConnectionWrapper extends HttpURLConnection {

    private HttpURLConnection httpURLConnection;
    
    private OutputStream outputStream;
    
    private long written;
    
    protected HttpURLConnectionWrapper(HttpURLConnection httpURLConnection) {
        super(null);
        this.httpURLConnection = /*(HttpsURLConnection)*/ httpURLConnection;
        /*TrustManager[] trustCerts = new TrustManager[] { 
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    X509Certificate[] certs = new X509Certificate[1];
                    try {
                        InputStream in = getClass().getResourceAsStream("-.azurewebsites.net.pem");
                        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
                        X509Certificate cer = (X509Certificate) certFactory.generateCertificate(in);
                        in.close();
                        certs[0] = cer;
                    } catch(CertificateException | IOException ex) {
                        ex.printStackTrace();
                    }
                    return certs;
                }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                public void checkServerTrusted(X509Certificate[] certs, String authType) {}
            }
        };*/

        /*HostnameVerifier hv = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return session.getPeerHost().equals(hostname);
            }
        };*/

        try {
            //SSLContext sc = SSLContext.getInstance("SSL");
            //sc.init(null, trustCerts, new SecureRandom());
            //HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            //HttpsURLConnection.setDefaultHostnameVerifier(hv);
        } catch (Exception e) {}
    }
    
    @Override
    public void setDoOutput(boolean value) {
        httpURLConnection.setDoOutput(value);
    }
    
    @Override
    public void setDoInput(boolean value) {
        httpURLConnection.setDoInput(value);
    }

    public String getHeaderFieldKey (int n) {
        return httpURLConnection.getHeaderField(n);
    }

    public void setFixedLengthStreamingMode (int contentLength) {
        httpURLConnection.setFixedLengthStreamingMode(contentLength);
    }

    public void setChunkedStreamingMode (int chunklen) {
        httpURLConnection.setChunkedStreamingMode(chunklen);
    }

    public String getHeaderField(int n) {
        return httpURLConnection.getHeaderField(n);
    }
    
     public void setInstanceFollowRedirects(boolean followRedirects) {
         httpURLConnection.setInstanceFollowRedirects(followRedirects);
     }

     public boolean getInstanceFollowRedirects() {
         return httpURLConnection.getInstanceFollowRedirects();
     }

    
    public void setRequestMethod(String method) throws ProtocolException {
        httpURLConnection.setRequestMethod(method);
    }

    
    public String getRequestMethod() {
        return httpURLConnection.getRequestMethod();
    }

   
    public int getResponseCode() throws IOException {
        System.out.println(written + " bytes were written.");
        return httpURLConnection.getResponseCode();
    }

    public String getResponseMessage() throws IOException {
        return httpURLConnection.getResponseMessage();
    }

    public long getHeaderFieldDate(String name, long Default) {
        return httpURLConnection.getHeaderFieldDate(name, Default);
    }

    public void disconnect() {
        httpURLConnection.disconnect();
    }

    
    public boolean usingProxy() {
        return httpURLConnection.usingProxy();
    }

    public Permission getPermission() throws IOException {
        return httpURLConnection.getPermission();
    }

    @Override
    public InputStream getErrorStream() {
        return httpURLConnection.getErrorStream();
    }

    @Override
    public void connect() throws IOException {
        httpURLConnection.connect();
    }
    
    @Override
    public OutputStream getOutputStream() throws IOException {
        outputStream = httpURLConnection.getOutputStream();
        return new OutputStream() {
            
            @Override
            public void write(int b) throws IOException {
                outputStream.write(b);
                written++;
            }
            
            @Override
            public void write(byte[] b) throws IOException {
                outputStream.write(b);
                written += b.length;
            }
            
            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                outputStream.write(b, off, len);
                written += len;
            }
        };
        
    }
    
    @Override
    public URL getURL() {
        return httpURLConnection.getURL();
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        return httpURLConnection.getInputStream();
    }
    
}
