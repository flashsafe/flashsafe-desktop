package ru.flashsafe.core.old.storage.rest;

import java.io.File;

import ru.flashsafe.core.storage.StorageOperationStatusImpl;

public class FileWithStatus {
    
    private final File file;
    
    private final StorageOperationStatusImpl status;
    
    public FileWithStatus(File file, StorageOperationStatusImpl status) {
        this.file = file;
        this.status = status;
    }

    public File getFile() {
        return file;
    }

    public StorageOperationStatusImpl getStatus() {
        return status;
    }

}
