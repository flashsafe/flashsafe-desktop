package ru.flashsafe.core.file.util;

import ru.flashsafe.core.file.FileOperationType;
import ru.flashsafe.core.file.impl.AbstractFileOperation;
import ru.flashsafe.core.file.impl.FileOperationInfo;

/**
 * 
 * 
 * @author Andrew
 *
 */
public class SingleFileOperation extends AbstractFileOperation {
    
    private final long totalBytes;
    
    public SingleFileOperation(long id, FileOperationType operationType, FileOperationInfo operationInfo, long size) {
        super(id, operationType, operationInfo);
        totalBytes = size;
    }
    
    @Override
    public long getTotalBytes() {
        return totalBytes;
    }

}
