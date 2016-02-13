package ru.flashsafe.core.operation;

/**
 * An operation.
 * 
 * @author Andrew
 *
 */
public interface Operation {

    /**
     * @return identifier
     */
    long getId();
    
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
    
    /**
     * Stops the operation execution.
     */
    void stop();
    
}
