package ru.flashsafe.core;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.flashsafe.core.file.FileManager;
import ru.flashsafe.core.file.FileObject;
import ru.flashsafe.core.file.FileOperation;
import ru.flashsafe.core.file.exception.FileOperationException;
import ru.flashsafe.core.operation.OperationState;

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
        FlashSafeApplication.run();
        FileManager fileManager = FlashSafeApplication.flashSafeSystem().fileManager();
        //fileManager.createDirectory(FileManager.FLASH_SAFE_STORAGE_PATH_PREFIX + "test9");
        List<FileObject> fileObjs = fileManager.list(FileManager.FLASH_SAFE_STORAGE_PATH_PREFIX  + "test9/Test96");
        long after = System.currentTimeMillis();
        for (FileObject fsObject : fileObjs) {
            System.out.println("-" + fsObject.getName() + " size " + fsObject.getSize() + " -type");
        }
        //FileOperationStatus status =  fileManager.delete("D:\\папка_1");
        FileOperation operation = fileManager.copy("E:\\Video\\A Good Year 2006",  "E:\\");
        
        while(operation.getState() != OperationState.FINISHED) {
            LOGGER.info("Progress :" + operation.getProgress());
            LOGGER.info("Copy " + operation.getFileObjectName() + " from " + operation.getSourcePath() + " to "
                    + operation.getDestinationPath());
            Thread.sleep(500);
            operation.stop();
        }
        LOGGER.info("end");
        
        
        System.exit(0);
    }
}
