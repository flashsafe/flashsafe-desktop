package ru.flashsafe.core.storage;

import java.nio.file.Path;


public interface FlashSafeStorageService {

    FlashSafeStorageFileObject list(long directoryId);
    
    FlashSafeStorageDirectory createDirectory(long parentDirectoryId, String name);
    
    void downloadFile(long fileId, Path directory);
    
    void uploadFile(long directoryId, Path file);
    
    void copy(long fileObjectId, long destinationDirectoryId);
    
    void move(long fileObjectId, long destinationDirectoryId);
    
    void delete(long fileObjectId);
    
}
