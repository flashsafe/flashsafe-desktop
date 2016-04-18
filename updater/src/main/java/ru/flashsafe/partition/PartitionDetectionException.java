package ru.flashsafe.partition;

public class PartitionDetectionException extends RuntimeException {
    
    private static final long serialVersionUID = -8473507264686971378L;

    public enum Type {
        
        NO_PARTITIONS,
        
        TOO_MANY_PARTITIONS
        
    }
    
    private final Type errorType;
    
    public PartitionDetectionException() {
        errorType = null;
    }
    
    public PartitionDetectionException(String message) {
        super(message);
        errorType = null;
    }
    
    public PartitionDetectionException(String message, Type errorType) {
        super(message);
        this.errorType = errorType;
    }
    
    public PartitionDetectionException(Type errorType) {
        this.errorType = errorType;
    }

    public Type getErrorType() {
        return errorType;
    }
}
