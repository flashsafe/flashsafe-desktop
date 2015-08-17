package ru.flashsafe.core;

import java.util.Properties;

/**
 * A registry for FlashSafe application's settings.
 * 
 * @author Andrew
 *
 */
public class FlashSafeRegistry {

    public static final String STORAGE_ADDRESS = "STORAGE_ADDRESS";

    private static Properties flashSafeProperties = new Properties();
    
    static {
        flashSafeProperties.put(STORAGE_ADDRESS, "https://flashsafe-alpha.azurewebsites.net");
    }
    
    private FlashSafeRegistry() {
    }
    
    public static String getStorageAddress() {
        return flashSafeProperties.getProperty(STORAGE_ADDRESS);
    }
}
