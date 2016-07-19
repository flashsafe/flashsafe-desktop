package ru.flashsafe.core.old.storage.rest.data;

import java.io.IOException;

import ru.flashsafe.core.json.JSONHelper;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class CopyResponse {

    @JsonProperty("meta")
    private CopyResponseMeta responseMeta;
    
    public CopyResponseMeta getResponseMeta() {
        return responseMeta;
    }

    @JsonCreator
    public static CopyResponse fromJSON(String jsonString) throws JsonParseException, JsonMappingException, IOException {
        return JSONHelper.fromJson(jsonString, CopyResponse.class);
    }

    public static final class CopyResponseMeta extends ResponseMeta {
        
        @JsonProperty("target_dir_id")
        private long targetDirectoryId;

        public long getNewDirectoryId() {
            return targetDirectoryId;
        }
    }
}
