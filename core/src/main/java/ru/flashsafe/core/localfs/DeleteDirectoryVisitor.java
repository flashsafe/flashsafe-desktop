package ru.flashsafe.core.localfs;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.flashsafe.core.file.FileOperationType;
import ru.flashsafe.core.file.impl.FileOperationInfo;
import ru.flashsafe.core.file.util.CompositeFileOperation;
import ru.flashsafe.core.file.util.SingleFileOperation;
import ru.flashsafe.core.operation.OperationIDGenerator;
import ru.flashsafe.core.operation.OperationState;

/**
 * 
 * 
 * @author Andrey
 *
 */
//TODO add status processing
public class DeleteDirectoryVisitor extends SimpleFileVisitor<Path> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteDirectoryVisitor.class);
    
    private final CompositeFileOperation operation;
    
    public DeleteDirectoryVisitor(CompositeFileOperation operation) {
        this.operation = operation;
    }
    
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        long fileSize = Files.size(file);
        FileOperationInfo operationInfo = new FileOperationInfo(file.toString(), null, file.getFileName()
                .toString());
        SingleFileOperation currentFileOperation = new SingleFileOperation(OperationIDGenerator.nextId(), FileOperationType.DELETE,
                operationInfo, fileSize);
        currentFileOperation.setState(OperationState.IN_PROGRESS);
        operation.setCurrentOperation(currentFileOperation);
        LOGGER.trace("Deleting file {}", file);
        Files.delete(file);
        currentFileOperation.setProcessedBytes(fileSize);
        currentFileOperation.setState(OperationState.FINISHED);
        operation.submitCurrentOperationAsFinished();
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path directory, IOException exc) throws IOException {
        if (exc == null) {
            LOGGER.trace("Deleting directory {}", directory);
            Files.delete(directory);
            return FileVisitResult.CONTINUE;
        } else {
            throw exc;
        }
    }

}
