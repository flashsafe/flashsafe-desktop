package ru.flashsafe.core.storage;

import java.nio.file.Path;
import java.util.List;

import ru.flashsafe.core.old.storage.FlashSafeStorageDirectory;
import ru.flashsafe.core.old.storage.FlashSafeStorageFile;
import ru.flashsafe.core.old.storage.FlashSafeStorageFileObject;
import ru.flashsafe.core.storage.exception.FlashSafeStorageException;

public interface FlashSafeStorageService {

    /**
     * Retrieves list of file objects inside specified directory.
     * 
     * @param directoryId directory Id
     * @return list of fileObjects in this directory
     * @throws FlashSafeStorageException
     */
    List<FlashSafeStorageFileObject> list(String path) throws FlashSafeStorageException;
    
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
    FlashSafeStorageDirectory createDirectory(String path) throws FlashSafeStorageException;
    
    FlashSafeStorageFile createEmptyFile(String path) throws FlashSafeStorageException;

    StorageFileOperation download(String remoteObjectPath, Path localDirectoryPath) throws FlashSafeStorageException;
    
    StorageFileOperation upload(Path localObjectPath, String remoteDirectoryPath) throws FlashSafeStorageException;

    void copy(String fromPath, String toPath) throws FlashSafeStorageException;

    void move(String fromPath, String toPath) throws FlashSafeStorageException;

    void delete(String path) throws FlashSafeStorageException;
    
    void rename(long fileObjectId, String name) throws FlashSafeStorageException;
    
}
