package ru.flashsafe.core.storage;

import ru.flashsafe.core.file.File;

public class FlashSafeStorageFile extends FlashSafeStorageFileObject implements File {

    private String format;
    
    @Override
    public String getAbsolutePath() {
        return null;
    }

}
