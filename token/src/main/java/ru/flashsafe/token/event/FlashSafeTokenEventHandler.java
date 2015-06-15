package ru.flashsafe.token.event;

import ru.flashsafe.token.FlashSafeToken;

/**
 * An object that represents handler for FlashSafeToken events.
 * 
 * @author Andrew
 *
 */
public interface FlashSafeTokenEventHandler {

    void handleEvent(FlashSafeTokenEvent event, FlashSafeToken flashSafeToken);
    
}
