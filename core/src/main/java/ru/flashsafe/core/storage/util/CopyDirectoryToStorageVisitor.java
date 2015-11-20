package ru.flashsafe.core.storage.util;

import static ru.flashsafe.core.storage.util.StorageUtils.STORAGE_PATH_SEPARATOR;
import static ru.flashsafe.core.storage.util.StorageUtils.convertToFlashSafeStoragePath;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.flashsafe.core.old.storage.FlashSafeStorageFileObject;
import ru.flashsafe.core.old.storage.FlashSafeStorageIdBasedService;
import ru.flashsafe.core.old.storage.FlashSafeStorageNullFileObject;
import ru.flashsafe.core.old.storage.ResourceResolver;
import ru.flashsafe.core.operation.OperationResult;
import ru.flashsafe.core.storage.CompositeFileStorageOperation;
import ru.flashsafe.core.storage.StorageFileOperation;
import ru.flashsafe.core.storage.StorageOperationType;
import ru.flashsafe.core.storage.exception.FlashSafeStorageException;
import ru.flashsafe.core.storage.exception.ResourceResolverException;

/**
 * File visitor which allows to copy a directory structure to FlashSafe storage.
 * 
 * @author Andrew
 *
 */
public class CopyDirectoryToStorageVisitor extends SimpleFileVisitor<Path> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CopyDirectoryToStorageVisitor.class);
    
    private final Path fromPath;
    
    private final FlashSafeStorageFileObject toPath;
    
    private final FlashSafeStorageIdBasedService storageService;
    
    private final ResourceResolver resolver;
    
    private final CompositeFileStorageOperation operation;

    public CopyDirectoryToStorageVisitor(Path fromPath, FlashSafeStorageFileObject toPath, FlashSafeStorageIdBasedService storageService,
            ResourceResolver resourceResolver, CompositeFileStorageOperation operation) {
        this.fromPath = fromPath;
        this.toPath = toPath;
        this.storageService = storageService;
        this.operation = operation;
        resolver = resourceResolver;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        String resourcePath = convertToFlashSafeStoragePath(fromPath.relativize(dir));
        FlashSafeStorageFileObject directory = resolveResourceIfExists(toPath, resourcePath);
        if (directory == FlashSafeStorageNullFileObject.NULL_OBJECT) {
            LOGGER.debug("Creating directory {}{}{}", toPath.getAbsolutePath(), STORAGE_PATH_SEPARATOR, resourcePath);
            FlashSafeStorageFileObject parentDirectory = toPath;
            int lastElementPosition = resourcePath.lastIndexOf(STORAGE_PATH_SEPARATOR);
            if (lastElementPosition > 0) {
                parentDirectory = resolveResource(toPath, resourcePath.substring(0, lastElementPosition));
            }
            createDirectory(parentDirectory.getId(), resourcePath.substring(lastElementPosition + 1));
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (Thread.currentThread().isInterrupted()) {
            markOperationAsCanceled(operation);
            LOGGER.debug("Stopping copy operation");
            return FileVisitResult.TERMINATE;
        }
        String resourcePath = convertToFlashSafeStoragePath(fromPath.relativize(file));
        FlashSafeStorageFileObject fileDirectory = toPath;
        if (!resourcePath.isEmpty()) {
            int pathEnd = resourcePath.lastIndexOf(STORAGE_PATH_SEPARATOR);
            if (pathEnd > 0) {
                resourcePath = resourcePath.substring(0, pathEnd);
                fileDirectory = resolveResource(toPath, resourcePath);
            }
        }
        boolean shouldContinue;
        if(operation.getStorageOperationType() == StorageOperationType.DOWNLOAD) {
            LOGGER.debug("Downloading file {} to {}{}{}", file, toPath.getAbsolutePath(), STORAGE_PATH_SEPARATOR, resourcePath);
            StorageFileOperation downloadOperation = downloadFile(((FlashSafeStorageFileObject) fromPath).getId(), Paths.get(toPath.getAbsolutePath()));
            operation.setCurrentOperation(downloadOperation);
            shouldContinue = waitUntilFinished(downloadOperation);
            operation.submitCurrentOperationAsFinished();
        } else {
            LOGGER.debug("Uploading file {} to {}{}{}", file, toPath.getAbsolutePath(), STORAGE_PATH_SEPARATOR, resourcePath);
            StorageFileOperation uploadOperation = uploadFile(fileDirectory.getId(), file);
            operation.setCurrentOperation(uploadOperation);
            shouldContinue = waitUntilFinished(uploadOperation);
            operation.submitCurrentOperationAsFinished();
        }
        return shouldContinue ? FileVisitResult.CONTINUE : FileVisitResult.TERMINATE;
    }
    
    private FlashSafeStorageFileObject resolveResource(FlashSafeStorageFileObject parent, String name) throws IOException {
        try {
            return resolver.resolveResource(parent, name);
        } catch (ResourceResolverException e) {
            // TODO add message
            LOGGER.warn("Error while resolving resource " + name, e);
            throw new IOException("Error while resolving resource " + name, e);
        }
    }

    private FlashSafeStorageFileObject resolveResourceIfExists(FlashSafeStorageFileObject parent, String name) throws IOException {
            return resolver.resolveResourceIfExists(parent, name);
    }

    private StorageFileOperation uploadFile(long directoryId, Path file) throws IOException {
        try {
            return storageService.uploadFile(directoryId, file);
        } catch (FlashSafeStorageException e) {
            LOGGER.warn("Error while uploading file " + file, e);
            throw new IOException("Error while uploading file " + file, e);
        }
    }
    
    private StorageFileOperation downloadFile(long fileId, Path directory) throws IOException {
        try {
            return storageService.downloadFile(fileId, directory);
        } catch (FlashSafeStorageException e) {
            LOGGER.warn("Error while downloading file with id " + fileId, e);
            throw new IOException("Error while downloading file with id " + fileId, e);
        }
    }

    private void createDirectory(long parentDirectoryId, String name) throws IOException {
        try {
            storageService.createDirectory(parentDirectoryId, name);
        } catch (FlashSafeStorageException e) {
            LOGGER.warn("Error while creating directory " + name, e);
            throw new IOException("Error while creating directory " + name, e);
        }
    }
    
    /**
     * 
     * 
     * @param uploadStatus
     * @return true if continue, false otherwise
     * @throws IOException
     */
    private boolean waitUntilFinished(StorageFileOperation uploadOperation) {
        try {
            uploadOperation.waitUntilFinished();
            return true;
        } catch (InterruptedException e) {
            uploadOperation.stop();
            markOperationAsCanceled(operation);
            LOGGER.debug("Operation was stopped", e);
            return false;
        }
    }
    
    private static void markOperationAsCanceled(CompositeFileStorageOperation operation) {
        operation.setResult(OperationResult.CANCELED);
    }
}
