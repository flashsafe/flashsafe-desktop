package ru.flashsafe.token;

/**
 * 
 * 
 * @author Andrew
 *
 */
public interface FlashSafeToken {

    String getId();
    
    /**
     * Generates code for defined key.
     * 
     * @param key
     * @return
     */
    String generateCode(String key);
    
    boolean isAvailable();
}
