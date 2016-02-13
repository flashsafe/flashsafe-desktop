package ru.flashsafe.core.old.storage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.flashsafe.core.FlashSafeRegistry;
import ru.flashsafe.core.event.ApplicationStopEvent;
import ru.flashsafe.core.event.FlashSafeEventService;
import ru.flashsafe.core.file.FileManager;
import ru.flashsafe.core.file.FileObjectType;
import ru.flashsafe.core.file.FileOperationType;
import ru.flashsafe.core.file.event.FileManagementEventHandlerProvider;
import ru.flashsafe.core.file.event.FileObjectSecurityEvent;
import ru.flashsafe.core.file.event.FileObjectSecurityEventResult;
import ru.flashsafe.core.file.event.FileObjectSecurityEventResult.ResultType;
import ru.flashsafe.core.file.event.FileObjectSecurityHandler;
import ru.flashsafe.core.file.impl.FileOperationInfo;
import ru.flashsafe.core.file.util.AsyncFileTreeWalker;
import ru.flashsafe.core.operation.OperationIDGenerator;
import ru.flashsafe.core.operation.OperationResult;
import ru.flashsafe.core.storage.CompositeFileStorageOperation;
import ru.flashsafe.core.storage.FlashSafeStorageService;
import ru.flashsafe.core.storage.StorageFileOperation;
import ru.flashsafe.core.storage.StorageOperationType;
import ru.flashsafe.core.storage.exception.FlashSafeStorageException;
import ru.flashsafe.core.storage.exception.ResourceResolverException;
import ru.flashsafe.core.storage.util.CopyDirectoryToStorageVisitor;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class FlashSafeStorageServiceImpl implements FlashSafeStorageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlashSafeStorageServiceImpl.class);

    private static final String PATH_SEPARATOR = "/";

    private final ResourceResolver resourceResolver;

    public final FlashSafeStorageIdBasedService storageService;

    private final FileManagementEventHandlerProvider handlerProvider;

    private final ExecutorService executorService = Executors.newFixedThreadPool(FlashSafeRegistry
            .readProperty(FlashSafeRegistry.LOCAL_TO_STORAGE_SIMULTANEOUSLY_EXECUTED_OPERATIONS));

    @Inject
    FlashSafeStorageServiceImpl(FlashSafeStorageIdBasedService storageService, ResourceResolver resourceResolver,
            FlashSafeEventService eventService, FileManagementEventHandlerProvider handlerProvider) {
        this.storageService = storageService;
        this.resourceResolver = resourceResolver;
        this.handlerProvider = handlerProvider;
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
            if (resource.isNeedPassword()) {
                FileObjectSecurityHandler handler = handlerProvider.getFileObjectSecurityHandler();
                FileObjectSecurityEventResult eventResult = handler.handle(new FileObjectSecurityEvent(resource));
                if (eventResult.getResult() == ResultType.CONTINUE) {
                    return addAbsolutePath(storageService.list(resource.getId(), eventResult.getCode()),
                            resource.getAbsolutePath());
                }
                // FIXME add specific exception
                throw new FlashSafeStorageException("Security code request was canceled");
            } else {
                return addAbsolutePath(storageService.list(resource.getId()), resource.getAbsolutePath());
            }
        } catch (ResourceResolverException e) {
            throw new FlashSafeStorageException("Error while listing directory", e);
        }
    }

    @Override
    public FlashSafeStorageDirectory createDirectory(String path) throws FlashSafeStorageException {
        String resourcePath = path.replaceFirst( FileManager.FLASH_SAFE_STORAGE_PATH_PREFIX, StringUtils.EMPTY);
        int lastPathSeparatorIndex = resourcePath.lastIndexOf(PATH_SEPARATOR);
        String parentDirectoryPath = FileManager.FLASH_SAFE_STORAGE_PATH_PREFIX;
        if (lastPathSeparatorIndex > 0) {
            parentDirectoryPath = resourcePath.substring(0, lastPathSeparatorIndex);
        }
        try {
            FlashSafeStorageFileObject parentDirectory = resourceResolver.resolveResource(parentDirectoryPath);
            String newDirectoryName = path.substring(path.lastIndexOf(PATH_SEPARATOR) + 1);
            FlashSafeStorageDirectory directory = storageService.createDirectory(parentDirectory.getId(), newDirectoryName);
            directory.setAbsolutePath(path);
            return directory;
        } catch (ResourceResolverException e) {
            LOGGER.warn("Error while creating directory " + path, e);
            throw new FlashSafeStorageException("Error while creating directory " + path, e);
        }
    }

    @Override
    public StorageFileOperation download(String remoteObjectPath, Path localDirectoryPath) throws FlashSafeStorageException {
        String localDirectoryPathString = localDirectoryPath.toString();
        try {
            FlashSafeStorageFileObject fromPathResource = resourceResolver.resolveResource(remoteObjectPath);
            if (fromPathResource.getType() == FileObjectType.DIRECTORY) {
                throw new IllegalStateException("Download process for directories is not implemented yet");
            }
//            FlashSafeStorageFileObject toPathResource = resourceResolver.resolveResource(remoteObjectPath);
//            FileOperationInfo operationInfo = new FileOperationInfo(localDirectoryPathString, remoteObjectPath, localDirectoryPath
//                    .getFileName().toString());
//            CompositeFileStorageOperation storageOperation = new CompositeFileStorageOperation(OperationIDGenerator.nextId(),
//                    FileOperationType.COPY, StorageOperationType.DOWNLOAD, operationInfo);
//
//            Future<OperationResult> operationFuture = executorService.submit(new AsyncFileTreeWalker(localDirectoryPath,
//                    new CopyDirectoryToStorageVisitor(localDirectoryPath, toPathResource, storageService, resourceResolver,
//                            storageOperation), storageOperation));
//            storageOperation.setOperationFuture(operationFuture);
            return storageService.downloadFile(fromPathResource.getId(), localDirectoryPath);
        } catch (ResourceResolverException e) {
            LOGGER.warn("Error while copying " + remoteObjectPath + " from storage", e);
            throw new FlashSafeStorageException("Error while copying " + remoteObjectPath + " from storage", e);
        }
    }

    @Override
    public StorageFileOperation upload(Path localObjectPath, String remoteDirectoryPath) throws FlashSafeStorageException {
        String localObjectPathString = localObjectPath.toString();
        try {
            FlashSafeStorageFileObject toPathResource = resourceResolver.resolveResource(remoteDirectoryPath);
            /* we should think about moving this operation inside async context */
            if (Files.isDirectory(localObjectPath)) {
                String pathName = localObjectPath.getFileName().toString();
                toPathResource = storageService.createDirectory(toPathResource.getId(), pathName);
                setAbsolutePath(toPathResource, remoteDirectoryPath);
            }
            FileOperationInfo operationInfo = new FileOperationInfo(localObjectPathString, remoteDirectoryPath, localObjectPath
                    .getFileName().toString());
            CompositeFileStorageOperation storageOperation = new CompositeFileStorageOperation(OperationIDGenerator.nextId(),
                    FileOperationType.COPY, StorageOperationType.UPLOAD, operationInfo);

            Future<OperationResult> operationFuture = executorService.submit(new AsyncFileTreeWalker(localObjectPath,
                    new CopyDirectoryToStorageVisitor(localObjectPath, toPathResource, storageService, resourceResolver,
                            storageOperation), storageOperation));
            storageOperation.setOperationFuture(operationFuture);
            return storageOperation;
        } catch (ResourceResolverException | FlashSafeStorageException e) {
            LOGGER.warn("Error while copying " + localObjectPathString + " to storage", e);
            throw new FlashSafeStorageException("Error while copying " + localObjectPathString + " to storage", e);
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

    private static List<FlashSafeStorageFileObject> addAbsolutePath(List<FlashSafeStorageFileObject> fileObjects,
            String absolutePath) {
        fileObjects.forEach(fileObject -> {
            setAbsolutePath(fileObject, absolutePath);
        });
        return fileObjects;
    }
    
    private static void setAbsolutePath(FlashSafeStorageFileObject fileObject, String absolutePath) {
        String path = absolutePath
                + (absolutePath.endsWith(PATH_SEPARATOR) ? fileObject.getName() : PATH_SEPARATOR + fileObject.getName());
        fileObject.setAbsolutePath(path);
    }

}
