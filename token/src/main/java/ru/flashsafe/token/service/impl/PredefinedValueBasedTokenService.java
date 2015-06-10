package ru.flashsafe.token.service.impl;

import ru.flashsafe.token.FlashSafeToken;
import ru.flashsafe.token.event.FlashSafeTokenEvent;
import ru.flashsafe.token.exception.FlashSafeTokenNotFoundException;
import ru.flashsafe.token.impl.FlashSafePredefinedValueToken;
import ru.flashsafe.token.service.FlashSafeTokenService;

/**
 * 
 * @author Andrew
 *
 */
public class PredefinedValueBasedTokenService extends FlashSafeTokenServiceBase implements FlashSafeTokenService {
    
    private static final PredefinedValueBasedTokenService INSTANCE = new PredefinedValueBasedTokenService();
    
    private String tokenId = "3467687665";
    
    private String tokenCode = "open123458";
    
    private FlashSafePredefinedValueToken token = createTokenInstance(tokenId, tokenCode);
    
    private PredefinedValueBasedTokenService() {
    }
    
    public static PredefinedValueBasedTokenService getTokenService() {
        return INSTANCE;
    }
    
    @Override
    public FlashSafeToken lookup(String tokenId) throws FlashSafeTokenNotFoundException {
        if (!this.tokenId.equals(tokenId)) {
            throw new FlashSafeTokenNotFoundException(tokenId);
        }
        return token;
    }
    
    public void defineTokenId(String id) {
        if (id == null) {
            throw new IllegalStateException("Token's id may not be NULL");
        }
        tokenId = id;
        token = createTokenInstance(tokenId, tokenCode);
    }
    
    public void defineTokenCode(String code) {
        if (code == null) {
            throw new IllegalStateException("Token's code may not be NULL");
        }
        tokenCode = code;
        token = createTokenInstance(tokenId, tokenCode);
    }
    
    public synchronized void fireAttachEvent() {
        token.setUnavailable();
        token = createTokenInstance(tokenId, tokenCode);
        fireEvent(FlashSafeTokenEvent.ATTACHED, token);
    }
    
    public synchronized void fireDetachEvent() {
        token.setUnavailable();
        fireEvent(FlashSafeTokenEvent.DETACHED, token);
    }
    
    private static FlashSafePredefinedValueToken createTokenInstance(String tokenId, String tokenCode) {
        return new FlashSafePredefinedValueToken(tokenId, tokenCode);
    }
}
