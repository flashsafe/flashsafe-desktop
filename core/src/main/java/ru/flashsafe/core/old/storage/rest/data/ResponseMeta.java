package ru.flashsafe.core.old.storage.rest.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResponseMeta {
    
    @JsonProperty("code")
    private int responseCode;
    
    @JsonProperty("msg")
    private String responseMessage;
    
    @JsonProperty("file_hash")
    private String file_hash;
    
    private String info;

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getFileHash() {
        return file_hash;
    }
    
    public void setFilehash(String file_hash) {
        this.file_hash = file_hash;
    }
    
    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
