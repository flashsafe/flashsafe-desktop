package ru.flashsafe;

import java.io.IOException;

import ru.flashsafe.token.exception.TokenServiceInitializationException;
import ru.flashsafe.token.service.FlashSafeTokenService;
import ru.flashsafe.token.service.impl.USBFlashDriveBasedTokenService;

public class Demo {
    
    public static void main(String[] args) throws TokenServiceInitializationException, IOException {
        
        FlashSafeTokenService tokenService = USBFlashDriveBasedTokenService.getTokenService();
        while(true) {
            
        }
    }

}
