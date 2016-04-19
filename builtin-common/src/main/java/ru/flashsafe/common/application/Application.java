package ru.flashsafe.common.application;

import static java.util.Objects.requireNonNull;
import ru.flashsafe.common.version.Version;

public class Application {
    
    private final String id;

    private final String name;

    private final Version latestVersion;

    private final String shortDescription;

    public Application(String id, String applicationName, Version latestVersion, String shortDescription) {
        this.id = requireNonNull(id);
        name = requireNonNull(applicationName);
        this.latestVersion = requireNonNull(latestVersion);
        this.shortDescription = requireNonNull(shortDescription);
    }

    public Application(String id, String applicationName, Version latestVersion) {
        this(id, applicationName, latestVersion, "");
    }

    public Application(String id, String applicationName, String shortDescription) {
        this(id, applicationName, Version.UNDEFINED, shortDescription);
    }

    public Application(String id, String applicationName) {
        this(id, applicationName, Version.UNDEFINED);
    }
    
    public Application(String id) {
        this(id, "", Version.UNDEFINED);
    }
    
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Version getLatestVersion() {
        return latestVersion;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Application other = (Application) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Application [id=" + id + ", name=" + name + ", latestVersion=" + latestVersion + ", shortDescription="
                + shortDescription + "]";
    }
}
