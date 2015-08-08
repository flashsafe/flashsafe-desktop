package ru.flashsafe.core.storage;

import java.nio.file.Path;
import java.util.List;

import ru.flashsafe.core.old.storage.FlashSafeStorageDirectory;
import ru.flashsafe.core.old.storage.FlashSafeStorageFileObject;
import ru.flashsafe.core.storage.exception.FlashSafeStorageException;

public interface FlashSafeStorageService {

    String FLASH_SAFE_STORAGE_PATH_PREFIX = "fls://";

    List<FlashSafeStorageFileObject> list(long directoryId) throws FlashSafeStorageException;

    FlashSafeStorageDirectory createDirectory(long parentDirectoryId, String name) throws FlashSafeStorageException;

    StorageOperationStatus downloadFile(long fileId, Path directory) throws FlashSafeStorageException;

    StorageOperationStatus uploadFile(long directoryId, Path file) throws FlashSafeStorageException;

    void copy(long fileObjectId, long destinationDirectoryId) throws FlashSafeStorageException;

    void move(long fileObjectId, long destinationDirectoryId) throws FlashSafeStorageException;

    void delete(long fileObjectId) throws FlashSafeStorageException;
    
}
