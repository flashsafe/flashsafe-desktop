package ru.flashsafe.token.service.impl;

import java.io.IOException;

import ru.flashsafe.token.FlashSafeToken;
import ru.flashsafe.token.event.FlashSafeTokenEventHandler;
import ru.flashsafe.token.exception.FlashSafeTokenNotFoundException;
import ru.flashsafe.token.exception.TokenServiceInitializationException;
import ru.flashsafe.token.generator.CodeGenerationStrategy;
import ru.flashsafe.token.service.FlashSafeTokenService;

/**
 * 
 * 
 * @author Andrew
 *
 */
public class RemoteEmulatorTokenService implements FlashSafeTokenService {

    private static RemoteEmulatorTokenService instance; 
    
    private TestTokenService testTokenService = TestTokenService.getTokenService();
    
    private RemoteEmulatorMonitor remoteEmulatorMonitor;
    
    private RemoteEmulatorTokenService() {
    }
    
    public static RemoteEmulatorTokenService getTokenService() throws TokenServiceInitializationException {
        if (instance == null) {
            instance = new RemoteEmulatorTokenService();
            instance.initService();
        }
        return instance;
    }
    
    private void initService() throws TokenServiceInitializationException {
        try {
            remoteEmulatorMonitor = new RemoteEmulatorMonitor(4420, this);
            remoteEmulatorMonitor.start();
        } catch (IOException e) {
            throw new TokenServiceInitializationException("Failed to start emulator monitor",e);
        }
    }
    
    /**
     * Sets the code generation strategy
     * 
     * @param codeGenerationStrategy the strategy
     */
    public void setCodeGenerationStrategy(CodeGenerationStrategy codeGenerationStrategy) {
        testTokenService.setCodeGenerationStrategy(codeGenerationStrategy);
    }
    
    @Override
    public FlashSafeToken lookup(String tokenId) throws FlashSafeTokenNotFoundException {
        return testTokenService.lookup(tokenId);
    }

    @Override
    public void subscribeToEvents(String tokenId, FlashSafeTokenEventHandler handler) {
        testTokenService.subscribeToEvents(tokenId, handler);
    }

    @Override
    public void subscribeToEvents(FlashSafeToken token, FlashSafeTokenEventHandler handler) {
        subscribeToEvents(token.getId(), handler);
    }

    @Override
    public void unsubscribeFromEvents(String tokenId, FlashSafeTokenEventHandler handler) {
        testTokenService.unsubscribeFromEvents(tokenId, handler);
    }

    @Override
    public void unsubscribeFromEvents(FlashSafeToken token, FlashSafeTokenEventHandler handler) {
        unsubscribeFromEvents(token.getId(), handler);
    }
    
    protected synchronized void fireAttachEvent(String tokenId) {
        testTokenService.fireAttachEvent(tokenId);
    }
    
    protected synchronized void fireDetachEvent(String tokenId) {
        testTokenService.fireDetachEvent(tokenId);
    }

}
