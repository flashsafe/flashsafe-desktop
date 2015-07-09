package ru.flashsafe.core.file;

import ru.flashsafe.core.operation.OperationStatus;

public interface FileOperationStatus extends OperationStatus {

    FileOperationType getOperationType();
    
    long getProcessedBytes();
    
}
