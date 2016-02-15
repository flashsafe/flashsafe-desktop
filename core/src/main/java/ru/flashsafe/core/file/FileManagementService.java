package ru.flashsafe.core.file;

import ru.flashsafe.core.file.event.FileObjectDuplicationHandler;
import ru.flashsafe.core.file.event.FileObjectSecurityHandler;

/**
 * 
 * 
 * @author Andrew
 *
 */
public interface FileManagementService {

    /**
     * @return 
     */
    FileManager getFileManager();
    
    /**
     * @param handler
     */
    void registerFileObjectDuplicationHandler(FileObjectDuplicationHandler handler);
    
    /**
     * @param handler
     */
    void registerFileObjectSecurityHandler(FileObjectSecurityHandler handler);
    
}
