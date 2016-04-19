package ru.flashsafe.application.model;

import ru.flashsafe.common.fv.Algorithms;
import ru.flashsafe.common.fv.checksum.Checksum;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = JsonChecksum.Builder.class)
public class JsonChecksum extends Checksum {

    public JsonChecksum(Algorithms algorithm, String checksumValue) {
        super(algorithm, checksumValue);
    }

    @JsonPOJOBuilder(buildMethodName = "build", withPrefix = "set")
    public static class Builder {

        private Algorithms algorithm;

        private String checksumValue;

        public void setAlgorithm(Algorithms algorithm) {
            this.algorithm = algorithm;
        }

        public void setChecksumValue(String checksumValue) {
            this.checksumValue = checksumValue;
        }

        public JsonChecksum build() {
            return new JsonChecksum(algorithm, checksumValue);
        }
    }
}
