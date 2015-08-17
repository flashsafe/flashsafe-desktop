package ru.flashsafe.core.operation;

/**
 * A set of possible operation's states.
 * 
 * @author Andrew
 *
 */
public enum OperationState {

    /**
     * An operation was created
     */
    CREATED,
    
    /**
     * An operation was planned to do but not executed yet
     */
    PLANNED,
    
    /**
     * An operation was executed and now processing now
     */
    IN_PROGRESS,
    
    /**
     * An operation execution process was finished 
     */
    FINISHED
    
}
