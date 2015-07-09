package ru.flashsafe.core.file.util;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtils {

    private FileUtils() {
    }

    public static long countSizeForPath(Path path) throws IOException {
        if (Files.isRegularFile(path)) {
            return Files.size(path);
        }
        long totalBytes = 0;
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
            for (Path currentPath : directoryStream) {
                if (Files.isRegularFile(currentPath)) {
                    totalBytes += Files.size(currentPath);
                }
            }
            return totalBytes;
        }
    }

}
