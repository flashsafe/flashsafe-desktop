package ru.flashsafe.core.file.impl;

import java.util.concurrent.Future;

import ru.flashsafe.core.file.FileOperation;
import ru.flashsafe.core.file.FileOperationType;
import ru.flashsafe.core.operation.AbstractOperation;
import ru.flashsafe.core.operation.OperationState;

/**
 * Base functionality of a file operation.
 * 
 * @author Andrew
 *
 */
public abstract class AbstractFileOperation extends AbstractOperation implements FileOperation {

    private final FileOperationType operationType;

    private final FileOperationInfo operationInfo;
    
    private long processedBytes;
    
    private Future<?> operationFuture;

    /**
     * @param id
     *            operation id
     * @param state
     *            initial state of operation
     * @param operationType
     *            type of file operation
     * @param operationInfo
     *            operation info. See {@link FileOperationInfo} for details
     */
    public AbstractFileOperation(long id, OperationState state, FileOperationType operationType, FileOperationInfo operationInfo) {
        super(id, state);
        this.operationType = operationType;
        this.operationInfo = operationInfo;
    }

    /**
    * Creates {@code AbstractFileOperation} using {@link OperationState.CREATED} as initial state.
    * 
    * @param id
    *            operation id
    * @param operationType
    *            type of file operation
    * @param operationInfo
    *            operation info. See {@link FileOperationInfo} for details
    */
    public AbstractFileOperation(long id, FileOperationType operationType, FileOperationInfo operationInfo) {
        super(id);
        this.operationType = operationType;
        this.operationInfo = operationInfo;
    }

    @Override
    public FileOperationType getOperationType() {
        return operationType;
    }
    
    @Override
    public String getSourcePath() {
        return operationInfo.getSource();
    }
    
    @Override
    public String getDestinationPath() {
        return operationInfo.getDestination();
    }
    
    @Override
    public String getFileObjectName() {
        return operationInfo.getFileObjectName();
    }

    @Override
    public long getProcessedBytes() {
        return processedBytes;
    }
    
    /**
     * This method is not allowed to use with {@link FileOperations} implementations.
     * The progress for such operations calculated automatically while setting processedBytes value via {@link #setProcessedBytes(long)}
     */
    @Override
    public void setProgress(int progress) {
        throw new IllegalStateException("Can not set progress value of file operation directly");
    }

    /**
     * Sets processedBytes value to operation
     * 
     * @param processedBytes processedBytes value
     */
    public void setProcessedBytes(long processedBytes) {
        this.processedBytes = processedBytes;
        int progress = (int) ((processedBytes * 100) /  getTotalBytes());
        super.setProgress(progress);
    }
    
    public void setOperationFuture(Future<?> operationFuture) {
        this.operationFuture = operationFuture;
    }

    @Override
    public void stop() {
        if (operationFuture == null) {
            throw new IllegalStateException("Can not stop operation. Operation's future was not set");
        }
        operationFuture.cancel(true);
    }

}
