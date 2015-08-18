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
    
    public static final String LOCAL_SIMULTANEOUSLY_EXECUTED_OPERATIONS = "LOCAL_SIMULTANEOUSLY_EXECUTEN_OPERATIONS";
    
    public static final String REMOTE_SIMULTANEOUSLY_EXECUTED_OPERATIONS = "REMOTE_SIMULTANEOUSLY_EXECUTEN_OPERATIONS";
    
    public static final String LOCAL_TO_STORAGE_SIMULTANEOUSLY_EXECUTED_OPERATIONS = "LOCAL_TO_STORAGE_SIMULTANEOUSLY_EXECUTEN_OPERATIONS";

    private static Properties flashSafeProperties = new Properties();
    
    static {
        flashSafeProperties.put(STORAGE_ADDRESS, "https://flashsafe-alpha.azurewebsites.net");
        flashSafeProperties.put(LOCAL_SIMULTANEOUSLY_EXECUTED_OPERATIONS, 10);
        flashSafeProperties.put(REMOTE_SIMULTANEOUSLY_EXECUTED_OPERATIONS, 10);
        flashSafeProperties.put(LOCAL_TO_STORAGE_SIMULTANEOUSLY_EXECUTED_OPERATIONS, 10);
    }
    
    private FlashSafeRegistry() {
    }
    
    public static String getStorageAddress() {
        return flashSafeProperties.getProperty(STORAGE_ADDRESS);
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T readProperty(String name) {
        return (T) flashSafeProperties.get(name);
    }
}
