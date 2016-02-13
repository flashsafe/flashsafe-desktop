package ru.flashsafe.core.example;

import ru.flashsafe.core.FlashSafeApplication;
import ru.flashsafe.core.file.FileManager;
import ru.flashsafe.core.file.FileOperation;
import ru.flashsafe.core.file.exception.FileOperationException;
import ru.flashsafe.core.operation.OperationState;

/**
 * 
 * @author Andrew
 *
 */
public class CopyObjectToRemoteStorageAndStopProcess {

    private static final long UPDATE_PERIOD = 1000;
    
    public static void main(String[] args) throws FileOperationException, InterruptedException {
        FlashSafeApplication.run();

        FileManager fileManager = FlashSafeApplication.flashSafeSystem().fileManager();
        FileOperation copyOperation = fileManager.copy("D:\\magheli.pdf", FileManager.FLASH_SAFE_STORAGE_PATH_PREFIX
                + "");
        
        /* wait until finish */
        while(copyOperation.getState() != OperationState.FINISHED) {
            System.out.println("Progress :" + copyOperation.getProgress());
            System.out.println("Copy " + copyOperation.getFileObjectName() + " from " + copyOperation.getSourcePath() + " to "
                    + copyOperation.getDestinationPath());
            /* just for example - avoid using Thread.sleep */
            Thread.sleep(UPDATE_PERIOD);
            copyOperation.stop();
        }
        
        FlashSafeApplication.stop();
    }

}
