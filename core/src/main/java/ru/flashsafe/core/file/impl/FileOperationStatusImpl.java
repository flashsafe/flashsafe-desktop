package ru.flashsafe.core.file.impl;

import ru.flashsafe.core.file.FileOperationStatus;
import ru.flashsafe.core.file.FileOperationType;
import ru.flashsafe.core.operation.OperationState;

public class FileOperationStatusImpl implements FileOperationStatus {

    private final FileOperationType operationType;
    
    private final long fileSize;
    
    private volatile long processedBytes;
    
    private OperationState operationState;
    
    public FileOperationStatusImpl(FileOperationType operationType, long fileSize) {
        this.operationType = operationType;
        this.fileSize = fileSize;
    }
    
    @Override
    public OperationState getState() {
        return operationState;
    }
    
    public void setState(OperationState operationState) {
        this.operationState = operationState;
    }

    @Override
    public int getProgress() {
        return (int) ( (processedBytes * 100) /  fileSize);
    }

    @Override
    public FileOperationType getOperationType() {
        return operationType;
    }

    @Override
    public long getProcessedBytes() {
        return processedBytes;
    }
    
    public void setProcessedBytes(long processedBytes) {
        this.processedBytes = processedBytes;
    }

}
