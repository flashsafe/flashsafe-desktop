package ru.flashsafe.token.event;

import ru.flashsafe.token.FlashSafeToken;

/**
 * 
 * 
 * @author Andrew
 *
 */
public abstract class BaseEventHandler implements FlashSafeTokenEventHandler {

    @Override
    public void handleEvent(FlashSafeTokenEvent event, FlashSafeToken flashSafeToken) {
        switch (event) {
        case ATTACHED:
            onAttach(flashSafeToken);
            break;
        case DETACHED:
            onDetach(flashSafeToken);
            break;
        default:
            break;
        }
    }
    
    protected abstract void onAttach(FlashSafeToken flashSafeToken);
    
    protected abstract void onDetach(FlashSafeToken flashSafeToken);

}
