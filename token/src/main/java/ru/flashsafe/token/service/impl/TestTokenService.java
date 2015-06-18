package ru.flashsafe.token.service.impl;

import java.util.HashMap;
import java.util.Map;

import ru.flashsafe.token.FlashSafeToken;
import ru.flashsafe.token.event.FlashSafeTokenEvent;
import ru.flashsafe.token.exception.FlashSafeTokenNotFoundException;
import ru.flashsafe.token.generator.CodeGenerationStrategy;
import ru.flashsafe.token.generator.FixedValueGenerationStrategy;
import ru.flashsafe.token.impl.FlashSafeTestToken;
import ru.flashsafe.token.service.FlashSafeTokenService;

/**
 * 
 * @author Andrew
 *
 */
public class TestTokenService extends FlashSafeTokenServiceBase implements FlashSafeTokenService {
    
    private static final TestTokenService INSTANCE = new TestTokenService();
    
    private CodeGenerationStrategy codeGenerationStrategy = new FixedValueGenerationStrategy();
    
    private Map<String, FlashSafeTestToken> availableTokens = new HashMap<>();
    
    private TestTokenService() {
    }
    
    @Override
    public synchronized FlashSafeToken lookup(String tokenId) throws FlashSafeTokenNotFoundException {
        FlashSafeToken token = availableTokens.get(tokenId);
        if (token == null) {
            throw new FlashSafeTokenNotFoundException(tokenId);
        }
        return token;
    }
    
    public static TestTokenService getTokenService() {
        return INSTANCE;
    }
    
    /**
     * Sets the code generation strategy
     * 
     * @param codeGenerationStrategy the strategy
     */
    public void setCodeGenerationStrategy(CodeGenerationStrategy codeGenerationStrategy) {
        if (codeGenerationStrategy == null) {
            throw new IllegalStateException("Generation strategy may not be NULL");
        }
        this.codeGenerationStrategy = codeGenerationStrategy;
    }
    
    public synchronized void fireAttachEvent(String tokenId) {
        FlashSafeTestToken token = createTokenInstance(tokenId, codeGenerationStrategy);
        availableTokens.put(tokenId, token);
        fireEvent(FlashSafeTokenEvent.ATTACHED, token);
    }
    
    public synchronized void fireDetachEvent(String tokenId) {
        FlashSafeTestToken token = availableTokens.get(tokenId);
        if (token != null) {
            token.setUnavailable();
            availableTokens.remove(tokenId);
            fireEvent(FlashSafeTokenEvent.DETACHED, token);
        }
    }
    
    private static FlashSafeTestToken createTokenInstance(String tokenId, CodeGenerationStrategy codeGenerationStrategy) {
        return new FlashSafeTestToken(tokenId, codeGenerationStrategy);
    }
}
