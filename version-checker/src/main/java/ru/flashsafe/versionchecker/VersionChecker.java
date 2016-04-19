package ru.flashsafe.versionchecker;

public class VersionChecker {
    
    private static final String VERSION_CHECK_PATH = "update/"; 
    
    private final String host;
    
    private final String updateURL;

    private VersionChecker(String host) {
        this.host = host;
        updateURL = buildUpdateURL(host);
    }
    
    public static VersionChecker build(String host) {
        return new VersionChecker(host);
    }
    
    public Version getAvailableVersion() {
        return null;
    }

    public boolean checkVersion(Version currentVersion) {
        Version availableVersion = getAvailableVersion();
        return currentVersion.compareTo(availableVersion) == 0;
    }
    
    public String getHost() {
        return host;
    }

    public String getUpdateURL() {
        return updateURL;
    }

    private static String buildUpdateURL(String host) {
        StringBuilder updateURL = new StringBuilder(host);
        if (!host.endsWith("/")) {
            updateURL.append("/");
        }
        updateURL.append(VERSION_CHECK_PATH);
        return updateURL.toString();
    }
    
}
