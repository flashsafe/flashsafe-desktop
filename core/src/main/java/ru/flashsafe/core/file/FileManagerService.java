package ru.flashsafe.core.file;

import ru.flashsafe.core.file.event.FileObjectDuplicationHandler;
import ru.flashsafe.core.file.event.FileObjectSecurityHandler;

public interface FileManagerService {

    FileManager getFileManager();
    
    void registerFileObjectDuplicationHandler(FileObjectDuplicationHandler handler);
    
    void registerFileObjectSecurityHandler(FileObjectSecurityHandler handler);
    
}
