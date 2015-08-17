package ru.flashsafe.core;

import ru.flashsafe.core.file.FileManager;
import ru.flashsafe.core.file.FileUtility;

/**
 * A facade to all public systems of FlashSafe.
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
