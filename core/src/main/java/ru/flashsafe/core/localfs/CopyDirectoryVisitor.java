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
import ru.flashsafe.core.file.impl.FileOperationStatusComposite;
import ru.flashsafe.core.file.impl.FileOperationStatusImpl;
import ru.flashsafe.core.operation.OperationState;
import ru.flashsafe.core.operation.monitor.ProcessMonitorInputStream;

//TODO work with operation's statuses
public class CopyDirectoryVisitor extends SimpleFileVisitor<Path> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CopyDirectoryVisitor.class);
    
    private final Path fromPath;
    
    private final Path toPath;
    
    private final FileOperationStatusComposite operationStatus;
    
    public CopyDirectoryVisitor(Path fromPath, Path toPath, FileOperationStatusComposite operationStatus) {
        this.fromPath = fromPath;
        this.toPath = toPath;
        this.operationStatus = operationStatus;
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
        FileOperationStatusImpl fileOperationStatus = new FileOperationStatusImpl(FileOperationType.COPY, Files.size(file));
        operationStatus.setActiveOperationStatus(fileOperationStatus);
        try (FileInputStream fs = new FileInputStream(file.toFile());
                InputStream monitoredInputStream = new ProcessMonitorInputStream(fs, fileOperationStatus)) {
            LOGGER.debug("Copying file {} to {}", file, toPath);
            Files.copy(monitoredInputStream, toPath.resolve(fromPath.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
        }
        fileOperationStatus.setState(OperationState.FINISHED);
        operationStatus.submitActiveOperationStatusAsFinished();
        LOGGER.trace("File {} was copied to {}", file, toPath);
        return FileVisitResult.CONTINUE;
    }
}
