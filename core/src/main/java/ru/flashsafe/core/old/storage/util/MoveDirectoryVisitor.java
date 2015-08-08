package ru.flashsafe.core.old.storage.util;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

public class MoveDirectoryVisitor extends SimpleFileVisitor<Path> {

    private Path fromPath;

    private Path toPath;

    private StandardCopyOption copyOption;

    public MoveDirectoryVisitor(Path fromPath, Path toPath, StandardCopyOption copyOption) {
        this.fromPath = fromPath;
        this.toPath = toPath;
        this.copyOption = copyOption;
    }

    public MoveDirectoryVisitor(Path fromPath, Path toPath) {
        this(fromPath, toPath, StandardCopyOption.REPLACE_EXISTING);
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
        Files.move(file, toPath.resolve(fromPath.relativize(file)), copyOption);
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
