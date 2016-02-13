package ru.flashsafe.token.service;

import ru.flashsafe.token.FlashSafeToken;
import ru.flashsafe.token.event.FlashSafeTokenEventHandler;
import ru.flashsafe.token.exception.FlashSafeTokenNotFoundException;

/**
 * An object that represents a service to work with {@link FlashSafeToken}.
 * An implementation of this interface has to be thread-safe.
 * 
 * @author Andrew
 * 
 */
public interface FlashSafeTokenService {
    
    /**
     * Retrieves the FlashSafeToken with specified identifier.
     * 
     * @param tokenId token's identifier
     * @return the FlashSafeToken instance
     * @throws FlashSafeTokenNotFoundException if the requested FlashSafeToken with {@code tokenId} was not found
     */
    FlashSafeToken lookup(String tokenId) throws FlashSafeTokenNotFoundException;
    
    /**
     * Subscribes the handler to events of specified token.
     * 
     * @param tokenId the token's identifier
     * @param handler the handler 
     */
    void subscribeToEvents(String tokenId, FlashSafeTokenEventHandler handler);
    
    /**
     * Subscribes the handler to events of specified token.
     * 
     * @param token the token
     * @param handler the handler 
     */
    void subscribeToEvents(FlashSafeToken token, FlashSafeTokenEventHandler handler);
    
    /**
     * Unsubscribes the handler from events of specified token.
     * 
     * @param tokenId the token's identifier
     * @param handler the handler 
     */
    void unsubscribeFromEvents(String tokenId, FlashSafeTokenEventHandler handler);
    
    /**
     * Unsubscribes the handler from events of specified token.
     * 
     * @param token the token
     * @param handler the handler 
     */
    void unsubscribeFromEvents(FlashSafeToken token, FlashSafeTokenEventHandler handler);

}
