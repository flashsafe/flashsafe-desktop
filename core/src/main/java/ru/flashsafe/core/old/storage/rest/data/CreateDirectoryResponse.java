package ru.flashsafe.core.old.storage.rest.data;

import java.io.IOException;

import ru.flashsafe.core.json.JSONHelper;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class CreateDirectoryResponse {

    @JsonProperty("status")
    private String status;
    
    @JsonProperty("response")
    private String hash;
    
    @JsonProperty("result")
    private String result;
    
    @JsonProperty("code")
    private int code;
    
    public String getStatus() {
        return status;
    }
    
    public String getHash() {
        return hash;
    }
    
    public String getResult() {
        return result;
    }
    
    public int getCode() {
        return code;
    }

    @JsonCreator
    public static CreateDirectoryResponse fromJSON(String jsonString) throws JsonParseException, JsonMappingException, IOException {
        return JSONHelper.fromJson(jsonString, CreateDirectoryResponse.class);
    }
}
