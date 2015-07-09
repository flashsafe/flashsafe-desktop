package ru.flashsafe.core.storage;

import ru.flashsafe.core.file.FileOperationStatus;
import ru.flashsafe.core.file.FileOperationType;
import ru.flashsafe.core.operation.OperationState;

public class ToFileOperationStatusAdapter implements FileOperationStatus {

    private final StorageOperationStatus storageOperationStatus;
    
    private final FileOperationType fileOperationType;
    
    public ToFileOperationStatusAdapter(StorageOperationStatus storageOperationStatus, FileOperationType fileOperationType) {
        this.storageOperationStatus = storageOperationStatus;
        this.fileOperationType = fileOperationType;
    }
    
    @Override
    public OperationState getState() {
        return storageOperationStatus.getState();
    }

    @Override
    public int getProgress() {
        return storageOperationStatus.getProgress();
    }

    @Override
    public FileOperationType getOperationType() {
        return fileOperationType;
    }

    @Override
    public long getProcessedBytes() {
        return storageOperationStatus.getProcessedBytes();
    }

}
