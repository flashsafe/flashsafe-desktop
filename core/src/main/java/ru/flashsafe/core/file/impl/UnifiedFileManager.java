package ru.flashsafe.core.file.impl;

import java.util.List;

import ru.flashsafe.core.file.Directory;
import ru.flashsafe.core.file.File;
import ru.flashsafe.core.file.FileManager;
import ru.flashsafe.core.file.FileObject;
import ru.flashsafe.core.localfs.LocalFileManager;
import ru.flashsafe.core.old.storage.DefaultFlashSafeStorageService;
import ru.flashsafe.core.storage.FlashSafeStorageFileManager;
import ru.flashsafe.core.storage.FlashSafeStorageService;

public class UnifiedFileManager implements FileManager {
    
    private final LocalFileManager localFileSystemManager;
    
    private final FlashSafeStorageFileManager flashSafeStorageFileManager;
    
    private final FlashSafeStorageService flashSafeStorageService;
    
    protected  UnifiedFileManager() {
        localFileSystemManager = new LocalFileManager();
        flashSafeStorageService = new DefaultFlashSafeStorageService(); 
        flashSafeStorageFileManager = new FlashSafeStorageFileManager(flashSafeStorageService);
    }
    
    @Override
    public List<FileObject> list(String path) {
        FileManager fileManager = getFileManagerForPath(path);
        return fileManager.list(path);
    }

    @Override
    public File createFile(String path) {
        FileManager fileManager = getFileManagerForPath(path);
        return fileManager.createFile(path);
    }

    @Override
    public Directory createDirectory(String path) {
        FileManager fileManager = getFileManagerForPath(path);
        return fileManager.createDirectory(path);
    }

    @Override
    public void copy(String fromPath, String toPath) {
        if (pathsRelateToSameStorage(fromPath, toPath)) {
            FileManager fileManager = getFileManagerForPath(fromPath);
            fileManager.copy(fromPath, toPath);
        } else {
            
        }
    }

    @Override
    public void move(String fromPath, String toPath) {
        if (pathsRelateToSameStorage(fromPath, toPath)) {
            FileManager fileManager = getFileManagerForPath(fromPath);
            fileManager.move(fromPath, toPath);
        } else {
            
        }
    }

    @Override
    public void delete(String path) {
        FileManager fileManager = getFileManagerForPath(path);
        fileManager.delete(path);
    }
    
    private static boolean pathsRelateToSameStorage(String firstPath, String secondPath) {
        boolean firstPathOnRemoteStorage = firstPath.startsWith(FlashSafeStorageService.FLASH_SAFE_STORAGE_PATH_PREFIX);
        boolean secondPathOnRemoteStorage = secondPath.startsWith(FlashSafeStorageService.FLASH_SAFE_STORAGE_PATH_PREFIX);
        return firstPathOnRemoteStorage == secondPathOnRemoteStorage;
    }
    
    private FileManager getFileManagerForPath(String path) {
        return path.startsWith(FlashSafeStorageService.FLASH_SAFE_STORAGE_PATH_PREFIX) ? flashSafeStorageFileManager
                : localFileSystemManager;
    }

}
