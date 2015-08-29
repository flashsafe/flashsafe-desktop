package ru.flashsafe.core.file.util;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Set of utilities to work with fileObjects.
 * 
 * @author Andrew
 *
 */
public class FileUtils {

    private FileUtils() {
    }

    /**
     * Counts size of {@code path}. This method works for files and directories.
     * 
     * @param path path of an object
     * @return size in bytes
     * @throws IOException
     */
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
