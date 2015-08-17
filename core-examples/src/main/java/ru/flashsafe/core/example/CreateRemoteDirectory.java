package ru.flashsafe.core.example;

import ru.flashsafe.core.FlashSafeApplication;
import ru.flashsafe.core.file.Directory;
import ru.flashsafe.core.file.FileManager;
import ru.flashsafe.core.file.exception.FileOperationException;

/**
 * 
 * @author Andrew
 *
 */
public class CreateRemoteDirectory {

    public static void main(String[] args) throws FileOperationException {
        FlashSafeApplication.run();
        
        FileManager fileManager = FlashSafeApplication.flashSafeSystem().fileManager();
        Directory newDirectory = fileManager.createDirectory(FileManager.FLASH_SAFE_STORAGE_PATH_PREFIX
                + "{PUT_NEW_DIRECTORY_NAME_HERE}");
        System.out.println("New directory " + newDirectory.getName());
        
        FlashSafeApplication.stop();
    }

}
