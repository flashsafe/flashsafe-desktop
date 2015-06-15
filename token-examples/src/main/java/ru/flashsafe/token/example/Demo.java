package ru.flashsafe.token.example;

import java.io.File;
import java.io.IOException;

import ru.flashsafe.token.FlashSafeToken;
import ru.flashsafe.token.event.BaseEventHandler;
import ru.flashsafe.token.exception.CodeGenerationException;
import ru.flashsafe.token.exception.FlashSafeTokenUnavailableException;
import ru.flashsafe.token.exception.TokenServiceInitializationException;
import ru.flashsafe.token.generator.FixedValueGenerationStrategy;
import ru.flashsafe.token.service.impl.RemoteEmulatorTokenService;

public class Demo {
    
    public static void main(String[] args) throws TokenServiceInitializationException, IOException {
        
        RemoteEmulatorTokenService tokenService = RemoteEmulatorTokenService.getTokenService();
        tokenService.setCodeGenerationStrategy(new FixedValueGenerationStrategy("open12345"));
        
        
        tokenService.subscribeToEvents("12345", new BaseEventHandler() {
            
            @Override
            protected void onDetach(FlashSafeToken flashSafeToken) {
                System.out.println("FlashSafe token detached." + flashSafeToken);
                System.out.println("Checking status...");
                System.out.println("Token status: " + flashSafeToken.isAvailable());
                File currFile = new File("C:\tmp\temfile.flashsafe");
                currFile.delete();
            }
            
            @Override
            protected void onAttach(FlashSafeToken flashSafeToken) {
                System.out.println("FlashSafe token attached." + flashSafeToken);
                System.out.println("Trying to generate code...");
                try {
                    System.out.println("The code is " + flashSafeToken.generateCode("qwerty"));
                } catch (CodeGenerationException | FlashSafeTokenUnavailableException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        
        while(true) {
            
        }
    }

}
