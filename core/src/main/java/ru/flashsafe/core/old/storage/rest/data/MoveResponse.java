package ru.flashsafe.core.old.storage.rest.data;

import java.io.IOException;

import ru.flashsafe.core.json.JSONHelper;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class MoveResponse {

    @JsonProperty("meta")
    private MoveResponseMeta responseMeta;
    
    public MoveResponseMeta getResponseMeta() {
        return responseMeta;
    }

    @JsonCreator
    public static MoveResponse fromJSON(String jsonString) throws JsonParseException, JsonMappingException, IOException {
        return JSONHelper.fromJson(jsonString, MoveResponse.class);
    }

    public static final class MoveResponseMeta extends ResponseMeta {
        
        @JsonProperty("new_dir_id")
        private long newDirectoryId;

        public long getNewDirectoryId() {
            return newDirectoryId;
        }
    }
}
