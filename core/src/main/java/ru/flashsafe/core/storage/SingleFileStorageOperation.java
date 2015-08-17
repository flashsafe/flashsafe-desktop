package ru.flashsafe.core.storage;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ru.flashsafe.core.file.FileOperationType;
import ru.flashsafe.core.file.impl.FileOperationInfo;
import ru.flashsafe.core.file.util.SingleFileOperation;
import ru.flashsafe.core.operation.OperationState;

public class SingleFileStorageOperation extends SingleFileOperation implements StorageFileOperation {

    private final StorageOperationType storageOperationType;
    
    private final Lock lock = new ReentrantLock();
    
    private final Condition isOperationFinished = lock.newCondition();
    
    public SingleFileStorageOperation(long id, FileOperationType operationType, StorageOperationType storageOperationType,
            FileOperationInfo operationInfo, long size) {
        super(id, operationType, operationInfo, size);
        this.storageOperationType = storageOperationType;
    }

    @Override
    public StorageOperationType getStorageOperationType() {
        return storageOperationType;
    }
    
    @Override
    public void markAsFinished() {
        lock.lock();
        try {
            isOperationFinished.signalAll();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void waitUntilFinished() throws InterruptedException {
        lock.lock();
        try {
            if (getState() == OperationState.FINISHED) {
                return;
            }
            isOperationFinished.await();
        } finally {
            lock.unlock();
        }
    }

}
