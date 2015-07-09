package ru.flashsafe.core.localfs;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicLong;

import ru.flashsafe.core.file.Directory;

//TODO cache the results of getSize() and count() operations
public class LocalDirectory implements Directory {

    private final Path directoryPath;

    protected LocalDirectory(Path directoryPath) {
        this.directoryPath = directoryPath;
    }

    @Override
    public String getName() {
        return directoryPath.getFileName().toString();
    }

    @Override
    public String getAbsolutePath() {
        return directoryPath.toString();
    }

    @Override
    public long getSize() throws IOException {
        final AtomicLong size = new AtomicLong();
        Files.walkFileTree(directoryPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                size.addAndGet(attrs.size());
                return FileVisitResult.CONTINUE;
            }
        });
        return size.longValue();
    }

}
