package ru.flashsafe.core.old.storage;

import com.fasterxml.jackson.annotation.JsonTypeName;

import ru.flashsafe.core.file.Directory;

@JsonTypeName("dir")
public class FlashSafeStorageDirectory extends FlashSafeStorageFileObject implements Directory {

    private int count;
    
    @Deprecated
    private String format;

    public FlashSafeStorageDirectory() {
        
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
