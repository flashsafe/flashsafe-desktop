package ru.flashsafe.token.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.flashsafe.token.FlashSafeToken;
import ru.flashsafe.token.event.FlashSafeTokenEvent;
import ru.flashsafe.token.exception.FlashSafeTokenNotFoundException;
import ru.flashsafe.token.exception.TokenServiceInitializationException;
import ru.flashsafe.token.generator.CodeGenerationStrategy;
import ru.flashsafe.token.generator.FixedValueGenerationStrategy;
import ru.flashsafe.token.impl.FlashSafeUSBFlashDriveToken;
import ru.flashsafe.token.service.FlashSafeTokenService;

/**
 * The implementation of {@link FlashSafeTokenService} which uses USB flash
 * drive as a {@link FlashSafeToken} mock.
 * 
 * @author Andrew
 * 
 */
public class USBFlashDriveBasedTokenService extends FlashSafeTokenServiceBase {
    
    private static final Logger LOGGER = LogManager.getLogger(USBFlashDriveBasedTokenService.class);
    
    private static USBFlashDriveBasedTokenService instance;
    
    /**
     * Map [devicePath -> token].
     */
    private final Map<String, FlashSafeUSBFlashDriveToken> availableTokens = new HashMap<>();
    
    private final Set<String> availableTokenIds = new HashSet<>();
    
    private CodeGenerationStrategy codeGenerationStrategy = new FixedValueGenerationStrategy();
    
    private USBFlashDriveBasedTokenService() {
    }
    
    @Override
    public synchronized FlashSafeToken lookup(String tokenId) throws FlashSafeTokenNotFoundException {   
        FlashSafeToken token = availableTokens.get(tokenId);
        if (token == null) {
            throw new FlashSafeTokenNotFoundException(tokenId);
        }
        return token;
    }
    
    /**
     * @return the instance of {@link USBFlashDriveBasedTokenService}
     * @throws TokenServiceInitializationException
     */
    public static synchronized USBFlashDriveBasedTokenService getTokenService() throws TokenServiceInitializationException {
        if (instance == null) {
            instance = new USBFlashDriveBasedTokenService();
            instance.initService();
        }
        return instance;
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
    
    private void initService() throws TokenServiceInitializationException {
        StorageDeviceMonitor monitor = new StorageDeviceMonitor(this);
        monitor.start();
    }
    
    protected synchronized void onAttachDevice(File pathToDevice) {
        if (!FlashSafeUSBFlashDriveToken.isFlashSafeToken(pathToDevice)) {
            return;
        }
        String tokenId = readTokenId(pathToDevice);
        if (tokenId.equals(FlashSafeToken.UNDEFINED_TOKEN_ID)) {
            LOGGER.warn("Undefined token identifier for " + pathToDevice.getAbsolutePath()
                    + ". Can't attach device as FlashSafe token");
            return;
        }
        if (availableTokenIds.contains(tokenId)) {
            LOGGER.error("Trying to attach device with duplicate token identifier. " + "Token " + pathToDevice.getAbsolutePath()
                    + " with id = " + tokenId + " will be ignored");
            return;
        }
        FlashSafeUSBFlashDriveToken token = createTokenInstance(tokenId, pathToDevice, codeGenerationStrategy, this);
        availableTokens.put(pathToDevice.getAbsolutePath(), token);
        availableTokenIds.add(tokenId);
        LOGGER.debug("Attaching USB FlashSafe token " + token);
        fireEvent(FlashSafeTokenEvent.ATTACHED, token);
        LOGGER.debug("USB FlashSafe token attached " + token);
    }
    
    protected synchronized void onDetachDevice(File pathToDevice) {
        FlashSafeToken token = availableTokens.remove(pathToDevice.getAbsolutePath());
        if (token != null) {
            availableTokenIds.remove(token.getId());
            LOGGER.debug("Detaching USB FlashSafe token " + token);
            fireEvent(FlashSafeTokenEvent.DETACHED, token);
            LOGGER.debug("USB FlashSafe token detached " + token);
        }
    }
    
    protected synchronized void checkExistedDevices() {
        FlashSafeUSBFlashDriveToken[] tokensToCheck = availableTokens.values().toArray(
                new FlashSafeUSBFlashDriveToken[availableTokens.size()]);
        for (FlashSafeUSBFlashDriveToken token : tokensToCheck) {
            if (!token.isAvailable()) {
                onDetachDevice(token.getTokenRoot());
            }
        }
    }
    
    private static String readTokenId(File pathToDevice) {
        String tokenId = FlashSafeToken.UNDEFINED_TOKEN_ID;
        try {
            tokenId = FlashSafeUSBFlashDriveToken.getTokenId(pathToDevice);
        } catch (IOException e) {
            LOGGER.error("Error while reading token identifier from device " + pathToDevice.getAbsolutePath(), e);
        }
        return tokenId;
    }
    
    private static FlashSafeUSBFlashDriveToken createTokenInstance(String id, File tokenRoot,
            CodeGenerationStrategy codeGenerationStrategy, USBFlashDriveBasedTokenService tokenService) {
        return new FlashSafeUSBFlashDriveToken(id, tokenRoot, codeGenerationStrategy, tokenService);
    }
    
}
