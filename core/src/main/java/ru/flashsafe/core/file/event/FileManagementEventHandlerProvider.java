package ru.flashsafe.core.file.event;

/**
 * This object 
 * 
 * @author Andrew
 *
 */
public interface FileManagementEventHandlerProvider {

    FileObjectDuplicationHandler getFileObjectDuplicationHandler();
    
    FileObjectSecurityHandler getFileObjectSecurityHandler();
    
}
