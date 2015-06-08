package ru.flashsafe.token.service.impl;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import ru.flashsafe.token.FlashSafeToken;

public class PredefinedValueBasedTokenServiceTest {

    private PredefinedValueBasedTokenService service;
    
    @Before
    public void before() {
        service = PredefinedValueBasedTokenService.getInstace();
    }
    
    @Test
    public void testLookup() {
        service.defineTokenId("TEST_TOKEN");
        FlashSafeToken expectedToken = service.lookup("TEST_TOKEN");
        assertNotNull(expectedToken);
    }
    
    
    
}
