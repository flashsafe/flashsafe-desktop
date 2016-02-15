package ru.flashsafe.token.example;

import java.io.IOException;

import ru.flashsafe.token.FlashSafeToken;
import ru.flashsafe.token.event.BaseEventHandler;
import ru.flashsafe.token.exception.CodeGenerationException;
import ru.flashsafe.token.exception.FlashSafeTokenNotFoundException;
import ru.flashsafe.token.exception.FlashSafeTokenUnavailableException;
import ru.flashsafe.token.exception.TokenServiceInitializationException;
import ru.flashsafe.token.generator.FixedValueGenerationStrategy;
import ru.flashsafe.token.service.FlashSafeTokenService;
import ru.flashsafe.token.service.impl.RemoteEmulatorTokenService;

/**
 * Example of usage FlashSafeTokenService and FlashSafeToken.
 * Uses RemoteEmulatorTokenService as a mock of FlashSafeTokenService.
 * 
 * Use {@link FlashSafeRemoteEmulator} emulator to send attach/detach events. 
 *
 */
public class RemoteEmulatorServiceDemo {
    
    private static final String TOKEN_ID = "12345";

    public static void main(String[] args) throws TokenServiceInitializationException, IOException {

        // Creating certain implementation of FlashSafeTokenService.
        RemoteEmulatorTokenService tokenService = RemoteEmulatorTokenService.getTokenService();
        // setting up the code generation strategy. There we use fixed value strategy 
        tokenService.setCodeGenerationStrategy(new FixedValueGenerationStrategy("open12345"));
        
        RemoteEmulatorServiceDemo demo = new RemoteEmulatorServiceDemo();
        // From now we should use RemoteEmulatorTokenService thru FlashSafeTokenService.
        demo.startApplication(tokenService);

        while (true) {
        }
    }
    
    public void startApplication(FlashSafeTokenService tokenService) {
        
        try {
            // use FlashSafeTokenService#lookup() to lookup the token at start of application.
            FlashSafeToken token = tokenService.lookup(TOKEN_ID);
            
            // use FlashSafeToken#isAvailable() to make sure that the token is available.
            boolean isAvailable = token.isAvailable();
        
            
            System.out.println("Token " + token + ". Is available = " + isAvailable);
        } catch (FlashSafeTokenNotFoundException e1) {
            // it will occur on each start of this demo application - because of
            // absence of token with id 12345.
            System.out.println(e1.getMessage());
        }
            
        // use FlashSafeTokenService#subscribeToEvents() to handle token's
        // attach and detach.
        tokenService.subscribeToEvents(TOKEN_ID, new BaseEventHandler() {

            @Override
            protected void onDetach(FlashSafeToken flashSafeToken) {
                System.out.println("FlashSafe token detached." + flashSafeToken);
                System.out.println("Checking status...");
                System.out.println("Token status: " + flashSafeToken.isAvailable());
            }

            @Override
            protected void onAttach(FlashSafeToken flashSafeToken) {
                System.out.println("FlashSafe token attached." + flashSafeToken);
                System.out.println("Trying to generate code...");
                try {
                    System.out.println("The code is " + flashSafeToken.generateCode("qwerty"));
                } catch (CodeGenerationException | FlashSafeTokenUnavailableException e) {
                    System.out.println(e.getMessage());
                }
            }
        });

    }
    
}
