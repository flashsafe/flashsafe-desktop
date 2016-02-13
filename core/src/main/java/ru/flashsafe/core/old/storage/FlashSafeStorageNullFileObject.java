package ru.flashsafe.core.old.storage;

import ru.flashsafe.core.file.FileObjectType;

public class FlashSafeStorageNullFileObject extends FlashSafeStorageFileObject {

    public static final FlashSafeStorageNullFileObject NULL_OBJECT = new FlashSafeStorageNullFileObject();
    
    private FlashSafeStorageNullFileObject() {
    }
    
    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getAbsolutePath() {
        return null;
    }

    @Override
    public long getSize() {
        return 0;
    }

    @Override
    public long getCreationTime() {
        return 0;
    }

    @Override
    public long getLastModifiedTime() {
        return 0;
    }

    @Override
    public FileObjectType getType() {
        return null;
    }

}
