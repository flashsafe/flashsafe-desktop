package ru.flashsafe.core.file.util;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 
 * 
 * @author Andrew
 *
 */
public class FileUtils {

    private FileUtils() {
    }

    public static long countSizeForPath(Path path) throws IOException {
        if (Files.isRegularFile(path)) {
            return Files.size(path);
        }
        final AtomicLong totalBytes = new AtomicLong();
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                totalBytes.addAndGet(attrs.size());
                return FileVisitResult.CONTINUE;
            }
        });
        return totalBytes.longValue();
    }

}
