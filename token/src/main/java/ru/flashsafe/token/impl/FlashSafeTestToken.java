package ru.flashsafe.token.impl;

import ru.flashsafe.token.FlashSafeToken;
import ru.flashsafe.token.exception.CodeGenerationException;
import ru.flashsafe.token.exception.FlashSafeTokenUnavailableException;
import ru.flashsafe.token.generator.CodeGenerationStrategy;

/**
 * Test implementation of the FlashSafeToken interface. This implementation
 * allows to test a client code without real FlashSafe device.
 * 
 * Use {@link #setUnavailable()} method to make it unavailable to generate
 * codes.
 * 
 * @author Andrew
 * 
 */
public class FlashSafeTestToken implements FlashSafeToken {

    private final String id;
    
    private final CodeGenerationStrategy codeGenerationStrategy;
    
    private volatile boolean available = true;
    
    /**
     * Constructs a token with provided {@code tokenId} and {@code code}.
     * 
     * @param tokenId the token's id
     * @param codeGenerationStrategy the code generation strategy 
     */
    public FlashSafeTestToken(String tokenId, CodeGenerationStrategy codeGenerationStrategy) {
        this.id = tokenId;
        this.codeGenerationStrategy = codeGenerationStrategy;
    }
    
    @Override
    public String getId() {
        return id;
    }

    @Override
    public String generateCode(String key) throws FlashSafeTokenUnavailableException, CodeGenerationException {
        if (!isAvailable()) {
            throw new FlashSafeTokenUnavailableException("The token with Id = " + id + " is unavailable");
        }
        return codeGenerationStrategy.generateCodeFor(key);
    }

    @Override
    public boolean isAvailable() {
        return available;
    }
    
    @Override
    public String toString() {
        return String.format("[id = %s, available = %b]", id, available);
    }
    
    /**
     * Makes the token unavailable to generate codes.
     */
    public void setUnavailable() {
        available = false;
    }

}
