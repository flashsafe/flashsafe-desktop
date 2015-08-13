package ru.flashsafe.core.operation;

public interface OperationStatus {

    OperationState getState();
    
    OperationResult getResult();

    int getProgress();

}
