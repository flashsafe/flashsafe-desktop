package ru.flashsafe.token.impl;

import ru.flashsafe.token.FlashSafeToken;
import ru.flashsafe.token.exception.FlashSafeTokenUnavailableException;

/**
 * 
 * 
 * @author Andrew
 *
 */
public class FlashSafePredefinedValueToken implements FlashSafeToken {

    private final String id;
    
    private final String value;
    
    private volatile boolean available = true;
    
    public FlashSafePredefinedValueToken(String tokenId, String value) {
        this.id = tokenId;
        this.value = value;
    }
    
    @Override
    public String getId() {
        return id;
    }

    @Override
    public String generateCode(String key) throws FlashSafeTokenUnavailableException {
        if (!isAvailable()) {
            throw new FlashSafeTokenUnavailableException("The token with Id = " + id + " is unavailable");
        }
        return value;
    }

    @Override
    public boolean isAvailable() {
        return available;
    }
    
    public synchronized void setAvailable(boolean available) {
        this.available = available;
    }

}
