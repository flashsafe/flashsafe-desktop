package ru.flashsafe.core.file.impl;

import ru.flashsafe.core.file.FileManager;

public interface FileManagerBridge<F, T> {
    
    void transferFileObject(F fromCurrentLocaltion, T toLocation, FileManager thisManager);

}
