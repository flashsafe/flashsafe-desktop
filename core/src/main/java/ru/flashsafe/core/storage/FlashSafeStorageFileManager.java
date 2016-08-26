package ru.flashsafe.core.storage;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.flashsafe.core.file.Directory;
import ru.flashsafe.core.file.File;
import ru.flashsafe.core.file.FileManager;
import ru.flashsafe.core.file.FileObject;
import ru.flashsafe.core.file.FileOperation;
import ru.flashsafe.core.file.exception.FileOperationException;
import ru.flashsafe.core.storage.exception.FlashSafeStorageException;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import ru.flashsafe.core.old.storage.FlashSafeStorageFileObject;

@Singleton
public class FlashSafeStorageFileManager implements FileManager {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FlashSafeStorageFileManager.class);

    private final FlashSafeStorageService storageService;
    
    @Inject
    FlashSafeStorageFileManager(FlashSafeStorageService storageService) {
        this.storageService = storageService;
    }
    
    @Override
    public List<FileObject> list(String path) throws FileOperationException {
        try {
            return cast(storageService.list(path));
        } catch (FlashSafeStorageException e) {
            throw new FileOperationException("Error while listing directory", e);
        }
    }
    
    @Override
    public List<FileObject> trashList() throws FileOperationException {
        try {
            return cast(storageService.trashList());
        } catch (FlashSafeStorageException e) {
            throw new FileOperationException("Error while listing trash", e);
        }
    }

    @Override
    public File createFile(String path) throws FileOperationException {
    	try {
            return storageService.createEmptyFile(path);
        } catch (FlashSafeStorageException e) {
            LOGGER.warn("Error while creating directory " + path, e);
            throw new FileOperationException("Error while creating directory " + path, e);
        }
    }

    @Override
    public Directory createDirectory(String parentHash, String path) throws FileOperationException {
        try {
            return storageService.createDirectory(parentHash, path);
        } catch (FlashSafeStorageException e) {
            LOGGER.warn("Error while creating directory " + path, e);
            throw new FileOperationException("Error while creating directory " + path, e);
        }
    }

    @Override
    public FileOperation copy(String fromPath, String toPath) throws FileOperationException {
        try {
            storageService.copy(fromPath, toPath);
            return null;
        } catch (FlashSafeStorageException e) {
            LOGGER.warn("Error while copying directory " + fromPath + " to " + toPath, e);
            throw new FileOperationException("Error while copying directory", e);
        }
    }

    @Override
    public FileOperation move(String fromPath, String toPath) throws FileOperationException {
        try {
            storageService.move(fromPath, toPath);
            return null;
        } catch (FlashSafeStorageException e) {
            LOGGER.warn("Error while moving " + fromPath + " to " + toPath, e);
            throw new FileOperationException("Error while moving ", e);
        }
    }

    @Override
    public FileOperation delete(String path) throws FileOperationException {
        try {
            storageService.delete(path);
            return null;
        } catch (FlashSafeStorageException e) {
            LOGGER.warn("Error while deleting " + path, e);
            throw new FileOperationException("Error while deleting ", e);
        }
    }
    
    @Override
    public FileOperation rename(String fileObjectHash, String name) throws FileOperationException {
        try {
            storageService.rename(fileObjectHash, name);
            return null;
        } catch (FlashSafeStorageException e) {
            LOGGER.warn("Error while rename to " + name, e);
            throw new FileOperationException("Error while rename ", e);
        }
    }
    
    @SuppressWarnings("unchecked")
    private static List<FileObject> cast(List<? extends FileObject> fileObject) {
        return (List<FileObject>) fileObject;
    }

    @Override
    public List<FlashSafeStorageFileObject> getTree() throws FileOperationException {
        return storageService.getTree();
    }

}
