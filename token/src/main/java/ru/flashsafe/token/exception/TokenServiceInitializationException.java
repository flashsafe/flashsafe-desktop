package ru.flashsafe.token.exception;

public class TokenServiceInitializationException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 5417509049024355779L;
    
    public TokenServiceInitializationException() {
    }
    
    public TokenServiceInitializationException(String message) {
        super(message);
    }
    
    public TokenServiceInitializationException(String message, Throwable e) {
        super(message, e);
    }

}
