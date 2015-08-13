package ru.flashsafe.core.storage;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ru.flashsafe.core.operation.OperationResult;
import ru.flashsafe.core.operation.OperationState;

public class StorageOperationStatusImpl implements StorageOperationStatus {

    private final long id;
    
    private final long lenght;
    
    private final StorageOperationType operationType;
    
    private volatile OperationState state = OperationState.PLANNED;
    
    private OperationResult operationResult = null;
    
    private final Lock lock = new ReentrantLock();
    
    private final Condition isOperationFinished = lock.newCondition();
    
    private AtomicLong processedBytes = new AtomicLong(0);
    
    public StorageOperationStatusImpl(long id, StorageOperationType operationType, long lenght) {
        this.id = id;
        this.operationType = operationType;
        this.lenght = lenght;
    }
    
    public void setState(OperationState state) {
        this.state = state;
    }

    @Override
    public OperationState getState() {
        return state;
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
        return (int) ((processedBytes.longValue() * 100) / lenght );
    }
    
    public long getId() {
        return id;
    }
    
    @Override
    public long getProcessedBytes() {
        return processedBytes.longValue();
    }
    
    public void incrementProcessedBytes(long count) {
        processedBytes.addAndGet(count);
    }

    @Override
    public StorageOperationType getOperationType() {
        return operationType;
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
            if (state == OperationState.FINISHED) {
                return;
            }
            isOperationFinished.await();
        } finally {
            lock.unlock();
        }
    }

}
