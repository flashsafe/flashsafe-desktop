package ru.flashsafe.core.old.storage.rest.data;

import java.io.IOException;

import ru.flashsafe.core.json.JSONHelper;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class AuthResponse {
    
    @JsonProperty("meta")
    private ResponseMeta responseMeta;
    
    @JsonProperty("data")
    private AuthData authData;
    
    public ResponseMeta getResponseMeta() {
        return responseMeta;
    }

    public AuthData getAuthData() {
        return authData;
    }

    @JsonCreator
    public static AuthResponse fromJSON(String jsonString) throws JsonParseException, JsonMappingException, IOException {
        return JSONHelper.fromJson(jsonString, AuthResponse.class);
    }
    
}
