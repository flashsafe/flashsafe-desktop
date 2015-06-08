package ru.flashsafe.token.impl;

import ru.flashsafe.token.FlashSafeToken;
import ru.flashsafe.token.exception.FlashSafeTokenUnavailableException;

/**
 * Test implementation of the FlashSafeToken interface. This implementation of
 * token uses predefined code value which returns on each {@link #generateCode(String)} call.
 * 
 * Use {@link #setUnavailable()} method to make it unavailable to generate codes.
 * 
 * @author Andrew
 * 
 */
public class FlashSafePredefinedValueToken implements FlashSafeToken {

    private final String id;
    
    private final String code;
    
    private volatile boolean available = true;
    
    /**
     * Constructs a token with provided {@code tokenId} and {@code code}.
     * 
     * @param tokenId the token's id
     * @param code the code to return from {@link #generateCode(String)}
     */
    public FlashSafePredefinedValueToken(String tokenId, String code) {
        this.id = tokenId;
        this.code = code;
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
        return code;
    }

    @Override
    public boolean isAvailable() {
        return available;
    }
    
    /**
     * Makes the token unavailable to generate codes.
     */
    public void setUnavailable() {
        available = false;
    }

}
