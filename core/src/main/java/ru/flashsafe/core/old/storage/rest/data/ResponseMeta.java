package ru.flashsafe.core.old.storage.rest.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResponseMeta {
    
    @JsonProperty("code")
    private int responseCode;
    
    @JsonProperty("msg")
    private String responseMessage;
    
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

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
