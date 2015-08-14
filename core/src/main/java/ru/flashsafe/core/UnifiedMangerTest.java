package ru.flashsafe.core;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.flashsafe.core.file.FileManager;
import ru.flashsafe.core.file.exception.FileOperationException;
import ru.flashsafe.core.file.impl.UnifiedFileManager;

public class UnifiedMangerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnifiedMangerTest.class);
    
    /**
     * @param args
     * @throws FileOperationException 
     * @throws InterruptedException 
     * @throws IOException 
     */
    public static void main(String[] args) throws FileOperationException, InterruptedException, IOException {
        LOGGER.info("Start");
        
        FileManager fileManager = new UnifiedFileManager();
//        fileManager.createDirectory(FlashSafeStorageService.FLASH_SAFE_STORAGE_PATH_PREFIX + "test9");
//        List<FileObject> fileObjs = fileManager.list(FileManager.FLASH_SAFE_STORAGE_PATH_PREFIX);
//        long after = System.currentTimeMillis();
//        for (FileObject fsObject : fileObjs) {
//            System.out.println("-" + fsObject.getName() + " size " + fsObject.getSize() + " -type");
//        }
//        
//        FileOperationStatus status = fileManager.copy("D:\\TR.pdf", FlashSafeStorageService.FLASH_SAFE_STORAGE_PATH_PREFIX);
//        
//        while(status.getState() != OperationState.FINISHED) {
//            System.out.println("Progress :" + status.getProgress());
//            Thread.sleep(2000);
//        }
//        System.out.println("end");
        
        System.exit(0);
    }
}
