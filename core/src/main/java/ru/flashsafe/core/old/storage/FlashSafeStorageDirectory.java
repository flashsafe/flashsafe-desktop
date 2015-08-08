package ru.flashsafe.core.old.storage;

import ru.flashsafe.core.file.Directory;
import ru.flashsafe.core.file.FileObjectType;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("dir")
public class FlashSafeStorageDirectory extends FlashSafeStorageFileObject implements Directory {

    private int count;
    
    @Deprecated
    private String format;

    public FlashSafeStorageDirectory() {
        setObjectType(FileObjectType.DIRECTORY);
    }
    
    @Override
    public String getAbsolutePath() {
        // TODO Auto-generated method stub
        return null;
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
    
    

}
