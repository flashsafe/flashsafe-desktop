package ru.flashsafe.core.storage;

import ru.flashsafe.core.file.FileOperation;

/**
 * Represents an operation with file(s) which includes FlashSafe storage operation(s).
 * 
 * @author Andrew
 *
 */
public interface StorageFileOperation extends FileOperation {
    
    StorageOperationType getStorageOperationType();
    
    void markAsFinished();
    
    void waitUntilFinished() throws InterruptedException;

}
