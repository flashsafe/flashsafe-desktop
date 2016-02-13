package ru.flashsafe.core.file.impl;

import static java.util.Objects.requireNonNull;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.flashsafe.core.file.Directory;
import ru.flashsafe.core.file.File;
import ru.flashsafe.core.file.FileManager;
import ru.flashsafe.core.file.FileObject;
import ru.flashsafe.core.file.FileOperation;
import ru.flashsafe.core.file.exception.FileOperationException;
import ru.flashsafe.core.localfs.LocalFileManager;
import ru.flashsafe.core.storage.FlashSafeStorageFileManager;
import ru.flashsafe.core.storage.FlashSafeStorageService;
import ru.flashsafe.core.storage.exception.FlashSafeStorageException;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * 
 * This file manager is able to deal with files located on current computer or FlashSafe storage.
 * 
 * @author Andrew
 *
 */
@Singleton
public class UnifiedFileManager implements FileManager {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(UnifiedFileManager.class);

    private final LocalFileManager localFileSystemManager;

    private final FlashSafeStorageFileManager flashSafeStorageFileManager;
    
    private final FlashSafeStorageService flashSafeStorageService;

    @Inject
    UnifiedFileManager(LocalFileManager localFileSystemManager, FlashSafeStorageFileManager flashSafeStorageFileManager,
            FlashSafeStorageService flashSafeStorageService) {
        this.localFileSystemManager = localFileSystemManager;
        this.flashSafeStorageFileManager = flashSafeStorageFileManager;
        this.flashSafeStorageService = flashSafeStorageService;
    }

    @Override
    public List<FileObject> list(String path) throws FileOperationException {
        requireNonNull(path);
        FileManager fileManager = getFileManagerForPath(path);
        List<FileObject> fileObjects = fileManager.list(path);
        return (fileObjects == null) ? Collections.<FileObject> emptyList() : fileObjects;
    }

    @Override
    public File createFile(String path) throws FileOperationException {
        requireNonNull(path);
        FileManager fileManager = getFileManagerForPath(path);
        return fileManager.createFile(path);
    }

    @Override
    public Directory createDirectory(String path) throws FileOperationException {
        requireNonNull(path);
        FileManager fileManager = getFileManagerForPath(path);
        return fileManager.createDirectory(path);
    }

    @Override
    public FileOperation copy(String fromPath, String toPath) throws FileOperationException {
        requireNonNull(fromPath);
        requireNonNull(toPath);
        if (pathsRelateToSameStorage(fromPath, toPath)) {
            FileManager fileManager = getFileManagerForPath(fromPath);
            return fileManager.copy(fromPath, toPath);
        } else {
            if (isRemoteStoragePath(toPath)) {
                try {
                    return flashSafeStorageService.upload(Paths.get(fromPath), toPath);
                } catch (FlashSafeStorageException e) {
                    LOGGER.warn("Error while copying file" + fromPath + " to storage", e);
                    throw new FileOperationException("Error while copying file" + fromPath + " to storage", e);
                }
                //return flashSafeStorageServiceHelper.copyToStorage(fromPath, toPath);
            } else {
                try {
                    return flashSafeStorageService.download(fromPath, Paths.get(toPath));
                } catch (FlashSafeStorageException e) {
                    LOGGER.warn("Error while copying file" + fromPath + " to storage", e);
                    throw new FileOperationException("Error while copying file" + fromPath + " to storage", e);
                }
            }
        }
    }

    @Override
    public FileOperation move(String fromPath, String toPath) throws FileOperationException {
        requireNonNull(fromPath);
        requireNonNull(toPath);
        if (pathsRelateToSameStorage(fromPath, toPath)) {
            FileManager fileManager = getFileManagerForPath(fromPath);
            return fileManager.move(fromPath, toPath);
        } else {
            //TODO implement move to/from cloud storage
            throw new UnsupportedOperationException("Not implemented yet!");
        }
    }

    @Override
    public FileOperation delete(String path) throws FileOperationException {
        requireNonNull(path);
        FileManager fileManager = getFileManagerForPath(path);
        return fileManager.delete(path);
    }

    private static boolean pathsRelateToSameStorage(String firstPath, String secondPath) {
        boolean firstPathOnRemoteStorage = firstPath.startsWith(FileManager.FLASH_SAFE_STORAGE_PATH_PREFIX);
        boolean secondPathOnRemoteStorage = secondPath.startsWith(FileManager.FLASH_SAFE_STORAGE_PATH_PREFIX);
        return firstPathOnRemoteStorage == secondPathOnRemoteStorage;
    }

    private FileManager getFileManagerForPath(String path) {
        return path.startsWith(FileManager.FLASH_SAFE_STORAGE_PATH_PREFIX) ? flashSafeStorageFileManager
                : localFileSystemManager;
    }
    
    private static boolean isRemoteStoragePath(String path) {
        return path.startsWith(FileManager.FLASH_SAFE_STORAGE_PATH_PREFIX);
    }
}
