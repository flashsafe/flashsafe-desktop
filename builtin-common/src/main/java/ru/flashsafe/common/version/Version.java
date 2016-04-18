package ru.flashsafe.common.version;

/**
 * Represents the version information. The format based on this description:
 * {@link http://semver.org/spec/v2.0.0.html} (Semantic Versioning 2.0.0).
 *
 */
public class Version implements Comparable<Version> {
    
    private static final int UNDEFINED_VERSION_NUMBER = -1;
    
    public static final Version UNDEFINED = new Version(UNDEFINED_VERSION_NUMBER);

    private final int majorVersion;

    private final int minorVersion;

    private final int patchVersion;

    public Version(int majorVersion, int minorVersion, int patchVersion) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.patchVersion = patchVersion;
    }

    public Version(int majorVersion, int minorVersion) {
        this(majorVersion, minorVersion, 0);
    }

    public Version(int majorVersion) {
        this(majorVersion, 0);
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public int getMinorVersion() {
        return minorVersion;
    }

    public int getPatchVersion() {
        return patchVersion;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + majorVersion;
        result = prime * result + minorVersion;
        result = prime * result + patchVersion;
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
        Version other = (Version) obj;
        if (majorVersion != other.majorVersion)
            return false;
        if (minorVersion != other.minorVersion)
            return false;
        if (patchVersion != other.patchVersion)
            return false;
        return true;
    }

    @Override
    public int compareTo(Version version) {
        if (majorVersion != version.majorVersion) {
            return majorVersion > version.majorVersion ? 1 : -1;
        }
        if (minorVersion != version.minorVersion) {
            return minorVersion > version.minorVersion ? 1 : -1;
        }
        if (patchVersion != version.patchVersion) {
            return patchVersion > version.patchVersion ? 1 : -1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Version [majorVersion=" + majorVersion + ", minorVersion=" + minorVersion + ", patchVersion=" + patchVersion
                + "]";
    }

}
