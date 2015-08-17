package ru.flashsafe.core.example;

import java.util.List;

import ru.flashsafe.core.FlashSafeApplication;
import ru.flashsafe.core.file.FileManager;
import ru.flashsafe.core.file.FileObject;
import ru.flashsafe.core.file.exception.FileOperationException;

/**
 * List directory on FlashSafe remote storage.
 * 
 * @author Andrew
 *
 */
public class ListRemoteDirectory {

    public static void main(String[] args) throws FileOperationException {
        FlashSafeApplication.run();
        
        FileManager fileManager = FlashSafeApplication.flashSafeSystem().fileManager();
        List<FileObject> test96List = fileManager.list(FileManager.FLASH_SAFE_STORAGE_PATH_PREFIX  + "test9/Test96");
        test96List.forEach(fileObject -> System.out.println(" - name: " + fileObject.getName()));
        
        FlashSafeApplication.stop();
    }

}
