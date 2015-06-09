package ru.flashsafe.token.impl;

import ru.flashsafe.token.FlashSafeToken;
import ru.flashsafe.token.exception.CodeGenerationException;
import ru.flashsafe.token.exception.FlashSafeTokenUnavailableException;

/**
 * USB flash drive implementation of the FlashSafeToken interface. This implementation of
 * token uses USB flash drive as a mock of real FlashSafe token.
 * 
 * @author Andrew
 *
 */
public class FlashSafeUSBFlashDriveToken implements FlashSafeToken {

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
