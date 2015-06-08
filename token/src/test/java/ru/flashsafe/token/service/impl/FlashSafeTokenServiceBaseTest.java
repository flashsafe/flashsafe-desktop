package ru.flashsafe.token.service.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import ru.flashsafe.token.FlashSafeToken;
import ru.flashsafe.token.event.FlashSafeTokenEvent;
import ru.flashsafe.token.event.FlashSafeTokenEventHandler;
import ru.flashsafe.token.exception.FlashSafeTokenNotFoundException;

public class FlashSafeTokenServiceBaseTest {
    
    private static final String TEST_TOKEN_ID = "TEST_TOKEN_ID";
    
    private FlashSafeTokenServiceBase tokenService;
    
    private FlashSafeTokenEventHandler testHandler;
    
    private FlashSafeToken testToken;
    
    @Before
    public void init() {
        tokenService = new FlashSafeTokenServiceBaseImpl();
        testHandler = mock(FlashSafeTokenEventHandler.class);
        testToken = mock(FlashSafeToken.class);
        when(testToken.getId()).thenReturn(TEST_TOKEN_ID);
    }
    
    @Test
    public void subscribeToEventsWithTokenId() {
        tokenService.subscribeToEvents(TEST_TOKEN_ID, testHandler);
        tokenService.fireEvent(FlashSafeTokenEvent.ATTACHED, testToken);
        verify(testHandler).handleEvent(FlashSafeTokenEvent.ATTACHED, testToken);
    }
    
    @Test
    public void subscribeToEvents() {
        tokenService.subscribeToEvents(testToken, testHandler);
        tokenService.fireEvent(FlashSafeTokenEvent.ATTACHED, testToken);
        verify(testHandler).handleEvent(FlashSafeTokenEvent.ATTACHED, testToken);
    }
    
    @Test
    public void unsubscribeFromEventsWithTokenId() {
        tokenService.subscribeToEvents(TEST_TOKEN_ID, testHandler);
        tokenService.unsubscribeFromEvents(TEST_TOKEN_ID, testHandler);
        tokenService.fireEvent(FlashSafeTokenEvent.ATTACHED, testToken);
        verify(testHandler, never()).handleEvent(FlashSafeTokenEvent.ATTACHED, testToken);
    }
    
    @Test
    public void unsubscribeFromEvents() {
        tokenService.subscribeToEvents(testToken, testHandler);
        tokenService.unsubscribeFromEvents(testToken, testHandler);
        tokenService.fireEvent(FlashSafeTokenEvent.ATTACHED, testToken);
        verify(testHandler, never()).handleEvent(FlashSafeTokenEvent.ATTACHED, testToken);
    }
    
    
    private static final class FlashSafeTokenServiceBaseImpl extends FlashSafeTokenServiceBase {

        @Override
        public FlashSafeToken lookup(String tokenId) throws FlashSafeTokenNotFoundException {
            throw new UnsupportedOperationException();
        }
        
    }
}
