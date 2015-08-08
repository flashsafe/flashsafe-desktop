package ru.flashsafe.core.storage;

import ru.flashsafe.core.operation.OperationStatus;

public interface StorageOperationStatus extends OperationStatus {
    
    StorageOperationType getOperationType();
    
    long getProcessedBytes();
    
    void markAsFinished();
    
    void waitUntilFinished() throws InterruptedException;

}
