package ru.flashsafe.partition;

import java.nio.file.Path;

public class PartitionDetectionException extends RuntimeException {
    
    private static final long serialVersionUID = -8473507264686971378L;

    public enum Type {
        
        NO_PARTITIONS,
        
        TOO_MANY_PARTITIONS
        
    }
    
    private final Type errorType;
    
    private final Path availableDriveForDataPartition;
    
    public PartitionDetectionException() {
        errorType = null;
        availableDriveForDataPartition = null;
    }
    
    public PartitionDetectionException(String message) {
        super(message);
        errorType = null;
        availableDriveForDataPartition = null;
    }
    
    public PartitionDetectionException(String message, Type errorType) {
        super(message);
        this.errorType = errorType;
        availableDriveForDataPartition = null;
    }
    
    public PartitionDetectionException(Type errorType) {
        this.errorType = errorType;
        availableDriveForDataPartition = null;
    }
    
    public PartitionDetectionException(Type errorType, Path availableDriveForDataPartition) {
        this.errorType = errorType;
        this.availableDriveForDataPartition = availableDriveForDataPartition;
    }

    public Type getErrorType() {
        return errorType;
    }

    public Path getAvailableDriveForDataPartition() {
        return availableDriveForDataPartition;
    }
}
