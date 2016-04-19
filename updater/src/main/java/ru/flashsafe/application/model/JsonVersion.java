package ru.flashsafe.application.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import ru.flashsafe.common.version.Version;

@JsonDeserialize(builder = JsonVersion.Builder.class)
public class JsonVersion extends Version {

    public JsonVersion(int majorVersion, int minorVersion, int patchVersion) {
        super(majorVersion, minorVersion, patchVersion);
    }

    @JsonPOJOBuilder(buildMethodName = "build", withPrefix = "set")
    public static class Builder {

        private int majorVersion;

        private int minorVersion;

        private int patchVersion;

        public void setMajorVersion(int majorVersion) {
            this.majorVersion = majorVersion;
        }

        public void setMinorVersion(int minorVersion) {
            this.minorVersion = minorVersion;
        }

        public void setPatchVersion(int patchVersion) {
            this.patchVersion = patchVersion;
        }

        public JsonVersion build() {
            return new JsonVersion(majorVersion, minorVersion, patchVersion);
        }

    }

}
