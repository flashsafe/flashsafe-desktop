package ru.flashsafe.core.operation;

/**
 * Base functionality of an operation.
 * 
 * @author Andrew
 *
 */
public abstract class AbstractOperation implements Operation {

    private final long id;
    
    private OperationState state;
    
    private OperationResult result = OperationResult.UNKNOWN;
    
    private int progress;
    
    /**
     * @param id operation id
     * @param state initial state of operation
     */
    protected AbstractOperation(long id, OperationState state) {
        this.id = id;
        this.state = state;
    }
    
    /**
     * Creates {@code AbstractOperation} using {@link OperationState.CREATED} as initial state.
     * 
     * @param id operation id
     */
    protected AbstractOperation(long id) {
        this(id, OperationState.CREATED);
    }
    
    @Override
    public long getId() {
        return id;
    }

    @Override
    public OperationState getState() {
        return state;
    }
    
    public void setState(OperationState state) {
        this.state = state;
    }

    @Override
    public OperationResult getResult() {
        return result;
    }
    
    public void setResult(OperationResult result) {
        this.result = result;
    }

    @Override
    public int getProgress() {
        return progress;
    }
    
    /**
     * Sets progress to operation.
     * 
     * @param progress
     * @throws IllegalStateException if progress value not in 0-100 range
     */
    public void setProgress(int progress) throws IllegalStateException {
        if (progress < 0 || progress > 100) {
            throw new IllegalStateException("The progress value must be between 0 and 100!");
        }
        this.progress = progress;
    }

    @Override
    public abstract void stop();
    
}
