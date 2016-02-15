package ru.flashsafe.core.storage;

/**
 * Set of types for FlashSafe storage.
 * 
 * @author Andrew
 *
 */
public enum StorageOperationType {

    /**
     * Upload file to storage
     */
    UPLOAD,
    
    /**
     * Download file from storage
     */
    DOWNLOAD,
    
    /**
     * File operation is executed (copy, move, etc) inside storage
     */
    INTERNAL
    
}
