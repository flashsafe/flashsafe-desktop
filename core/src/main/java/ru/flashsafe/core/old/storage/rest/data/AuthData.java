package ru.flashsafe.core.old.storage.rest.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthData {

    private long timestamp;
    
    private String token;
    
    private int uid;
    
    private String name;
    
    @JsonProperty("lastname")
    private String lastName;
    
    @JsonProperty("total_size")
    private long totalSize;
    
    @JsonProperty("used_size")
    private long usedSize;
    
    private int timeout;

    public long getTimestamp() {
        return timestamp;
    }

    public String getToken() {
        return token;
    }

    public int getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public long getUsedSize() {
        return usedSize;
    }

    public int getTimeout() {
        return timeout;
    }
}
