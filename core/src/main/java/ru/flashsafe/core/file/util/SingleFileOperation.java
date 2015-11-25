package ru.flashsafe.core.file.util;

import ru.flashsafe.core.file.FileOperationType;
import ru.flashsafe.core.file.impl.AbstractFileOperation;
import ru.flashsafe.core.file.impl.FileOperationInfo;

/**
 * A file operation object used to work with single files.
 * 
 * @author Andrew
 *
 */
public class SingleFileOperation extends AbstractFileOperation {
    
    private long totalBytes;
    
    public SingleFileOperation(long id, FileOperationType operationType, FileOperationInfo operationInfo, long size) {
        super(id, operationType, operationInfo);
        totalBytes = size;
    }
    
    public void setTotalBytes(long totalBytes) {
        this.totalBytes = totalBytes;
    }

    @Override
    public long getTotalBytes() {
        return totalBytes;
    }

}
