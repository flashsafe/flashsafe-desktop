package ru.flashsafe.core.storage.util;

import java.io.File;
import java.nio.file.Path;
import java.util.regex.Pattern;

public class StorageUtils {
    
    public static final String STORAGE_PATH_SEPARATOR = "/";
    
    public static final String STORAGE_PATH_PREFIX = "fls://";
    
    private StorageUtils() {
    }
    
    public static String convertToFlashSafeStoragePath(Path localPath) {
        String pathToConvert = localPath.toString();
        return pathToConvert.replaceAll(Pattern.quote(File.separator), STORAGE_PATH_SEPARATOR);
    }
    
}
