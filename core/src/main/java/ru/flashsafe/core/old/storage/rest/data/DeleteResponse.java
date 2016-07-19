package ru.flashsafe.core.old.storage.rest.data;

import java.io.IOException;

import ru.flashsafe.core.json.JSONHelper;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class DeleteResponse {

    @JsonProperty("meta")
    private DeleteResponseMeta responseMeta;
    
    public DeleteResponseMeta getResponseMeta() {
        return responseMeta;
    }

    @JsonCreator
    public static DeleteResponse fromJSON(String jsonString) throws JsonParseException, JsonMappingException, IOException {
        return JSONHelper.fromJson(jsonString, DeleteResponse.class);
    }

    public static final class DeleteResponseMeta extends ResponseMeta {
        
        @JsonProperty("deleted_id")
        private long deletedId;

        public long getNewDirectoryId() {
            return deletedId;
        }
    }
}
