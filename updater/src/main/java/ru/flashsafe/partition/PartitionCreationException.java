package ru.flashsafe.partition;

public class PartitionCreationException extends RuntimeException {

    private static final long serialVersionUID = -1948339971557719477L;
    
    public PartitionCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public PartitionCreationException(Throwable cause) {
        super(cause);
    }
    
}
