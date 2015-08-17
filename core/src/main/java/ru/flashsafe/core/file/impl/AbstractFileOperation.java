package ru.flashsafe.core.file.impl;

import java.util.concurrent.Future;

import ru.flashsafe.core.file.FileOperation;
import ru.flashsafe.core.file.FileOperationType;
import ru.flashsafe.core.operation.AbstractOperation;
import ru.flashsafe.core.operation.OperationState;

public abstract class AbstractFileOperation extends AbstractOperation implements FileOperation {

    private final FileOperationType operationType;

    private final FileOperationInfo operationInfo;
    
    private long processedBytes;
    
    private Future<?> operationFuture;

    public AbstractFileOperation(long id, OperationState state, FileOperationType operationType, FileOperationInfo operationInfo) {
        super(id, state);
        this.operationType = operationType;
        this.operationInfo = operationInfo;
    }

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

    public void setProcessedBytes(long processedBytes) {
        this.processedBytes = processedBytes;
        int progress = (int) ((processedBytes * 100) /  getTotalBytes());
        setProgress(progress);
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
