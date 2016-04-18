package ru.flashsafe.common.application;

import static java.util.Objects.requireNonNull;
import ru.flashsafe.common.version.Version;

public class Application {

    private final String name;

    private final Version latestVersion;

    private final String shortDescription;

    public Application(String applicationName, Version latestVersion, String shortDescription) {
        name = requireNonNull(applicationName);
        this.latestVersion = requireNonNull(latestVersion);
        this.shortDescription = requireNonNull(shortDescription);
    }

    public Application(String applicationName, Version latestVersion) {
        this(applicationName, latestVersion, "");
    }

    public Application(String applicationName, String shortDescription) {
        this(applicationName, Version.UNDEFINED, shortDescription);
    }

    public Application(String applicationName) {
        this(applicationName, Version.UNDEFINED);
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
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Application [name=" + name + ", latestVersion=" + latestVersion + ", shortDescription=" + shortDescription + "]";
    }

}
