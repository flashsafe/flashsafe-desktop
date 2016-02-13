package ru.flashsafe.token.impl;

import ru.flashsafe.token.FlashSafeToken;
import ru.flashsafe.token.exception.CodeGenerationException;
import ru.flashsafe.token.exception.FlashSafeTokenUnavailableException;

/**
 * 
 * 
 * @author Andrew
 *
 */
//TODO waiting for token implementation
public class FlashSafeHardwareToken implements FlashSafeToken {

    @Override
    public String getId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String generateCode(String key) throws CodeGenerationException, FlashSafeTokenUnavailableException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isAvailable() {
        // TODO Auto-generated method stub
        return false;
    }

}
