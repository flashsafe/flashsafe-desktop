package ru.flashsafe.core.old.storage;

import ru.flashsafe.core.file.FileObject;
import ru.flashsafe.core.file.FileObjectType;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @JsonSubTypes.Type(value = FlashSafeStorageFile.class),
        @JsonSubTypes.Type(value = FlashSafeStorageDirectory.class) })
public abstract class FlashSafeStorageFileObject implements FileObject {

    private long id;

    private String name;

    private long size;
    
    private FileObjectType objectType;
    
    private String absolutePath;

    @JsonProperty("create_time")
    private long creationTime;

    @JsonProperty("update_time")
    private long lastModifiedTime;

    @JsonProperty("pincode")
    private boolean needPassword;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
    
    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public long getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(long lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public boolean isNeedPassword() {
        return needPassword;
    }

    public void setNeedPassword(boolean needPassword) {
        this.needPassword = needPassword;
    }

    public FileObjectType getType() {
        return objectType;
    }

    public void setType(FileObjectType objectType) {
        this.objectType = objectType;
    }
    
    public String getAbsolutePath() {
        return absolutePath;
    }
    
    public void setAbsolutePath(String path) {
        absolutePath = path;
    }
}
