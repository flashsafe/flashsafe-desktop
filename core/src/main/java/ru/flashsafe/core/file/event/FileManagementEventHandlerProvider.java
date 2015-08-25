package ru.flashsafe.core.file.event;

public interface FileManagementEventHandlerProvider {

    FileObjectDuplicationHandler getFileObjectDuplicationHandler();
    
    FileObjectSecurityHandler getFileObjectSecurityHandler();
    
}
