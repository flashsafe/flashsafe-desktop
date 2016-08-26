package ru.flashsafe.core.old.storage;

import ru.flashsafe.core.file.FileObject;
import ru.flashsafe.core.file.FileObjectType;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "objectType")
@JsonSubTypes({ @JsonSubTypes.Type(value = FlashSafeStorageFile.class),
        @JsonSubTypes.Type(value = FlashSafeStorageDirectory.class) })
public abstract class FlashSafeStorageFileObject implements FileObject {

    @JsonProperty("objectHash")
    private String objectHash;
    
    @JsonProperty("parentHash")
    private String parentHash;

    private FileObjectType objectType;
    
    @JsonProperty("objectName")
    private String objectName;
   
    @JsonProperty("extension")
    private String extension;

    @JsonProperty("size")
    private long size;
    
    private String mimeType;

    public String getObjectHash() {
        return objectHash;
    }

    public void setObjectHash(String objectHash) {
        this.objectHash = objectHash;
    }

    public String getParentHash() {
        return parentHash;
    }

    public void setParentHash(String parentHash) {
        this.parentHash = parentHash;
    }
    
    @Override
    public FileObjectType getType() {
        return objectType;
    }

    public void setType(FileObjectType objectType) {
        this.objectType = objectType;
    }
    
    public String getName() {
        return objectName;
    }
    
    public void setName(String name) {
        this.objectName = name;
    }

    public String getExt() {
        return extension;
    }
    
    public void setExt(String ext) {
        this.extension = ext;
    }
    
    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
    
    public String getMimeType() {
        return mimeType;
    }
    
    public void setMimeType(String mimeType) {
        this.mimeType= mimeType;
    }
    
}
