package ru.flashsafe;

import ru.flashsafe.token.service.FlashSafeTokenService;
import ru.flashsafe.token.service.impl.USBFlashDriveBasedTokenService;

public class Demo {
    
    public static void main(String[] args) {
        FlashSafeTokenService tokenService = new USBFlashDriveBasedTokenService();
        while(true) {
            
        }
    }

}
