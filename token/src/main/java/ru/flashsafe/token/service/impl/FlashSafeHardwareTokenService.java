package ru.flashsafe.token.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.usb.UsbConfiguration;
import javax.usb.UsbDevice;
import javax.usb.UsbException;
import javax.usb.UsbHostManager;
import javax.usb.UsbInterface;
import javax.usb.UsbServices;
import javax.usb.event.UsbServicesEvent;
import javax.usb.event.UsbServicesListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.flashsafe.token.FlashSafeToken;
import ru.flashsafe.token.event.FlashSafeTokenEvent;
import ru.flashsafe.token.exception.FlashSafeTokenNotFoundException;
import ru.flashsafe.token.exception.TokenServiceInitializationException;
import ru.flashsafe.token.service.FlashSafeTokenService;
import ru.flashsafe.token.usb.USBClassTypes;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * 
 * 
 * @author Andrew
 *
 */
//TODO waiting for token implementation
public class FlashSafeHardwareTokenService extends FlashSafeTokenServiceBase implements FlashSafeTokenService {

    private static final Logger LOGGER = LogManager.getLogger(FlashSafeHardwareTokenService.class);
    
    private static FlashSafeHardwareTokenService instance;
    
    private final Map<String, FlashSafeToken> availableTokens = new HashMap<>();
    
    private FlashSafeHardwareTokenService() {
    }
    
    @Override
    public synchronized FlashSafeToken lookup(String tokenId) throws FlashSafeTokenNotFoundException {   
        FlashSafeToken token = availableTokens.get(tokenId);
        if (token == null) {
            throw new FlashSafeTokenNotFoundException(tokenId);
        }
        return token;
    }
    
    public static synchronized FlashSafeTokenService getTokenService() throws TokenServiceInitializationException {
        throw new NotImplementedException();
        /*if (instance == null) {
            instance = new FlashSafeHardwareTokenService();
            instance.initService();
        }
        return instance;*/
    }
    
    private void initService() throws TokenServiceInitializationException {
        try {
            UsbServices usbServices = UsbHostManager.getUsbServices();
            attachHandlers(usbServices);
        } catch (SecurityException | UsbException e) {
            LOGGER.error("Failed to access the USB service", e);
            throw new TokenServiceInitializationException("Failed to access the USB service", e);
        }
    }
    
    private void attachHandlers(UsbServices usbServices) {
        usbServices.addUsbServicesListener(new UsbServicesListener() {
            
            @Override
            public synchronized void usbDeviceDetached(UsbServicesEvent event) {
                UsbDevice device = event.getUsbDevice();
                if (!matchesUSBClassType(device, USBClassTypes.MASS_STORAGE_DEVICE_TYPE)) {
                    return;
                }
                LOGGER.debug("USB FlashSafe token detached");
                //TODO remove token from availableTokens
                //FlashSafeToken token = availableTokens.remove("some_id");
                fireEvent(FlashSafeTokenEvent.DETACHED, null);
            }
            
            @Override
            public synchronized void usbDeviceAttached(UsbServicesEvent event) {
                UsbDevice device = event.getUsbDevice();
                if (!matchesUSBClassType(device, USBClassTypes.MASS_STORAGE_DEVICE_TYPE)) {
                    return;
                }
                LOGGER.debug("USB FlashSafe token attached" + " " + device);
                FlashSafeToken token = createTokenInstance();
                availableTokens.put(token.getId(), token);
                fireEvent(FlashSafeTokenEvent.ATTACHED, token);
            }
        });
    }
    
    private static FlashSafeToken createTokenInstance() {
        throw new NotImplementedException();
    }
    
    @SuppressWarnings("unchecked")
    private static boolean matchesUSBClassType(UsbDevice usbDevice, byte usbClassType) {
        UsbConfiguration config = usbDevice.getActiveUsbConfiguration();
        if (config == null) {
            return false;
        }
        for (UsbInterface iface: (List<UsbInterface>) config.getUsbInterfaces()) {
           if(iface.getUsbInterfaceDescriptor().bInterfaceClass() == usbClassType) {
               return true;
           }
        }
        return false;
   }

}
