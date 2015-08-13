package ru.flashsafe.core;

import java.io.IOException;
import java.util.List;

import ru.flashsafe.core.file.FileManager;
import ru.flashsafe.core.file.FileObject;
import ru.flashsafe.core.file.FileOperationStatus;
import ru.flashsafe.core.file.exception.FileOperationException;
import ru.flashsafe.core.file.impl.UnifiedFileManager;
import ru.flashsafe.core.operation.OperationState;
import ru.flashsafe.core.storage.FlashSafeStorageService;

public class UnifiedMangerTest {

    /**
     * @param args
     * @throws FileOperationException 
     * @throws InterruptedException 
     * @throws IOException 
     */
    public static void main(String[] args) throws FileOperationException, InterruptedException, IOException {
        System.out.println("start");
        FileManager fileManager = new UnifiedFileManager();
//        fileManager.createDirectory(FlashSafeStorageService.FLASH_SAFE_STORAGE_PATH_PREFIX + "test9");
//        List<FileObject> fileObjs = fileManager.list(FlashSafeStorageService.FLASH_SAFE_STORAGE_PATH_PREFIX);
//        long after = System.currentTimeMillis();
//        for (FileObject fsObject : fileObjs) {
//            System.out.println("-" + fsObject.getName() + " size " + fsObject.getSize());
//        }
//        
        FileOperationStatus status = fileManager.copy("E:\\arch_for_replace\\docsLETO\\res1.ru.rar", FlashSafeStorageService.FLASH_SAFE_STORAGE_PATH_PREFIX);
        
        while(status.getState() != OperationState.FINISHED) {
            System.out.println("Progress :" + status.getProgress());
            Thread.sleep(2000);
        }
        System.out.println("end");
        
        System.exit(0);
    }
}
