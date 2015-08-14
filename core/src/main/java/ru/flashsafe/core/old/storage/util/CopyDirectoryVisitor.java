package ru.flashsafe.core.old.storage.util;

import static ru.flashsafe.core.old.storage.util.TransferUtility.convertToFlashSafeStoragePath;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.flashsafe.core.file.FileOperationType;
import ru.flashsafe.core.file.impl.FileOperationStatusComposite;
import ru.flashsafe.core.old.storage.FlashSafeStorageFileObject;
import ru.flashsafe.core.storage.FlashSafeStorageService;
import ru.flashsafe.core.storage.ResourceResolver;
import ru.flashsafe.core.storage.StorageOperationStatus;
import ru.flashsafe.core.storage.ToFileOperationStatusAdapter;
import ru.flashsafe.core.storage.exception.FlashSafeStorageException;
import ru.flashsafe.core.storage.exception.ResourceResolverException;

public class CopyDirectoryVisitor extends SimpleFileVisitor<Path> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CopyDirectoryVisitor.class);
    
    private final Path fromPath;
    
    private final FlashSafeStorageFileObject toPath;
    
    private final FlashSafeStorageService storageService;
    
    private final ResourceResolver resolver;
    
    private final FileOperationStatusComposite operationStatus;

    public CopyDirectoryVisitor(Path fromPath, FlashSafeStorageFileObject toPath, FlashSafeStorageService storageService,
            FileOperationStatusComposite operationStatus) {
        this.fromPath = fromPath;
        this.toPath = toPath;
        this.storageService = storageService;
        this.operationStatus = operationStatus;
        resolver = new ResourceResolver(storageService);
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        String resourcePath = convertToFlashSafeStoragePath(fromPath.relativize(dir));
        FlashSafeStorageFileObject directory = resolveResourceIfExists(toPath, resourcePath);
        if (directory == null) {
            LOGGER.debug("Creating directory {}/{}", toPath, resourcePath);
            FlashSafeStorageFileObject parentDirectory = toPath;
            int lastElementPosition = resourcePath.lastIndexOf("/");
            if (lastElementPosition > 0) {
                parentDirectory = resolveResource(toPath, resourcePath.substring(0, lastElementPosition));
            }
            createDirectory(parentDirectory.getId(), resourcePath.substring(lastElementPosition + 1));
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        String resourcePath = convertToFlashSafeStoragePath(fromPath.relativize(file));
        FlashSafeStorageFileObject fileDirectory = toPath;
        if (!resourcePath.isEmpty()) {
            int pathEnd = resourcePath.lastIndexOf("/");
            if (pathEnd > 0) {
                resourcePath = resourcePath.substring(0, pathEnd);
                fileDirectory = resolveResource(toPath, resourcePath);
            }
        }
        LOGGER.debug("Uploading file {} to {}", file, resourcePath);
        StorageOperationStatus uploadStatus = uploadFile(fileDirectory.getId(), file);
        operationStatus.setActiveOperationStatus(new ToFileOperationStatusAdapter(uploadStatus, FileOperationType.COPY));
        //TODO add to the common status
        waitUntilFinished(uploadStatus);
        operationStatus.submitActiveOperationStatusAsFinished();
        return FileVisitResult.CONTINUE;
    }
    
    private FlashSafeStorageFileObject resolveResource(FlashSafeStorageFileObject parent, String name) throws IOException {
        try {
            return resolver.resolveResource(parent, name);
        } catch (ResourceResolverException e) {
            // TODO add message
            LOGGER.warn("", e);
            throw new IOException("", e);
        }
    }

    private FlashSafeStorageFileObject resolveResourceIfExists(FlashSafeStorageFileObject parent, String name) throws IOException {
        try {
            return resolver.resolveResourceIfExists(parent, name);
        } catch (ResourceResolverException e) {
            // TODO add message
            LOGGER.warn("", e);
            throw new IOException("", e);
        }
    }

    private StorageOperationStatus uploadFile(long directoryId, Path file) throws IOException {
        try {
            return storageService.uploadFile(directoryId, file);
        } catch (FlashSafeStorageException e) {
            // TODO add message
            LOGGER.warn("", e);
            throw new IOException("", e);
        }
    }

    private void createDirectory(long parentDirectoryId, String name) throws IOException {
        try {
            storageService.createDirectory(parentDirectoryId, name);
        } catch (FlashSafeStorageException e) {
            // TODO add message
            LOGGER.warn("", e);
            throw new IOException("", e);
        }
    }
    
    private static void waitUntilFinished(StorageOperationStatus uploadStatus) throws IOException {
        try {
            uploadStatus.waitUntilFinished();
        } catch (InterruptedException e) {
         // TODO add message
            LOGGER.warn("", e);
            throw new IOException("", e);
        }
    }
}
