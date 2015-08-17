package ru.flashsafe.core.device;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * 
 * 
 * @author Andrew
 *
 */
public interface FlashSafeDeviceService {

    FlashSafeDevice getDevice();
    
    default void updateClientApplication() {
        throw new NotImplementedException();
    }
    
}
