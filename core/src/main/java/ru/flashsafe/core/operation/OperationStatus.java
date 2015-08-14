package ru.flashsafe.core.operation;

/**
 * An object that represents status of some operation.
 * 
 * @author Andrew
 *
 */
public interface OperationStatus {

    /**
     * @return state of the operation
     */
    OperationState getState();
    
    /**
     * @return result of the operation
     */
    OperationResult getResult();

    /**
     * @return progress of the operations in percent (range from 0 to 100).
     */
    int getProgress();

}
