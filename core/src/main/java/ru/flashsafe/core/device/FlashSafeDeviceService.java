package ru.flashsafe.core.device;


/**
 * 
 * 
 * @author Andrew
 *
 */
public interface FlashSafeDeviceService {

    FlashSafeDevice getDevice();
    
    default void updateClientApplication() {
        throw new UnsupportedOperationException("Not implemented yet!");
    }
    
}
