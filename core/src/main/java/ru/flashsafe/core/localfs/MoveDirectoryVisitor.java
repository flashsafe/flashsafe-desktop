package ru.flashsafe.core.localfs;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.flashsafe.core.file.FileOperationType;
import ru.flashsafe.core.file.impl.FileOperationInfo;
import ru.flashsafe.core.file.util.CompositeFileOperation;
import ru.flashsafe.core.file.util.SingleFileOperation;
import ru.flashsafe.core.operation.OperationIDGenerator;
import ru.flashsafe.core.operation.OperationState;

public class MoveDirectoryVisitor extends SimpleFileVisitor<Path> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MoveDirectoryVisitor.class);
    
    private final Path fromPath;

    private final Path toPath;

    private final CompositeFileOperation operation;

    public MoveDirectoryVisitor(Path fromPath, Path toPath, CompositeFileOperation operation) {
        this.fromPath = fromPath;
        this.toPath = toPath;
        this.operation = operation;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path directory, BasicFileAttributes attrs) throws IOException {
        Path targetPath = toPath.resolve(fromPath.relativize(directory));
        if (!Files.exists(targetPath)) {
            Files.createDirectory(targetPath);
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        long fileSize = Files.size(file);
        
        FileOperationInfo operationInfo = new FileOperationInfo(fromPath.toString(), toPath.toString(), file.getFileName()
                .toString());
        SingleFileOperation currentFileOperation = new SingleFileOperation(OperationIDGenerator.nextId(), FileOperationType.MOVE,
                operationInfo, fileSize);    
        currentFileOperation.setState(OperationState.IN_PROGRESS);
        operation.setCurrentOperation(currentFileOperation);
        LOGGER.debug("Moving file {} to {}", file, toPath);
        Files.move(file, toPath.resolve(fromPath.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
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
