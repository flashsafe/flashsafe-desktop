package ru.flashsafe.core.old.storage.rest.data;

import java.io.IOException;
import java.util.List;

import ru.flashsafe.core.json.JSONHelper;
import ru.flashsafe.core.old.storage.FlashSafeStorageFileObject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class TreeResponse {

    @JsonProperty("status")
    private String status;
    
    @JsonProperty("response")
    private List<FlashSafeStorageFileObject> fileObjects;
    

    public List<FlashSafeStorageFileObject> getFileObjects() {
        return fileObjects;
    }
    
    @JsonCreator
    public static TreeResponse fromJSON(String jsonString) throws JsonParseException, JsonMappingException, IOException {
        return JSONHelper.fromJson(jsonString, TreeResponse.class);
    }
    
}
