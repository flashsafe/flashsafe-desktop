package ru.flashsafe.application.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import ru.flashsafe.common.application.Application;
import ru.flashsafe.common.version.Version;

@JsonDeserialize(builder = JsonApplication.Builder.class)
public class JsonApplication extends Application {

    public JsonApplication(String id, String applicationName, Version latestVersion, String shortDescription) {
        super(id, applicationName, latestVersion, shortDescription);
    }

    @JsonPOJOBuilder(buildMethodName = "build", withPrefix = "set")
    public static class Builder {

        private String id;

        private String name;

        private JsonVersion latestVersion;

        private String shortDescription;

        public void setId(String id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setLatestVersion(JsonVersion latestVersion) {
            this.latestVersion = latestVersion;
        }

        public void setShortDescription(String shortDescription) {
            this.shortDescription = shortDescription;
        }

        public JsonApplication build() {
            return new JsonApplication(id, name, latestVersion, shortDescription);
        }

    }

}
