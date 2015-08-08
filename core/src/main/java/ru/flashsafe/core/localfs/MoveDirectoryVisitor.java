package ru.flashsafe.core.localfs;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

import ru.flashsafe.core.file.FileOperationType;
import ru.flashsafe.core.file.impl.FileOperationStatusComposite;
import ru.flashsafe.core.file.impl.FileOperationStatusImpl;
import ru.flashsafe.core.operation.OperationState;

public class MoveDirectoryVisitor extends SimpleFileVisitor<Path> {

    private final Path fromPath;

    private final Path toPath;

    private final FileOperationStatusComposite operationStatus;

    public MoveDirectoryVisitor(Path fromPath, Path toPath, FileOperationStatusComposite operationStatus) {
        this.fromPath = fromPath;
        this.toPath = toPath;
        this.operationStatus = operationStatus;
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
        FileOperationStatusImpl fileOperationStatus = new FileOperationStatusImpl(FileOperationType.MOVE, fileSize);
        operationStatus.setActiveOperationStatus(fileOperationStatus);
        Files.move(file, toPath.resolve(fromPath.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
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
