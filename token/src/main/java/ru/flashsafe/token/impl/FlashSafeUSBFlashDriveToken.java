package ru.flashsafe.token.impl;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.flashsafe.token.FlashSafeToken;
import ru.flashsafe.token.event.BaseEventHandler;
import ru.flashsafe.token.exception.CodeGenerationException;
import ru.flashsafe.token.exception.FlashSafeTokenUnavailableException;
import ru.flashsafe.token.service.impl.USBFlashDriveBasedTokenService;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * USB flash drive implementation of the FlashSafeToken interface. This implementation of
 * token uses USB flash drive as a mock of real FlashSafe token.
 * 
 * @author Andrew
 *
 */
public class FlashSafeUSBFlashDriveToken implements FlashSafeToken {
    
    private static final Logger logger = LogManager.getLogger(FlashSafeUSBFlashDriveToken.class);

    private final String id;
    
    private final File tokenRoot;
    
    private final File tokenIdFile;
    
    private volatile boolean available = true;
    
    public FlashSafeUSBFlashDriveToken(String id, File tokenRoot, USBFlashDriveBasedTokenService tokenService) {
        this.id = id;
        this.tokenRoot = tokenRoot;
        tokenIdFile = new File(tokenRoot, ".flashsafe" + File.separator + "flashsafe.id");
        subscrubeToDetach(tokenService);
    }
    
    @Override
    public String getId() {
        return id;
    }

    @Override
    public String generateCode(String key) throws CodeGenerationException, FlashSafeTokenUnavailableException {
        throw new NotImplementedException();
    }

    @Override
    public boolean isAvailable() {
        if (!available) {
            return false;
        }
        return checkTokenAvailability();
    }
    
    public File getTokenRoot() {
        return tokenRoot;
    }
    
    private boolean checkTokenAvailability() {
        return tokenIdFile.exists();
    }
    
    private void subscrubeToDetach(final USBFlashDriveBasedTokenService tokenService) {
        tokenService.subscribeToEvents(this, new BaseEventHandler() {
            
            @Override
            protected void onDetach(FlashSafeToken flashSafeToken) {
                available = false;
                tokenService.unsubscribeFromEvents(FlashSafeUSBFlashDriveToken.this, this);
                logger.debug("FlashSafeUSBFlashDriveToken [id = " + id + "] detached and unsubscribed");
            }
            
            @Override
            protected void onAttach(FlashSafeToken flashSafeToken) {
            }
        });
    }
}
