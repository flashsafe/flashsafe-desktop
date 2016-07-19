package ru.flashsafe.core.old.storage.rest.data;

import java.io.IOException;

import ru.flashsafe.core.json.JSONHelper;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class RenameResponse {

    @JsonProperty("meta")
    private RenameResponseMeta responseMeta;
    
    public RenameResponseMeta getResponseMeta() {
        return responseMeta;
    }

    @JsonCreator
    public static RenameResponse fromJSON(String jsonString) throws JsonParseException, JsonMappingException, IOException {
        return JSONHelper.fromJson(jsonString, RenameResponse.class);
    }

    public static final class RenameResponseMeta extends ResponseMeta {
        
        @JsonProperty("new_name")
        private String newName;

        public String getNewDirectoryId() {
            return newName;
        }
    }
}
