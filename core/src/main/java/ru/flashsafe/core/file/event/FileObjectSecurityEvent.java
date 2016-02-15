package ru.flashsafe.core.file.event;

import ru.flashsafe.core.file.FileObject;

/**
 * Represents fileObjet security event.
 * 
 * @author Andrew
 *
 */
public class FileObjectSecurityEvent {

    private final FileObject fileObject;
    
    public FileObjectSecurityEvent(FileObject fileObject) {
        this.fileObject = fileObject;
    }

    /**
     * @return fileObject 
     */
    public FileObject getFileObject() {
        return fileObject;
    }
    
}
