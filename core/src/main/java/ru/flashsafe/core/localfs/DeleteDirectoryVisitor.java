package ru.flashsafe.core.localfs;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import ru.flashsafe.core.file.FileOperationType;
import ru.flashsafe.core.file.impl.FileOperationStatusComposite;
import ru.flashsafe.core.file.impl.FileOperationStatusImpl;
import ru.flashsafe.core.operation.OperationState;

public class DeleteDirectoryVisitor extends SimpleFileVisitor<Path> {
    
    private final FileOperationStatusComposite operationStatus;
    
    public DeleteDirectoryVisitor(FileOperationStatusComposite operationStatus) {
        this.operationStatus = operationStatus;
    }
    
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        Files.delete(file);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        long fileSize = Files.size(file);
        FileOperationStatusImpl fileOperationStatus = new FileOperationStatusImpl(FileOperationType.MOVE, fileSize);
        operationStatus.setActiveOperationStatus(fileOperationStatus);
        Files.delete(file);
        fileOperationStatus.setProcessedBytes(fileSize);
        fileOperationStatus.setState(OperationState.FINISHED);
        operationStatus.submitActiveOperationStatusAsFinished();
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path directory, IOException exc) throws IOException {
        if (exc == null) {
            Files.delete(directory);
            return FileVisitResult.CONTINUE;
        } else {
            throw exc;
        }
    }

}
