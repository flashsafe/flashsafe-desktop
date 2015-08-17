package ru.flashsafe.core;

import ru.flashsafe.core.file.FileManager;
import ru.flashsafe.core.file.FileUtility;
import ru.flashsafe.core.storage.FlashSafeStorageService;

/**
 * 
 * 
 * @author Andrew
 *
 */
public interface FlashSafeSystem {
    
    /**
     * @return address of FlashSafe storage
     */
    String storageAddress();
    
    /**
     * @return FileManager instance
     */
    FileManager fileManager();
    
    /**
     * @return FileUtility instance
     */
    FileUtility fileUtility();

}
