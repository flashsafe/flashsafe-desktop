package ru.flashsafe.core.old.storage.util;

import java.io.File;
import java.nio.file.Path;
import java.util.regex.Pattern;

import ru.flashsafe.core.storage.FlashSafeStorageService;

public class TransferUtility {

    private TransferUtility() {
    }
    
    public static void copyToFlashSafeStorage(String fromPath, String toPath, FlashSafeStorageService storageService) {
        
    }

    public static void copyFromFlashSafeStorage(String fromPath, String toPath, FlashSafeStorageService storageService) {
        
    }
    
    public static String convertToFlashSafeStoragePath(Path localPath) {
        return localPath.toString().replaceAll(Pattern.quote(File.separator), "/"); 
    }
    
}
