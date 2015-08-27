package ru.flashsafe.core.storage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.flashsafe.core.FlashSafeRegistry;
import ru.flashsafe.core.event.ApplicationStopEvent;
import ru.flashsafe.core.event.FlashSafeEventService;
import ru.flashsafe.core.file.FileOperationType;
import ru.flashsafe.core.file.exception.FileOperationException;
import ru.flashsafe.core.file.impl.FileOperationInfo;
import ru.flashsafe.core.file.util.AsyncFileTreeWalker;
import ru.flashsafe.core.old.storage.FlashSafeStorageFileObject;
import ru.flashsafe.core.old.storage.FlashSafeStorageIdBasedService;
import ru.flashsafe.core.old.storage.ResourceResolver;
import ru.flashsafe.core.operation.OperationIDGenerator;
import ru.flashsafe.core.operation.OperationResult;
import ru.flashsafe.core.storage.exception.FlashSafeStorageException;
import ru.flashsafe.core.storage.exception.ResourceResolverException;
import ru.flashsafe.core.storage.util.CopyDirectoryToStorageVisitor;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

public class FlashSafeStorageServiceHelper {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FlashSafeStorageServiceHelper.class);
    
    private final FlashSafeStorageIdBasedService storageService;
    
    private final ResourceResolver resolver;

    private final ExecutorService executorService = Executors.newFixedThreadPool(FlashSafeRegistry
            .readProperty(FlashSafeRegistry.LOCAL_TO_STORAGE_SIMULTANEOUSLY_EXECUTED_OPERATIONS));
    
    @Inject
    FlashSafeStorageServiceHelper(FlashSafeStorageIdBasedService storageService, ResourceResolver resolver,
            FlashSafeEventService eventService) {
        this.storageService = storageService;
        this.resolver = resolver;
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
    
    public StorageFileOperation copyToStorage(String fromPath, String toPath) throws FileOperationException {
        try {
            FlashSafeStorageFileObject toPathResource = resolver.resolveResource(toPath);
            Path startPath = Paths.get(fromPath);
            /* we should think about moving this operation inside async context */
            if (Files.isDirectory(startPath)) {
                toPathResource = storageService.createDirectory(toPathResource.getId(), startPath.getFileName().toString());
            }
            FileOperationInfo operationInfo = new FileOperationInfo(fromPath, toPath, startPath.getFileName().toString());
            CompositeFileStorageOperation storageOperation = new CompositeFileStorageOperation(OperationIDGenerator.nextId(),
                    FileOperationType.COPY, StorageOperationType.UPLOAD, operationInfo);
            
            Future<OperationResult> operationFuture = executorService.submit(new AsyncFileTreeWalker(startPath, new CopyDirectoryToStorageVisitor(startPath,
                    toPathResource, storageService, storageOperation), storageOperation));
            storageOperation.setOperationFuture(operationFuture);
            return storageOperation; 
        } catch (ResourceResolverException | FlashSafeStorageException e) {
            LOGGER.warn("Error while copying file" + fromPath + " to storage", e);
            throw new FileOperationException("Error while copying file" + fromPath + " to storage", e);
        }
    }
    
}
