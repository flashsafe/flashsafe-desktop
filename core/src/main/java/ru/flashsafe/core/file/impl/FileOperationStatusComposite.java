package ru.flashsafe.core.file.impl;

import ru.flashsafe.core.file.FileOperationStatus;
import ru.flashsafe.core.file.FileOperationType;
import ru.flashsafe.core.operation.OperationResult;
import ru.flashsafe.core.operation.OperationState;

public class FileOperationStatusComposite implements FileOperationStatus {

    private final FileOperationType operationType;
    
    private volatile long totalBytesToProcess;
    
    private volatile long processedBytes;
    
    private OperationState state = OperationState.PLANNED;
    
    private OperationResult operationResult = null;
    
    private FileOperationStatus activeOperationStatus;
    
    public FileOperationStatusComposite(FileOperationType operationType) {
        this.operationType = operationType;
    }
    
    public void setTotalBytesToProcess(long totalBytesToProcess) {
        this.totalBytesToProcess = totalBytesToProcess;
    }
    
    public synchronized void setActiveOperationStatus(FileOperationStatus operationStatus) {
        if (activeOperationStatus != null) {
            throw new IllegalStateException("Previous operation was not submitted"); 
        }
        activeOperationStatus = operationStatus;
    }
    
    public synchronized void submitActiveOperationStatusAsFinished() {
        if (activeOperationStatus == null) {
            throw new IllegalStateException("No active operation to submit"); 
        }
        processedBytes += activeOperationStatus.getProcessedBytes();
        activeOperationStatus = null;
    }

    @Override
    public OperationState getState() {
        return state;
    }

    public void setState(OperationState state) {
        this.state = state;
    }
    
    public void setResult(OperationResult operationResult) {
        this.operationResult = operationResult;
    }
    
    @Override
    public OperationResult getResult() {
        return operationResult;
    }

    @Override
    public int getProgress() {
        long totalProcessed = getProcessedBytes();
        long dividend = totalProcessed * 100;
        return (int) (totalBytesToProcess > 0 ? dividend / totalBytesToProcess : 0);
    }

    @Override
    public FileOperationType getOperationType() {
        return operationType;
    }

    @Override
    public long getProcessedBytes() {
        return getTotalProcessedBytes();
    }

    private long getTotalProcessedBytes() {
        return activeOperationStatus == null ? processedBytes : processedBytes + activeOperationStatus.getProcessedBytes();
    }
}

