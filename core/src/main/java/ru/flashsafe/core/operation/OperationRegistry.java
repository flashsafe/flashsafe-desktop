package ru.flashsafe.core.operation;

public interface OperationRegistry {

    void registerOperation(Operation operation);
    
    void unregisterOperation(Operation operation);
    
}
