package ru.flashsafe.update_server.application;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import ru.flashsafe.common.application.Application;
import ru.flashsafe.common.fv.checksum.Checksum;
import ru.flashsafe.common.version.Version;

public class ApplicationEntry {

    private final String applicationId;
    
    private volatile Application application;
    
    private final Map<Version, String> versionToResourceMap = new ConcurrentHashMap<>();
    
    private final Map<String, Checksum> resourceToChecksumMap = new ConcurrentHashMap<>();
    
    public ApplicationEntry(Application application, String resourceId, Checksum checksum) {
        applicationId = application.getId();
        this.application = application;
        versionToResourceMap.put(application.getLatestVersion(), resourceId);
        resourceToChecksumMap.put(resourceId, checksum);
    }

    public String getApplicationId() {
        return applicationId;
    }
    
    public synchronized Application getApplication() {
        return application;
    }

    public synchronized void registerVersion(Application newApplication, String resourceId, Checksum checksum) {
        versionToResourceMap.put(newApplication.getLatestVersion(), resourceId);
        resourceToChecksumMap.put(resourceId, checksum);
        Version newVersion = newApplication.getLatestVersion();
        if (newVersion.compareTo(application.getLatestVersion()) > 0) {
            application = newApplication;
        }
    }
    
    public synchronized void unregisterVersion(Application application) {
        String resourceId = versionToResourceMap.remove(application.getLatestVersion());
        resourceToChecksumMap.remove(resourceId);
    }
    
    public synchronized String getResourceId(Version version) {
        return versionToResourceMap.get(version);
    }
    
    public synchronized String getResourceId() {
        return versionToResourceMap.get(getLatestVersion());
    }
    
    public synchronized Checksum getChecksum(Version version) {
        String resourceId = getResourceId(version);
        return resourceToChecksumMap.get(resourceId);
    }
    
    public synchronized Checksum getChecksum() {
        return getChecksum(getLatestVersion());
    }
    
    public synchronized Set<Version> getVersionNumbers() {
        return versionToResourceMap.keySet();
    }
    
    public synchronized Version getLatestVersion() {
        return application.getLatestVersion();
    }
    
}
