package ru.flashsafe.common.application;

import java.nio.file.Path;

import ru.flashsafe.common.version.Version;
import static java.util.Objects.*;

public class InstalledApplication extends Application {

    private final Version currentVersion;

    private final Path applicationRootPath;
    
    private final ApplicationDescriptor applicationDescriptor;

    public InstalledApplication(String applicationName, Version currentVersion, Path applicationRootPath,
            ApplicationDescriptor applicationDescriptor) {
        super(applicationName);
        this.currentVersion = requireNonNull(currentVersion);
        this.applicationRootPath = requireNonNull(applicationRootPath);
        this.applicationDescriptor = requireNonNull(applicationDescriptor);
    }

    public InstalledApplication(Application application, Version currentVersion, Path applicationRootPath,
            ApplicationDescriptor applicationDescriptor) {
        this(application.getName(), currentVersion, applicationRootPath, applicationDescriptor);
    }

    public Version getCurrentVersion() {
        return currentVersion;
    }

    public Path getApplicationRootPath() {
        return applicationRootPath;
    }

    public ApplicationDescriptor getApplicationDescriptor() {
        return applicationDescriptor;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((applicationRootPath == null) ? 0 : applicationRootPath.hashCode());
        result = prime * result + ((currentVersion == null) ? 0 : currentVersion.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        InstalledApplication other = (InstalledApplication) obj;
        if (applicationRootPath == null) {
            if (other.applicationRootPath != null)
                return false;
        } else if (!applicationRootPath.equals(other.applicationRootPath))
            return false;
        if (currentVersion == null) {
            if (other.currentVersion != null)
                return false;
        } else if (!currentVersion.equals(other.currentVersion))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "InstalledApplication [currentVersion=" + currentVersion + ", applicationRootPath=" + applicationRootPath + "]";
    }
}
