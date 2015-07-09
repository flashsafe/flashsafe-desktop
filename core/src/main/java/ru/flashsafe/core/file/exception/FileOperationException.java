package ru.flashsafe.core.file.exception;

public class FileOperationException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 8077420000373239382L;
    
    public FileOperationException() {
        super();
    }

    public FileOperationException(String message) {
        super(message);
    }

    public FileOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileOperationException(Throwable cause) {
        super(cause);
    }

}
