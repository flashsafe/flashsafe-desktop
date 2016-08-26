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
    List<FlashSafeStorageFileObject> list(String directoryHash) throws FlashSafeStorageException;

    /**
     * Retrieves list of file objects inside specified directory.
     * 
     * @param directoryId directory Id
     * @param pincode access code
     * @return list of fileObjects in this directory
     * @throws FlashSafeStorageException
     */
    List<FlashSafeStorageFileObject> list(String directoryHash, String pincode) throws FlashSafeStorageException;
    
    /**
     * Retrieves list of file objects inside trash.
     * 
     * @return list of fileObjects in trash
     * @throws FlashSafeStorageException
     */
    List<FlashSafeStorageFileObject> trashList() throws FlashSafeStorageException;
    
    /**
     * Creates directory with specified name in parent directory.
     * 
     * @param parentDirectoryId parent directory Id
     * @param name name for directory
     * @return created {@link FlashSafeStorageDirectory} instance
     * @throws FlashSafeStorageException
     */
    FlashSafeStorageDirectory createDirectory(String parentDirectoryHash, String name) throws FlashSafeStorageException;
    
    FlashSafeStorageFile createEmptyFile(String parentDirectoryHash, String name) throws FlashSafeStorageException;

    StorageFileOperation downloadFile(String fileHash, Path directory) throws FlashSafeStorageException;

    StorageFileOperation uploadFile(String directoryHash, Path file) throws FlashSafeStorageException;
    
    StorageFileOperation uploadFilePart(String directoryHash, String fhash, long part_num, Path file) throws FlashSafeStorageException;
    
    void copy(String fileObjectHash, String destinationDirectoryHash) throws FlashSafeStorageException;

    void move(String fileObjectHash, String destinationDirectoryHash) throws FlashSafeStorageException;

    void delete(String fileObjectHash) throws FlashSafeStorageException;
    
    void rename(String fileObject, String name) throws FlashSafeStorageException;
    
    List<FlashSafeStorageFileObject> getTree() throws FlashSafeStorageException;
}
