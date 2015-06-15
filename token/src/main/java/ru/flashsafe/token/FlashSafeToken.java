package ru.flashsafe.token;

import ru.flashsafe.token.exception.CodeGenerationException;
import ru.flashsafe.token.exception.FlashSafeTokenUnavailableException;
import ru.flashsafe.token.service.FlashSafeTokenService;

/**
 * A FlashSafeToken object. A {@code FlashSafeToken} object provides
 * functionality for generating codes for keys.
 * 
 * During program execution, the token could become unavailable due to various
 * reasons: detaching from USB, some error occures in OS or drivers, etc. In
 * these cases the token becomes unavailable and can not generate codes. You should
 * use {@link #isAvailable()} method to check if token is available. If the token becomes
 * unavailable you can not use it anymore - use {@link FlashSafeTokenService#lookup()} to
 * get new representation of the token.
 * 
 * An implementation of this interface has to be thread-safe.
 * 
 * @author Andrew
 * 
 */
public interface FlashSafeToken {

    String UNDEFINED_TOKEN_ID = "UNDEFINED_TOKEN_ID";
    
    /**
     * @return the token's identifier
     */
    String getId();

    /**
     * Generates code for defined key.
     * 
     * @param key
     *            the key
     * @return the code generated for the {@code key}
     * @throws CodeGenerationException
     * @throws FlashSafeTokenUnavailableException if the token became 
     */
    String generateCode(String key) throws CodeGenerationException, FlashSafeTokenUnavailableException;

    /**
     * Checks if the token is available and ready to use.
     * 
     * @return {@code true} if token is available and ready to use;
     *         {@code false} otherwise
     */
    boolean isAvailable();
}
