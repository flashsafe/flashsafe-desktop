package ru.flashsafe.token.service.impl;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.flashsafe.token.FlashSafeToken;
import ru.flashsafe.token.event.FlashSafeTokenEvent;
import ru.flashsafe.token.exception.FlashSafeTokenNotFoundException;
import ru.flashsafe.token.exception.TokenServiceInitializationException;
import ru.flashsafe.token.impl.FlashSafeUSBFlashDriveToken;
import ru.flashsafe.token.service.FlashSafeTokenService;

/**
 * 
 * @author Andrew
 *
 */
//TODO review onAttachDevice, onDetachDevice, StorageDeviceMonitor and improve
public class USBFlashDriveBasedTokenService extends FlashSafeTokenServiceBase {
    
    private static final Logger logger = LogManager.getLogger(USBFlashDriveBasedTokenService.class);
    
    private static USBFlashDriveBasedTokenService instance;
    
    private final Map<String, FlashSafeUSBFlashDriveToken> availableTokens = new HashMap<>();
    
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
    
    public static synchronized FlashSafeTokenService getTokenService() throws TokenServiceInitializationException {
        if (instance == null) {
            instance = new USBFlashDriveBasedTokenService();
            instance.initService();
        }
        return instance;
    }
    
    private void initService() throws TokenServiceInitializationException {
        StorageDeviceMonitor monitor = new StorageDeviceMonitor(this);
        monitor.start();
    }
    
    protected synchronized void onAttachDevice(File pathToDevice) {
        if (isFlashSafeToken(pathToDevice)) {
            try {
                String tokenId = getTokenId(pathToDevice);
                FlashSafeUSBFlashDriveToken token = createTokenInstance(tokenId, pathToDevice, this);
                availableTokens.put(pathToDevice.getAbsolutePath(), token);
                logger.debug("USB FlashSafe token attached [id = " + token.getId() + "]");
                fireEvent(FlashSafeTokenEvent.ATTACHED, token);
            } catch (IOException e) {
                logger.error("Error while processing token " + pathToDevice.getAbsolutePath(), e);
            }
        }
    }
    
    protected synchronized void onDetachDevice(File pathToDevice) {
        FlashSafeToken token = availableTokens.remove(pathToDevice.getAbsolutePath());
        if (token != null) {
            logger.debug("USB FlashSafe token detached [id = " + token.getId() + "]");
            fireEvent(FlashSafeTokenEvent.DETACHED, token);
        }
    }
    
    protected synchronized void checkExistedTokens() {
        for (FlashSafeUSBFlashDriveToken token : availableTokens.values()) {
            if (!token.isAvailable()) {
                onDetachDevice(token.getTokenRoot());
            }
        }
    }
    
    private static FlashSafeUSBFlashDriveToken createTokenInstance(String id, File tokenRoot, USBFlashDriveBasedTokenService tokenService) {
        return new FlashSafeUSBFlashDriveToken(id, tokenRoot, tokenService);
    }
    
    private static boolean isFlashSafeToken(File pathToToken) {
        File pathToTokenId = new File(pathToToken, ".flashsafe" + File.separator + "flashsafe.id");
        return pathToTokenId.exists();
    }
    
    private static String getTokenId(File pathToToken) throws IOException {
        URI tokenIdFileURI = new File(pathToToken, ".flashsafe" + File.separator + "flashsafe.id").toURI();
        List<String> lines = Files.readAllLines(Paths.get(tokenIdFileURI), Charset.forName("UTF-8"));
        return lines.isEmpty() ? null : lines.iterator().next();
    }
}
