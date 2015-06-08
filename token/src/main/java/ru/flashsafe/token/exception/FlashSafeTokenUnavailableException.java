package ru.flashsafe.token.exception;

/**
 * 
 * 
 * @author Andrew
 *
 */
public class FlashSafeTokenUnavailableException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -8630709871752006338L;
    
    public FlashSafeTokenUnavailableException() {
    }
    
    public FlashSafeTokenUnavailableException(String message) {
        super(message);
    }

}
