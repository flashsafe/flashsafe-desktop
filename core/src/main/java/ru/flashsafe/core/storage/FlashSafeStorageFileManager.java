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
import ru.flashsafe.core.old.storage.FlashSafeStorageFileObject;
import ru.flashsafe.core.operation.OperationRegistry;
import ru.flashsafe.core.storage.exception.FlashSafeStorageException;
import ru.flashsafe.core.storage.exception.ResourceResolverException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.google.inject.Inject;

public class FlashSafeStorageFileManager implements FileManager {
    
    private static final char PATH_SEPARATOR = '/';
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FlashSafeStorageFileManager.class);
    
    private final FlashSafeStorageService storageService;

    private final ResourceResolver resourceResolver;

    @Inject
    FlashSafeStorageFileManager(FlashSafeStorageService storageService, ResourceResolver resourceResolver) {
        this.storageService = storageService;
        this.resourceResolver = resourceResolver;
    }

    @Override
    public List<FileObject> list(String path) throws FileOperationException {
        try {
            FlashSafeStorageFileObject resource = resourceResolver.resolveResource(path);
            return cast(storageService.list(resource.getId()));
        } catch (FlashSafeStorageException | ResourceResolverException e) {
            throw new FileOperationException("Error while listing directory", e);
        }
    }

    @Override
    public File createFile(String path) {
        // TODO Auto-generated method stub
        throw new NotImplementedException();
    }

    @Override
    public Directory createDirectory(String path) throws FileOperationException {
        int lastPathSeparatorIndex = path.lastIndexOf(PATH_SEPARATOR, FileManager.FLASH_SAFE_STORAGE_PATH_PREFIX.length());
        String parentDirectoryPath = FileManager.FLASH_SAFE_STORAGE_PATH_PREFIX;
        if (lastPathSeparatorIndex > FileManager.FLASH_SAFE_STORAGE_PATH_PREFIX.length()) {
            parentDirectoryPath = path.substring(0, lastPathSeparatorIndex);
        }
        try {
            FlashSafeStorageFileObject parentDirectory = resourceResolver.resolveResource(parentDirectoryPath);
            String newDirectoryName = path.substring(path.lastIndexOf(PATH_SEPARATOR) + 1);
            return storageService.createDirectory(parentDirectory.getId(), newDirectoryName);
        } catch (FlashSafeStorageException | ResourceResolverException e) {
            LOGGER.warn("Error while creating directory " + path, e);
            throw new FileOperationException("Error while creating directory " + path, e);
        }
    }

    @Override
    public FileOperation copy(String fromPath, String toPath) throws FileOperationException {
        try {
            FlashSafeStorageFileObject resourceToCopy = resourceResolver.resolveResource(fromPath);
            FlashSafeStorageFileObject targetDirectory = resourceResolver.resolveResource(toPath);
            storageService.copy(resourceToCopy.getId(), targetDirectory.getId());
            return null;
        } catch (FlashSafeStorageException | ResourceResolverException e) {
            LOGGER.warn("Error while copying directory " + fromPath + " to " + toPath, e);
            throw new FileOperationException("Error while copying directory", e);
        }
    }

    @Override
    public FileOperation move(String fromPath, String toPath) throws FileOperationException {
        try {
            FlashSafeStorageFileObject resourceToCopy = resourceResolver.resolveResource(fromPath);
            FlashSafeStorageFileObject targetDirectory = resourceResolver.resolveResource(toPath);
            storageService.move(resourceToCopy.getId(), targetDirectory.getId());
            return null;
        } catch (FlashSafeStorageException | ResourceResolverException e) {
            LOGGER.warn("Error while moving " + fromPath + " to " + toPath, e);
            throw new FileOperationException("Error while moving ", e);
        }
    }

    @Override
    public FileOperation delete(String path) throws FileOperationException {
        try {
            FlashSafeStorageFileObject resource = resourceResolver.resolveResource(path);
            storageService.delete(resource.getId());
            return null;
        } catch (FlashSafeStorageException | ResourceResolverException e) {
            LOGGER.warn("Error while deleting " + path, e);
            throw new FileOperationException("Error while deleting ", e);
        }
    }
    
    @SuppressWarnings("unchecked")
    private static List<FileObject> cast(List<? extends FileObject> fileObject) {
        return (List<FileObject>) fileObject;
    }
}
