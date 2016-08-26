package ru.flashsafe.core.old.storage.rest.data;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Alexander Krysin
 */
public class UploadResponse {
    @JsonProperty("meta")
    private ResponseMeta responseMeta;
    
    public ResponseMeta getResponseMeta() {
        return responseMeta;
    }
    
}
