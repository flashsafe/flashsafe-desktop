package ru.flashsafe.core.storage;

import ru.flashsafe.core.file.FileOperation;

/**
 * Represents an operation with file(s) which includes FlashSafe storage operation(s).
 * 
 * @author Andrew
 *
 */
public interface StorageFileOperation extends FileOperation {
    
    /**
     * @return storage operation type
     * @see StorageOperationType
     */
    StorageOperationType getStorageOperationType();
    
    /**
     * Marks this operation as finished. Call of this method releases threads
     * which hang on {@link #waitUntilFinished()}.
     */
    void markAsFinished();
    
    /**
     * This method blocks current thread until {@link #markAsFinished()} execution call.
     * 
     * @throws InterruptedException
     */
    void waitUntilFinished() throws InterruptedException;

}
