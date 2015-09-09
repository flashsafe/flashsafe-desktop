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
    
    @Override
    public String getAbsolutePath() {
        return absolutePath;
    }
    
    public void setAbsolutePath(String path) {
        absolutePath = path;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((absolutePath == null) ? 0 : absolutePath.hashCode());
        result = prime * result + (int) (creationTime ^ (creationTime >>> 32));
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + (int) (lastModifiedTime ^ (lastModifiedTime >>> 32));
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + (needPassword ? 1231 : 1237);
        result = prime * result + ((objectType == null) ? 0 : objectType.hashCode());
        result = prime * result + (int) (size ^ (size >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FlashSafeStorageFileObject other = (FlashSafeStorageFileObject) obj;
        if (absolutePath == null) {
            if (other.absolutePath != null)
                return false;
        } else if (!absolutePath.equals(other.absolutePath))
            return false;
        if (creationTime != other.creationTime)
            return false;
        if (id != other.id)
            return false;
        if (lastModifiedTime != other.lastModifiedTime)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (needPassword != other.needPassword)
            return false;
        if (objectType != other.objectType)
            return false;
        if (size != other.size)
            return false;
        return true;
    }
}
