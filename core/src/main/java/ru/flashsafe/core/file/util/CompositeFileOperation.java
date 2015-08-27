package ru.flashsafe.core.file.util;

import ru.flashsafe.core.file.FileOperation;
import ru.flashsafe.core.file.FileOperationType;
import ru.flashsafe.core.file.impl.AbstractFileOperation;
import ru.flashsafe.core.file.impl.FileOperationInfo;

/**
 * A file operation object used to work with composite file objects (directories).
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
    
    /**
     * @return the root object's information of this operation.
     */
    public FileOperationInfo getOriginalOperationInfo() {
        return originalOperationInfo;
    }

    @Override
    public long getTotalBytes() {
        return totalBytes;
    }
    
    /**
     * Sets totalBytes value.
     * 
     * @param totalBytes number of bytes that have to be processed
     */
    public void setTotalBytes(long totalBytes) {
        this.totalBytes = totalBytes;
    }

    /**
     * @return name of fileObject of current sub-operation.
     */
    @Override
    public String getFileObjectName() {
        if (currentFileOperation == null) {
            return previousFileOperationObject == null ? super.getFileObjectName() : previousFileOperationObject;
        }
        return currentFileOperation.getFileObjectName();
    }
    
    @Override
    public int getProgress() {
        if (currentFileOperation == null) {
            return super.getProgress();
        }
        long processedBytes = getProcessedBytes() + currentFileOperation.getProcessedBytes();
        int progress = (int) (( processedBytes * 100) /  getTotalBytes());
        return progress;
    }
    
    /**
     * Sets current sub-operation.
     * 
     * @param operation sub-operation
     */
    public synchronized void setCurrentOperation(FileOperation operation) {
        if (currentFileOperation != null) {
            throw new IllegalStateException("Previous operation was not submitted"); 
        }
        currentFileOperation = operation;
        previousFileOperationObject = currentFileOperation.getFileObjectName();
    }
    
    /**
     * Submits current sub operation as finished. Call of this method does not affect to sub-operation object.
     * It only marks current sub-operation as finished for this composite operation and actualize it state.
     * 
     * @throws IllegalStateException if there is no active operation to submit
     */
    public synchronized void submitCurrentOperationAsFinished() throws IllegalStateException {
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
