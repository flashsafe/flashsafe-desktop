package ru.flashsafe.core.storage;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;

import ru.flashsafe.core.file.FileOperationStatus;
import ru.flashsafe.core.file.FileOperationType;
import ru.flashsafe.core.file.exception.FileOperationException;
import ru.flashsafe.core.file.impl.FileOperationStatusComposite;
import ru.flashsafe.core.file.util.AsyncFileTreeWalker;
import ru.flashsafe.core.old.storage.FlashSafeStorageFileObject;
import ru.flashsafe.core.old.storage.util.CopyDirectoryVisitor;
import ru.flashsafe.core.storage.exception.ResourceResolverException;

public class FlashSafeStorageServiceHelper {
    
    private final FlashSafeStorageService storageService;
    
    private final ExecutorService executorService;
    
    private final ResourceResolver resolver;

    public FlashSafeStorageServiceHelper(FlashSafeStorageService storageService, ExecutorService executorService) {
        this.storageService = storageService;
        this.executorService = executorService;
        resolver = new ResourceResolver(storageService);
    }
    
    public FileOperationStatus copyToStorage(String fromPath, String toPath) throws FileOperationException {
        try {
            FlashSafeStorageFileObject toPathResource = resolver.resolveResource(toPath);
            Path startPath = Paths.get(fromPath);
            FileOperationStatusComposite operationStatus = new FileOperationStatusComposite(FileOperationType.COPY);
            executorService.execute(new AsyncFileTreeWalker(startPath, new CopyDirectoryVisitor(startPath, toPathResource,
                    storageService, operationStatus), operationStatus));
            return operationStatus;
        } catch (ResourceResolverException e) {
            // TODO add message
            throw new FileOperationException("", e);
        }
    }
    
}
