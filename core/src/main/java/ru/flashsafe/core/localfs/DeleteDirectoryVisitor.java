package ru.flashsafe.core.localfs;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.flashsafe.core.file.impl.FileOperationStatusComposite;

/**
 * 
 * 
 * @author Andrey
 *
 */
//TODO add status processing
public class DeleteDirectoryVisitor extends SimpleFileVisitor<Path> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteDirectoryVisitor.class);
    
    private final FileOperationStatusComposite operationStatus;
    
    public DeleteDirectoryVisitor(FileOperationStatusComposite operationStatus) {
        this.operationStatus = operationStatus;
    }
    
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        LOGGER.trace("Deleting file {}", file);
        Files.delete(file);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        LOGGER.trace("Deleting file " + file + " after failed delete operation", exc);
        Files.delete(file);
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
