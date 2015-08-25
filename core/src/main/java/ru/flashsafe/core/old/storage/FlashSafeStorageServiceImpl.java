package ru.flashsafe.core.old.storage;

import java.nio.file.Path;
import java.util.List;

import ru.flashsafe.core.storage.FlashSafeStorageService;
import ru.flashsafe.core.storage.StorageFileOperation;
import ru.flashsafe.core.storage.exception.FlashSafeStorageException;

public class FlashSafeStorageServiceImpl implements FlashSafeStorageService {
    
    @Override
    public List<FlashSafeStorageFileObject> list(String path) throws FlashSafeStorageException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public FlashSafeStorageDirectory createDirectory(String path) throws FlashSafeStorageException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StorageFileOperation downloadFile(String path, Path directory) throws FlashSafeStorageException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StorageFileOperation uploadFile(String path, Path file) throws FlashSafeStorageException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void copy(String fromPath, String toPath) throws FlashSafeStorageException {
        // TODO Auto-generated method stub

    }

    @Override
    public void move(String fromPath, String toPath) throws FlashSafeStorageException {
        // TODO Auto-generated method stub

    }

    @Override
    public void delete(String path) throws FlashSafeStorageException {
        // TODO Auto-generated method stub

    }

}
