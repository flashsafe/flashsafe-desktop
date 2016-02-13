package ru.flashsafe.token.impl;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.flashsafe.token.FlashSafeToken;
import ru.flashsafe.token.event.BaseEventHandler;
import ru.flashsafe.token.exception.CodeGenerationException;
import ru.flashsafe.token.exception.FlashSafeTokenUnavailableException;
import ru.flashsafe.token.generator.CodeGenerationStrategy;
import ru.flashsafe.token.service.impl.USBFlashDriveBasedTokenService;

/**
 * USB flash drive implementation of the FlashSafeToken interface. This implementation of
 * token uses USB flash drive as a mock of real FlashSafe token.
 * 
 * @author Andrew
 *
 */
public class FlashSafeUSBFlashDriveToken implements FlashSafeToken {
    
    private static final Logger LOGGER = LogManager.getLogger(FlashSafeUSBFlashDriveToken.class);
    
    private static final String ID_FILE_PATH = ".flashsafe" + File.separator + "flashsafe.id";

    private final String id;
    
    private final File tokenRoot;
    
    private final File tokenIdFile;
    
    private volatile boolean available = true;
    
    private final CodeGenerationStrategy codeGenerationStrategy;
    
    public FlashSafeUSBFlashDriveToken(String id, File tokenRoot, CodeGenerationStrategy codeGenerationStrategy,
            USBFlashDriveBasedTokenService tokenService) {
        this.id = id;
        this.tokenRoot = tokenRoot;
        tokenIdFile = new File(tokenRoot, ID_FILE_PATH);
        this.codeGenerationStrategy = codeGenerationStrategy;
        subscrubeToDetach(tokenService);
    }
    
    @Override
    public String getId() {
        return id;
    }

    @Override
    public String generateCode(String key) throws CodeGenerationException, FlashSafeTokenUnavailableException {
        return codeGenerationStrategy.generateCodeFor(key);
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
                LOGGER.debug("FlashSafeUSBFlashDriveToken " + FlashSafeUSBFlashDriveToken.this + " detached and unsubscribed");
            }
            
            @Override
            protected void onAttach(FlashSafeToken flashSafeToken) {
            }
        });
    }
    
    @Override
    public String toString() {
        return String.format("[id = %s, path = %s, available = %b]", id, tokenRoot.getAbsolutePath(), available);
    }
    
    public static boolean isFlashSafeToken(File pathToToken) {
        File pathToTokenId = new File(pathToToken, ID_FILE_PATH);
        return pathToTokenId.exists();
    }
    
    public static String getTokenId(File pathToToken) throws IOException {
        URI tokenIdFileURI = new File(pathToToken, ID_FILE_PATH).toURI();
        List<String> lines = Files.readAllLines(Paths.get(tokenIdFileURI), Charset.forName("UTF-8"));
        return lines.isEmpty() ? FlashSafeToken.UNDEFINED_TOKEN_ID : lines.iterator().next();
    }
}
