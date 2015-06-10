package ru.flashsafe.token.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import ru.flashsafe.token.FlashSafeToken;
import ru.flashsafe.token.exception.CodeGenerationException;
import ru.flashsafe.token.exception.FlashSafeTokenNotFoundException;
import ru.flashsafe.token.exception.FlashSafeTokenUnavailableException;

public class PredefinedValueBasedTokenServiceTest {
    
    private static final String TEST_TOKEN_ID = "TEST_TOKEN_ID";

    private PredefinedValueBasedTokenService service;
    
    @Before
    public void before() {
        service = PredefinedValueBasedTokenService.getTokenService();
    }
    
    @Test
    public void lookup() throws FlashSafeTokenNotFoundException {
        service.defineTokenId(TEST_TOKEN_ID);
        FlashSafeToken expectedToken = service.lookup(TEST_TOKEN_ID);
        assertNotNull(expectedToken);
        assertEquals(TEST_TOKEN_ID, expectedToken.getId());
    }
    
    @Test
    public void defineTokenId() throws FlashSafeTokenNotFoundException {
        service.defineTokenId("UNIC_TOKEN_ID");
        FlashSafeToken expectedToken = service.lookup("UNIC_TOKEN_ID");
        assertNotNull(expectedToken);
        assertEquals("UNIC_TOKEN_ID", expectedToken.getId());
    }
    
    @Test
    public void defineTokenCode() throws FlashSafeTokenNotFoundException, CodeGenerationException,
            FlashSafeTokenUnavailableException {
        service.defineTokenCode("UNIC_TOKEN_CODE");
        service.defineTokenId(TEST_TOKEN_ID);
        FlashSafeToken expectedToken = service.lookup(TEST_TOKEN_ID);
        assertNotNull(expectedToken);
        assertEquals("UNIC_TOKEN_CODE", expectedToken.generateCode("TEST_KEY"));
    }
    
}
