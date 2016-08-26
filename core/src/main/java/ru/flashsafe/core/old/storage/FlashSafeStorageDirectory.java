package ru.flashsafe.core.old.storage;

import ru.flashsafe.core.file.Directory;
import ru.flashsafe.core.file.FileObjectType;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("FOLDER")
public class FlashSafeStorageDirectory extends FlashSafeStorageFileObject implements Directory {

    private int count;
    
    @Deprecated
    private String format;

    public FlashSafeStorageDirectory() {
        setType(FileObjectType.DIRECTORY);
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    public String getHash() {
        return getObjectHash();
    }

}
