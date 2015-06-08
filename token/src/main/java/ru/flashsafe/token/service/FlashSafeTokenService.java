package ru.flashsafe.token.service;

import ru.flashsafe.token.FlashSafeToken;
import ru.flashsafe.token.event.FlashSafeTokenEventHandler;

/**
 * 
 * 
 * @author Andrew
 * 
 */
public interface FlashSafeTokenService {

    FlashSafeToken lookup(String tokenId);
    
    void subscribeToEvents(String tokenId, FlashSafeTokenEventHandler handler);
    
    void subscribeToEvents(FlashSafeToken token, FlashSafeTokenEventHandler handler);
    
    void unsubscribeFromEvents(String tokenId, FlashSafeTokenEventHandler handler);
    
    void unsubscribeFromEvents(FlashSafeToken token, FlashSafeTokenEventHandler handler);

}
