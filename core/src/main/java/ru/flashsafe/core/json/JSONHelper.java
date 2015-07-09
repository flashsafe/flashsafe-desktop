package ru.flashsafe.core.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * 
 * @author Andrew
 *
 */
public class JSONHelper {

    private JSONHelper() {
    }
    
    public static <T> T fromJson(String jsonString, Class<? extends T> type) throws JsonParseException, JsonMappingException,
            IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonString, type);
    }
    
}
