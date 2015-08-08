package ru.flashsafe.core.storage;

import java.util.concurrent.ConcurrentHashMap;

public class StorageOperationsRegistry {

    private static final ConcurrentHashMap<Long, StorageOperationStatusImpl> uploadProgressMap = new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<Long, StorageOperationStatusImpl> downloadProgressMap = new ConcurrentHashMap<>();

    private StorageOperationsRegistry() {
    }

    public static void registerOperation(long operationId, StorageOperationType operationType, StorageOperationStatusImpl status) {
        if (operationType == StorageOperationType.UPLOAD) {
            registerUploadOperation(operationId, status);
        } else if (operationType == StorageOperationType.DOWNLOAD) {
            registerDownloadOperation(operationId, status);
        }
    }
    
    public StorageOperationStatusImpl getOperation(long operationId, StorageOperationType operationType) {
        if (operationType == StorageOperationType.UPLOAD) {
            return uploadProgressMap.get(operationId);
        } else if (operationType == StorageOperationType.DOWNLOAD) {
            return downloadProgressMap.get(operationId);
        }
        //TODO fix - this is impossible outcome but NULL is a bad idea
        return null;
    }

    private static void registerUploadOperation(long operationId, StorageOperationStatusImpl status) {
        uploadProgressMap.putIfAbsent(operationId, status);
    }

    private static void registerDownloadOperation(long operationId, StorageOperationStatusImpl status) {
        downloadProgressMap.putIfAbsent(operationId, status);
    }

}
