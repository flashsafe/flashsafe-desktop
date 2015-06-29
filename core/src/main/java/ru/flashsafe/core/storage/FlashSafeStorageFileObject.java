package ru.flashsafe.core.storage;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FlashSafeStorageFileObject {

    private long id;

    private String name;

    private long size;

    private long creationTime;

    private long lastModifiedTime;

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

}
