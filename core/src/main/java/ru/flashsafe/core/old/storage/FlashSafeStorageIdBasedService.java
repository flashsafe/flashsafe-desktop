package ru.flashsafe.core.old.storage;

import java.nio.file.Path;
import java.util.List;

import ru.flashsafe.core.storage.StorageFileOperation;
import ru.flashsafe.core.storage.exception.FlashSafeStorageException;

/**
 * An object that represents a service to work with remote FlashSafe storage.
 * This object operate with objects' Ids, not names. Use {@link ResourceResolver} to map names to storage's Ids.
 * 
 * @author Andrew
 *
 */
public interface FlashSafeStorageIdBasedService {

    /**
     * Retrieves list of file objects inside specified directory.
     * 
     * @param directoryId directory Id
     * @return list of fileObjects in this directory
     * @throws FlashSafeStorageException
     */
    List<FlashSafeStorageFileObject> list(long directoryId) throws FlashSafeStorageException;

    /**
     * Retrieves list of file objects inside specified directory.
     * 
     * @param directoryId directory Id
     * @param pincode access code
     * @return list of fileObjects in this directory
     * @throws FlashSafeStorageException
     */
    List<FlashSafeStorageFileObject> list(long directoryId, String pincode) throws FlashSafeStorageException;
    
    /**
     * Creates directory with specified name in parent directory.
     * 
     * @param parentDirectoryId parent directory Id
     * @param name name for directory
     * @return created {@link FlashSafeStorageDirectory} instance
     * @throws FlashSafeStorageException
     */
    FlashSafeStorageDirectory createDirectory(long parentDirectoryId, String name) throws FlashSafeStorageException;

    StorageFileOperation downloadFile(long fileId, Path directory) throws FlashSafeStorageException;

    StorageFileOperation uploadFile(long directoryId, Path file) throws FlashSafeStorageException;

    void copy(long fileObjectId, long destinationDirectoryId) throws FlashSafeStorageException;

    void move(long fileObjectId, long destinationDirectoryId) throws FlashSafeStorageException;

    void delete(long fileObjectId) throws FlashSafeStorageException;
    
}
