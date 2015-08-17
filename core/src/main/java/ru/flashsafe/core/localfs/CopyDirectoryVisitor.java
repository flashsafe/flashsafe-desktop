package ru.flashsafe.core.localfs;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
import ru.flashsafe.core.operation.OperationResult;
import ru.flashsafe.core.operation.OperationState;
import ru.flashsafe.core.operation.monitor.ProcessMonitorInputStream;

//TODO work with operation's statuses
public class CopyDirectoryVisitor extends SimpleFileVisitor<Path> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CopyDirectoryVisitor.class);
    
    private final Path fromPath;
    
    private final Path toPath;
    
    private final CompositeFileOperation operation;
    
    public CopyDirectoryVisitor(Path fromPath, Path toPath, CompositeFileOperation operation) {
        this.fromPath = fromPath;
        this.toPath = toPath;
        this.operation = operation;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        Path targetPath = toPath.resolve(fromPath.relativize(dir));
        if (!Files.exists(targetPath)) {
            LOGGER.debug("Creating directory {}", targetPath);
            Files.createDirectory(targetPath);
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (Thread.currentThread().isInterrupted()) {
            markOperationAsCanceled(operation);
            LOGGER.debug("Stopping copy operation");
            return FileVisitResult.TERMINATE;
        }
        FileOperationInfo operationInfo = new FileOperationInfo(fromPath.toString(), toPath.toString(), file.getFileName()
                .toString());
        SingleFileOperation currentFileOperation = new SingleFileOperation(OperationIDGenerator.nextId(), FileOperationType.COPY,
                operationInfo, Files.size(file));    
        operation.setCurrentOperation(currentFileOperation);
        try (FileInputStream fs = new FileInputStream(file.toFile());
                InputStream monitoredInputStream = new ProcessMonitorInputStream(fs, currentFileOperation)) {
            LOGGER.debug("Copying file {} to {}", file, toPath);
            Files.copy(monitoredInputStream, toPath.resolve(fromPath.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
        }
        currentFileOperation.setState(OperationState.FINISHED);
        operation.submitCurrentOperationAsFinished();
        LOGGER.trace("File {} was copied to {}", file, toPath);
        return FileVisitResult.CONTINUE;
    }
    
    private static void markOperationAsCanceled(CompositeFileOperation operation) {
        operation.setResult(OperationResult.CANCELED);
    }
    
}
