package ru.flashsafe.token.impl;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

import ru.flashsafe.token.exception.CodeGenerationException;
import ru.flashsafe.token.exception.FlashSafeTokenUnavailableException;
import ru.flashsafe.token.generator.CodeGenerationStrategy;

public class FlashSafeTestTokenTest {
    
    private CodeGenerationStrategy strategy;
    
    @Before
    public void init() throws CodeGenerationException {
        strategy = mock(CodeGenerationStrategy.class);
        when(strategy.generateCodeFor(anyString())).thenReturn("TEST_TOKEN_CODE");
    }
    
    @Test
    public void testGenerateCode() throws FlashSafeTokenUnavailableException, CodeGenerationException {
        FlashSafeTestToken token = new FlashSafeTestToken("TEST_TOKEN_ID", strategy);
        String actualCode = token.generateCode("TEST_KEY");
        assertThat(actualCode, equalTo("TEST_TOKEN_CODE"));
    }
    
    @Test(expected = FlashSafeTokenUnavailableException.class)
    public void testGenerateCode_fails_if_token_unavailable() throws FlashSafeTokenUnavailableException, CodeGenerationException {
        FlashSafeTestToken token = new FlashSafeTestToken("TEST_TOKEN_ID", strategy);
        token.setUnavailable();
        token.generateCode("TEST_KEY");
    }
    
}
