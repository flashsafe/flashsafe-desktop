package ru.flashsafe.core.old.storage;

import ru.flashsafe.core.file.File;
import ru.flashsafe.core.file.FileObjectType;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("file")
public class FlashSafeStorageFile extends FlashSafeStorageFileObject implements File {

    private String format;

    @Deprecated
    private int count;
    
    public FlashSafeStorageFile() {
        setType(FileObjectType.FILE);
    }
    
    @Override
    public String getAbsolutePath() {
        return super.getAbsolutePath();
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

    @Override
    public String getFileFormat() {
        return format;
    }
    
    
}
