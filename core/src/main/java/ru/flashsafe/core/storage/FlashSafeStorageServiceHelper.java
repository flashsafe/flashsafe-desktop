package ru.flashsafe.core.storage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.flashsafe.core.file.FileOperationType;
import ru.flashsafe.core.file.exception.FileOperationException;
import ru.flashsafe.core.file.impl.FileOperationInfo;
import ru.flashsafe.core.file.util.AsyncFileTreeWalker;
import ru.flashsafe.core.old.storage.FlashSafeStorageFileObject;
import ru.flashsafe.core.operation.OperationIDGenerator;
import ru.flashsafe.core.operation.OperationRegistry;
import ru.flashsafe.core.operation.OperationResult;
import ru.flashsafe.core.storage.exception.FlashSafeStorageException;
import ru.flashsafe.core.storage.exception.ResourceResolverException;
import ru.flashsafe.core.storage.util.CopyDirectoryToStorageVisitor;

import com.google.inject.Inject;

public class FlashSafeStorageServiceHelper {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FlashSafeStorageServiceHelper.class);
    
    private final FlashSafeStorageService storageService;
    
    private final ResourceResolver resolver;

    // TODO return usage of configuration registry (or properties)
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Inject
    FlashSafeStorageServiceHelper(FlashSafeStorageService storageService, ResourceResolver resolver) {
        this.storageService = storageService;
        this.resolver = resolver;
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
            // TODO add message
            LOGGER.warn("", e);
            throw new FileOperationException("", e);
        }
    }
    
}
