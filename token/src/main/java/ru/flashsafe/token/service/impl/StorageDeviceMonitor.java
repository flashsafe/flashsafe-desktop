package ru.flashsafe.token.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import ru.flashsafe.token.util.EventMonitor;
import ru.flashsafe.token.util.EventMonitor.EventHandler;

public class StorageDeviceMonitor implements EventHandler {

    private final EventMonitor monitor;
    
    private final USBFlashDriveBasedTokenService tokenService;
    
    private File[] previousDevicesList = File.listRoots();
    
    public StorageDeviceMonitor(USBFlashDriveBasedTokenService tokenService) {
        monitor = new EventMonitor("StorageDeviceMonitor", this);
        this.tokenService = tokenService;
    }
    
    public void start() {
        monitor.start();
    }

    @Override
    public void onEvent() {
        File[] currentDevicesList = File.listRoots();
        File[] changed = diff(previousDevicesList, currentDevicesList);
        if (changed.length > 0) {
            if (currentDevicesList.length > previousDevicesList.length) {
                for (File attachedDevice: changed) {
                    tokenService.onAttachDevice(attachedDevice);
                }
            } else {
                for (File detachedDevice: changed) {
                    tokenService.onDetachDevice(detachedDevice);
                }
            }
        } else {
            tokenService.checkExistedDevices();
        }
        previousDevicesList = currentDevicesList;
    }
    
    //FIXME should be improved
    private static File[] diff(File[] first, File[] second) {
        Collection<File> firstCollection = new ArrayList<>(Arrays.asList(first));
        Collection<File> secondCollection = new ArrayList<>(Arrays.asList(second));
        Collection<File> diff = null;
        if (firstCollection.size() > secondCollection.size()) {
            firstCollection.removeAll(secondCollection);
            diff = firstCollection;
        } else {
            secondCollection.removeAll(firstCollection);
            diff = secondCollection;
        }
        return diff.toArray(new File[diff.size()]);
    }

}
