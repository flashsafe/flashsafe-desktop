package ru.flashsafe.core.old.storage.rest.data;

import java.io.IOException;

import ru.flashsafe.core.json.JSONHelper;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class CreateDirectoryResponse {

    @JsonProperty("meta")
    private CreateDirectoryResponseMeta responseMeta;
    
    public CreateDirectoryResponseMeta getResponseMeta() {
        return responseMeta;
    }

    @JsonCreator
    public static CreateDirectoryResponse fromJSON(String jsonString) throws JsonParseException, JsonMappingException, IOException {
        return JSONHelper.fromJson(jsonString, CreateDirectoryResponse.class);
    }

    public static final class CreateDirectoryResponseMeta extends ResponseMeta {
        
        @JsonProperty("dir_id")
        private long directoryId;

        public long getDirectoryId() {
            return directoryId;
        }
    }
}
