package ru.flashsafe.core.example;

import ru.flashsafe.core.FlashSafeApplication;
import ru.flashsafe.core.FlashSafeSystem;

/**
 * Simple work flow example.
 * 
 * @author Andrew
 *
 */
public class HelloWorld {

    public static void main(String[] args) {
        FlashSafeApplication.run();
        
        FlashSafeSystem flashSafeSystem = FlashSafeApplication.flashSafeSystem();
        System.out.println("Storage address: " + flashSafeSystem.storageAddress());
        
        FlashSafeApplication.stop();
    }

}
