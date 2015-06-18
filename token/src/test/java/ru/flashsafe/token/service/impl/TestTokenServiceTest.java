package ru.flashsafe.token.service.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import ru.flashsafe.token.FlashSafeToken;
import ru.flashsafe.token.event.FlashSafeTokenEvent;
import ru.flashsafe.token.event.FlashSafeTokenEventHandler;

public class TestTokenServiceTest {
    
    private static final String TEST_TOKEN_ID = "TEST_TOKEN_ID";
    
    private FlashSafeTokenEventHandler testHandler;

    private TestTokenService service;
    
    @Before
    public void init() {
        service = TestTokenService.getTokenService();
        testHandler = mock(FlashSafeTokenEventHandler.class);
    }
    
    @Test
    public void fireAttachEvent() {
        service.subscribeToEvents(TEST_TOKEN_ID, testHandler);
        service.fireAttachEvent(TEST_TOKEN_ID);
        verify(testHandler).handleEvent(eq(FlashSafeTokenEvent.ATTACHED), any(FlashSafeToken.class));
    }
    
    @Test
    public void fireDetachEvent() {
        service.subscribeToEvents(TEST_TOKEN_ID, testHandler);
        service.fireDetachEvent(TEST_TOKEN_ID);
        verify(testHandler).handleEvent(eq(FlashSafeTokenEvent.DETACHED), any(FlashSafeToken.class));
    }
    
}
