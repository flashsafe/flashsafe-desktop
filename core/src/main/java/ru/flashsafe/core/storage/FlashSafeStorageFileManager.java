package ru.flashsafe.core.storage;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.flashsafe.core.file.Directory;
import ru.flashsafe.core.file.File;
import ru.flashsafe.core.file.FileManager;
import ru.flashsafe.core.file.FileObject;
import ru.flashsafe.core.file.FileOperationStatus;
import ru.flashsafe.core.file.exception.FileOperationException;
import ru.flashsafe.core.old.storage.FlashSafeStorageFileObject;
import ru.flashsafe.core.storage.exception.FlashSafeStorageException;
import ru.flashsafe.core.storage.exception.ResourceResolverException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class FlashSafeStorageFileManager implements FileManager {
    
    private static final char PATH_SEPARATOR = '/';
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FlashSafeStorageFileManager.class);
    
    private FlashSafeStorageService storageService;

    private ResourceResolver resolver;

    public FlashSafeStorageFileManager(FlashSafeStorageService storageService) {
        this.storageService = storageService;
        resolver = new ResourceResolver(storageService);
    }

    @Override
    public List<FileObject> list(String path) throws FileOperationException {
        try {
            FlashSafeStorageFileObject resource = resolver.resolveResource(path);
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
        int lastPathSeparatorIndex = path.lastIndexOf(PATH_SEPARATOR, FlashSafeStorageService.FLASH_SAFE_STORAGE_PATH_PREFIX.length());
        String parentDirectoryPath = FlashSafeStorageService.FLASH_SAFE_STORAGE_PATH_PREFIX;
        if (lastPathSeparatorIndex > FlashSafeStorageService.FLASH_SAFE_STORAGE_PATH_PREFIX.length()) {
            parentDirectoryPath = path.substring(0, lastPathSeparatorIndex);
        }
        try {
            FlashSafeStorageFileObject parentDirectory = resolver.resolveResource(parentDirectoryPath);
            String newDirectoryName = path.substring(path.lastIndexOf(PATH_SEPARATOR) + 1);
            return storageService.createDirectory(parentDirectory.getId(), newDirectoryName);
        } catch (FlashSafeStorageException | ResourceResolverException e) {
            throw new FileOperationException("Error while creating directory", e);
        }
    }

    @Override
    public FileOperationStatus copy(String fromPath, String toPath) throws FileOperationException {
        try {
            FlashSafeStorageFileObject resourceToCopy = resolver.resolveResource(fromPath);
            FlashSafeStorageFileObject targetDirectory = resolver.resolveResource(toPath);
            storageService.copy(resourceToCopy.getId(), targetDirectory.getId());
            return null;
        } catch (FlashSafeStorageException | ResourceResolverException e) {
            //TODO fix messages
            LOGGER.warn("Error while copying directory ", e);
            throw new FileOperationException("Error while copying directory", e);
        }
    }

    @Override
    public FileOperationStatus move(String fromPath, String toPath) throws FileOperationException {
        try {
            FlashSafeStorageFileObject resourceToCopy = resolver.resolveResource(fromPath);
            FlashSafeStorageFileObject targetDirectory = resolver.resolveResource(toPath);
            storageService.move(resourceToCopy.getId(), targetDirectory.getId());
            return null;
        } catch (FlashSafeStorageException | ResourceResolverException e) {
          //TODO edit message
            LOGGER.warn("Error while moving ", e);
            throw new FileOperationException("Error while moving ", e);
        }
    }

    @Override
    public FileOperationStatus delete(String path) throws FileOperationException {
        try {
            FlashSafeStorageFileObject resource = resolver.resolveResource(path);
            storageService.delete(resource.getId());
            return null;
        } catch (FlashSafeStorageException | ResourceResolverException e) {
            //TODO edit message
            LOGGER.warn("Error while deleting ", e);
            throw new FileOperationException("Error while deleting ", e);
        }
    }
    
    @SuppressWarnings("unchecked")
    private static List<FileObject> cast(List<? extends FileObject> fileObject) {
        return (List<FileObject>) fileObject;
    }
}
