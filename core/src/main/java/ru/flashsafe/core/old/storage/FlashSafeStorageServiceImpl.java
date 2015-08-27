package ru.flashsafe.core.old.storage;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.flashsafe.core.FlashSafeRegistry;
import ru.flashsafe.core.event.ApplicationStopEvent;
import ru.flashsafe.core.event.FlashSafeEventService;
import ru.flashsafe.core.file.FileManager;
import ru.flashsafe.core.file.FileObjectType;
import ru.flashsafe.core.storage.FlashSafeStorageService;
import ru.flashsafe.core.storage.StorageFileOperation;
import ru.flashsafe.core.storage.exception.FlashSafeStorageException;
import ru.flashsafe.core.storage.exception.ResourceResolverException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

public class FlashSafeStorageServiceImpl implements FlashSafeStorageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlashSafeStorageServiceImpl.class);

    private static final char PATH_SEPARATOR = '/';

    private final ResourceResolver resourceResolver;

    private final FlashSafeStorageIdBasedService storageService;

    private final ExecutorService executorService = Executors.newFixedThreadPool(FlashSafeRegistry
            .readProperty(FlashSafeRegistry.LOCAL_TO_STORAGE_SIMULTANEOUSLY_EXECUTED_OPERATIONS));

    @Inject
    FlashSafeStorageServiceImpl(FlashSafeStorageIdBasedService storageService, ResourceResolver resourceResolver,
            FlashSafeEventService eventService) {
        this.storageService = storageService;
        this.resourceResolver = resourceResolver;
        eventService.registerSubscriber(this);
    }

    @Subscribe
    public void handleApplicationStopEvent(ApplicationStopEvent event) {
        LOGGER.info("Handling application stop event");
        executorService.shutdownNow();
        try {
            executorService.awaitTermination(500, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            LOGGER.error("Shutdown process finished with an error", e);
        }
    }

    @Override
    public List<FlashSafeStorageFileObject> list(String path) throws FlashSafeStorageException {
        try {
            FlashSafeStorageFileObject resource = resourceResolver.resolveResource(path);
            return storageService.list(resource.getId());
        } catch (ResourceResolverException e) {
            throw new FlashSafeStorageException("Error while listing directory", e);
        }
    }

    @Override
    public FlashSafeStorageDirectory createDirectory(String path) throws FlashSafeStorageException {
        int lastPathSeparatorIndex = path.lastIndexOf(PATH_SEPARATOR, FileManager.FLASH_SAFE_STORAGE_PATH_PREFIX.length());
        String parentDirectoryPath = FileManager.FLASH_SAFE_STORAGE_PATH_PREFIX;
        if (lastPathSeparatorIndex > FileManager.FLASH_SAFE_STORAGE_PATH_PREFIX.length()) {
            parentDirectoryPath = path.substring(0, lastPathSeparatorIndex);
        }
        try {
            FlashSafeStorageFileObject parentDirectory = resourceResolver.resolveResource(parentDirectoryPath);
            String newDirectoryName = path.substring(path.lastIndexOf(PATH_SEPARATOR) + 1);
            return storageService.createDirectory(parentDirectory.getId(), newDirectoryName);
        } catch (ResourceResolverException e) {
            LOGGER.warn("Error while creating directory " + path, e);
            throw new FlashSafeStorageException("Error while creating directory " + path, e);
        }
    }

    @Override
    public StorageFileOperation downloadFile(String path, Path directory) throws FlashSafeStorageException {
        // TODO Auto-generated method stub
        throw new NotImplementedException();
    }

    @Override
    public StorageFileOperation uploadFile(String toPath, Path file) throws FlashSafeStorageException {
        try {
            FlashSafeStorageFileObject toPathResource = resourceResolver.resolveResource(toPath);
            if (toPathResource.getType() != FileObjectType.DIRECTORY) {
                throw new IllegalStateException("Destination path for uploading must be a directory. Can not upload file to: "
                        + toPath);
            }
            return storageService.uploadFile(toPathResource.getId(), file);
        } catch (ResourceResolverException e) {
            LOGGER.warn("Error while copying file" + file.toString() + " to storage", e);
            throw new FlashSafeStorageException("Error while copying file" + file.toString() + " to storage", e);
        }
    }

    @Override
    public void copy(String fromPath, String toPath) throws FlashSafeStorageException {
        try {
            FlashSafeStorageFileObject resourceToCopy = resourceResolver.resolveResource(fromPath);
            FlashSafeStorageFileObject targetDirectory = resourceResolver.resolveResource(toPath);
            storageService.copy(resourceToCopy.getId(), targetDirectory.getId());
        } catch (ResourceResolverException e) {
            LOGGER.warn("Error while copying directory " + fromPath + " to " + toPath, e);
            throw new FlashSafeStorageException("Error while copying directory", e);
        }
    }

    @Override
    public void move(String fromPath, String toPath) throws FlashSafeStorageException {
        try {
            FlashSafeStorageFileObject resourceToCopy = resourceResolver.resolveResource(fromPath);
            FlashSafeStorageFileObject targetDirectory = resourceResolver.resolveResource(toPath);
            storageService.move(resourceToCopy.getId(), targetDirectory.getId());
        } catch (ResourceResolverException e) {
            LOGGER.warn("Error while moving " + fromPath + " to " + toPath, e);
            throw new FlashSafeStorageException("Error while moving ", e);
        }
    }

    @Override
    public void delete(String path) throws FlashSafeStorageException {
        try {
            FlashSafeStorageFileObject resource = resourceResolver.resolveResource(path);
            storageService.delete(resource.getId());
        } catch (ResourceResolverException e) {
            LOGGER.warn("Error while deleting " + path, e);
            throw new FlashSafeStorageException("Error while deleting ", e);
        }
    }

}
