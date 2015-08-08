package ru.flashsafe.core.storage.exception;

public class FlashSafeStorageException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 5414482843359769760L;

    public FlashSafeStorageException(String message, Throwable cause) {
        super(message, cause);
    }

    public FlashSafeStorageException(Throwable cause) {
        super(cause);
    }
    
}
