package ru.flashsafe.token.event;

import ru.flashsafe.token.FlashSafeToken;

public interface FlashSafeTokenEventHandler {

    void handleEvent(FlashSafeTokenEvent event, FlashSafeToken flashSafeToken);
    
}
