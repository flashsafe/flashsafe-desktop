package ru.flashsafe.token.service.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ru.flashsafe.token.FlashSafeToken;
import ru.flashsafe.token.event.FlashSafeTokenEvent;
import ru.flashsafe.token.event.FlashSafeTokenEventHandler;
import ru.flashsafe.token.service.FlashSafeTokenService;

/**
 * This class provides base functionality for any {@link FlashSafeTokenService}
 * implementation (subscription/cancel subscription events, fire event). Use
 * it to create a new {@link FlashSafeTokenService} implementation.
 * 
 * @author Andrew
 * 
 */
public abstract class FlashSafeTokenServiceBase implements FlashSafeTokenService {

    private final Map<String, List<FlashSafeTokenEventHandler>> tokenToHandlersMap = new HashMap<>();

    @Override
    public void subscribeToEvents(String tokenId, FlashSafeTokenEventHandler handler) {
        registerHandler(tokenId, handler);
    }

    @Override
    public void subscribeToEvents(FlashSafeToken token, FlashSafeTokenEventHandler handler) {
        subscribeToEvents(token.getId(), handler);
    }

    @Override
    public void unsubscribeFromEvents(String tokenId, FlashSafeTokenEventHandler handler) {
        unregisterHandler(tokenId, handler);
    }

    @Override
    public void unsubscribeFromEvents(FlashSafeToken token, FlashSafeTokenEventHandler handler) {
        unsubscribeFromEvents(token.getId(), handler);
    }
    
    protected synchronized void fireEvent(FlashSafeTokenEvent event, FlashSafeToken flashSafeToken) {
        List<FlashSafeTokenEventHandler> handlersForToken = tokenToHandlersMap.get(flashSafeToken.getId());
        if (handlersForToken == null) {
            return;
        }
        for (FlashSafeTokenEventHandler eventHandler : handlersForToken) {
            eventHandler.handleEvent(event, flashSafeToken);
        }
    }
    
    private synchronized void registerHandler(String tokenId, FlashSafeTokenEventHandler handler) {
        List<FlashSafeTokenEventHandler> handlersForToken = tokenToHandlersMap.get(tokenId);
        if (handlersForToken == null) {
            handlersForToken = new LinkedList<>();
            tokenToHandlersMap.put(tokenId, handlersForToken);
        }
        handlersForToken.add(handler);
    }
    
    private synchronized void unregisterHandler(String tokenId, FlashSafeTokenEventHandler handler) {
        List<FlashSafeTokenEventHandler> handlersForToken = tokenToHandlersMap.get(tokenId);
        if (handlersForToken != null) {
            handlersForToken.remove(handler);
        }
    }

}
