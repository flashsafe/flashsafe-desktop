package ru.flashsafe.token.impl;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import org.junit.Test;

import ru.flashsafe.token.exception.FlashSafeTokenUnavailableException;

public class FlashSafePredefinedValueTokenTest {
    
    @Test
    public void testGenerateCode() throws FlashSafeTokenUnavailableException {
        FlashSafePredefinedValueToken token = new FlashSafePredefinedValueToken("TEST_TOKEN_ID", "TEST_TOKEN_CODE");
        String actualCode = token.generateCode("TEST_KEY");
        assertThat(actualCode, equalTo("TEST_TOKEN_CODE"));
    }
    
    @Test(expected = FlashSafeTokenUnavailableException.class)
    public void testGenerateCode_fails_if_token_unavailable() throws FlashSafeTokenUnavailableException {
        FlashSafePredefinedValueToken token = new FlashSafePredefinedValueToken("TEST_TOKEN_ID", "TEST_TOKEN_CODE");
        token.setUnavailable();
        token.generateCode("TEST_KEY");
    }
    
}
