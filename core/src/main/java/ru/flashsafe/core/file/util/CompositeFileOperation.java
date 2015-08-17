package ru.flashsafe.core.file.util;

import ru.flashsafe.core.file.FileOperation;
import ru.flashsafe.core.file.FileOperationType;
import ru.flashsafe.core.file.impl.AbstractFileOperation;
import ru.flashsafe.core.file.impl.FileOperationInfo;

/**
 * 
 * 
 * @author Andrew
 *
 */
public class CompositeFileOperation extends AbstractFileOperation {

    private long totalBytes;
    
    private final FileOperationInfo originalOperationInfo;
    
    private FileOperation currentFileOperation; 
    
    private String previousFileOperationObject;
    
    public CompositeFileOperation(long id, FileOperationType operationType, FileOperationInfo operationInfo) {
        super(id, operationType, operationInfo);
        originalOperationInfo = operationInfo;
    }
    
    public FileOperationInfo getOriginalOperationInfo() {
        return originalOperationInfo;
    }

    @Override
    public long getTotalBytes() {
        return totalBytes;
    }
    
    /**
     * 
     * 
     * @param totalBytes number of bytes that have to be processed
     */
    public void setTotalBytes(long totalBytes) {
        this.totalBytes = totalBytes;
    }

    @Override
    public String getFileObjectName() {
        if (currentFileOperation == null) {
            return previousFileOperationObject == null ? super.getFileObjectName() : previousFileOperationObject;
        }
        return currentFileOperation.getFileObjectName();
    }
    
    public synchronized void setCurrentOperation(FileOperation operation) {
        if (currentFileOperation != null) {
            throw new IllegalStateException("Previous operation was not submitted"); 
        }
        currentFileOperation = operation;
        previousFileOperationObject = currentFileOperation.getFileObjectName();
    }
    
    public synchronized void submitCurrentOperationAsFinished() {
        if (currentFileOperation == null) {
            throw new IllegalStateException("No active operation to submit");
        }
        long currentTotal = getProcessedBytes() + currentFileOperation.getProcessedBytes();
        setProcessedBytes(currentTotal);
        currentFileOperation = null;
    }
    
    public void clearCurrentOperation() {
        currentFileOperation = null;
    }

}
