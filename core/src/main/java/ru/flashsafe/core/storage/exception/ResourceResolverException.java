package ru.flashsafe.core.storage.exception;

public class ResourceResolverException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 2447195971488778738L;    
    
    public ResourceResolverException() {
        super();
    }

    public ResourceResolverException(String message) {
        super(message);
    }

    public ResourceResolverException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceResolverException(Throwable cause) {
        super(cause);
    }
    
}
