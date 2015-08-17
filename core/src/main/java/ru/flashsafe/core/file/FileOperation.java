package ru.flashsafe.core.file;

import ru.flashsafe.core.operation.Operation;

/**
 * Represents an operation with file(s).
 * 
 * @author Andrew
 *
 */
public interface FileOperation extends Operation {
    
    /**
     * @return type of operation
     */
    FileOperationType getOperationType();
    
    /**
     * @return source path
     */
    String getSourcePath();
    
    /**
     * @return destination path
     */
    String getDestinationPath();
    
    /**
     * @return name of processing fileObject
     */
    String getFileObjectName();

    /**
     * @return number of bytes that have to be processed
     */
    long getTotalBytes();
    
    /**
     * @return number of bytes that were already processed
     */
    long getProcessedBytes();
    
}
