package ru.flashsafe.core.storage;

import java.nio.file.Path;
import java.util.List;

import ru.flashsafe.core.old.storage.FlashSafeStorageDirectory;
import ru.flashsafe.core.old.storage.FlashSafeStorageFileObject;

public interface FlashSafeStorageService {

    String FLASH_SAFE_STORAGE_PATH_PREFIX = "fls://";

    List<FlashSafeStorageFileObject> list(long directoryId);

    FlashSafeStorageDirectory createDirectory(long parentDirectoryId, String name);

    void downloadFile(long fileId, Path directory);

    void uploadFile(long directoryId, Path file);

    void copy(long fileObjectId, long destinationDirectoryId);

    void move(long fileObjectId, long destinationDirectoryId);

    void delete(long fileObjectId);

}
