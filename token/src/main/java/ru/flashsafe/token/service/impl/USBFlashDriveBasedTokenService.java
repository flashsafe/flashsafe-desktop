package ru.flashsafe.token.service.impl;

import java.util.List;

import javax.usb.UsbConfiguration;
import javax.usb.UsbDevice;
import javax.usb.UsbException;
import javax.usb.UsbHostManager;
import javax.usb.UsbInterface;
import javax.usb.UsbServices;
import javax.usb.event.UsbServicesEvent;
import javax.usb.event.UsbServicesListener;

import ru.flashsafe.token.FlashSafeToken;
import ru.flashsafe.token.exception.FlashSafeTokenNotFoundException;

/**
 * 
 * @author Andrew
 *
 */
public class USBFlashDriveBasedTokenService extends FlashSafeTokenServiceBase {

    private static final byte MASS_STORAGE_DEVICE_TYPE = 8;
    
    private UsbServices usbServices;
    
    public USBFlashDriveBasedTokenService() {
        try {
            usbServices = UsbHostManager.getUsbServices();
            usbServices.addUsbServicesListener(new UsbServicesListener() {
                
                @Override
                public void usbDeviceDetached(UsbServicesEvent event) {
                    System.out.println("usbDeviceDetached");
                }
                
                @Override
                public void usbDeviceAttached(UsbServicesEvent event) {
                    UsbDevice device = event.getUsbDevice();
                    if (matchesUSBClassType(device, MASS_STORAGE_DEVICE_TYPE)) {
                        System.out.println("usb flash Attached");
                    }
                }
            });
        } catch (SecurityException | UsbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private static boolean matchesUSBClassType(UsbDevice usbDevice, byte usbClassType) {
        boolean matchingType = false;

        UsbConfiguration config = usbDevice.getActiveUsbConfiguration();
        if (config == null) {
            return false;
        }
        for (UsbInterface iface: (List<UsbInterface>) config.getUsbInterfaces()) {
           if(iface.getUsbInterfaceDescriptor().bInterfaceClass() == usbClassType) {
               matchingType = true;
               break;
           }
        }

        return matchingType;
   }
    
    @Override
    public FlashSafeToken lookup(String tokenId) throws FlashSafeTokenNotFoundException {
        
        return null;
    }

}
