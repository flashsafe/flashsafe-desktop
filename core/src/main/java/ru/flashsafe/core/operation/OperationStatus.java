package ru.flashsafe.core.operation;

public interface OperationStatus {

    OperationState getState();

    int getProgress();

}
