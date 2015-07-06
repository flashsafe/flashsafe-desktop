package ru.flashsafe.core.old.storage;

import com.fasterxml.jackson.annotation.JsonTypeName;

import ru.flashsafe.core.file.File;

@JsonTypeName("file")
public class FlashSafeStorageFile extends FlashSafeStorageFileObject implements File {

    private String format;

    @Deprecated
    private int count;
    
    @Override
    public String getAbsolutePath() {
        return null;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
    
    
}
