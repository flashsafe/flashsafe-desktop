package ru.flashsafe.file_loader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.security.GeneralSecurityException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import ru.flashsafe.common.ssl.SSLService;

import com.github.axet.wget.WGet;
import com.github.axet.wget.info.DownloadInfo;

public class DownloadUtil {

    private final SSLService sslService;

    public DownloadUtil(SSLService sslService) {
        this.sslService = sslService;
    }

    public Path download(URL url, Path targetPath) {
        //TrustManager[] trustAllCerts = new TrustManager[] { new InsecureTrustManager() };
        try {
            SSLContext sc = sslService.sslContext();
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(sslService.hostnameVerifier());
            DownloadInfo downloadInfo = new DownloadInfo(url);
            downloadInfo.extract();
            File targetFile = new File(targetPath.toFile(), "application_package.zip");
            WGet fileLoader = new WGet(downloadInfo, targetFile);
            fileLoader.download();
            return WGet.calcName(downloadInfo, targetFile).toPath();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("", e);
        }
    }

    public Path download(String urlString, Path targePath) {
        try {
            URL url = new URL(urlString);
            return download(url, targePath);
        } catch (MalformedURLException e) {
            throw new RuntimeException("", e);
        }
    }
    
//    public static class InsecureTrustManager implements X509TrustManager {
//        /**
//         * {@inheritDoc}
//         */
//        @Override
//        public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
//            // Everyone is trusted!
//        }
//
//        /**
//         * {@inheritDoc}
//         */
//        @Override
//        public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
//            // Everyone is trusted!
//        }
//
//        /**
//         * {@inheritDoc}
//         */
//        @Override
//        public X509Certificate[] getAcceptedIssuers() {
//            return new X509Certificate[0];
//        }
//    }
}
