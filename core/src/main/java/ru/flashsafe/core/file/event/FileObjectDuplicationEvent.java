package ru.flashsafe.core.file.event;

import ru.flashsafe.core.file.FileObject;

/**
 * Represents fileObject duplication event.
 * 
 * @author Andrew
 *
 */
public class FileObjectDuplicationEvent {
    
    private final FileObject fileObject;
    
    public FileObjectDuplicationEvent(FileObject fileObject) {
        this.fileObject = fileObject;
    }

    /**
     * @return duplicated fileObject
     */
    public FileObject getFileObject() {
        return fileObject;
    }
    
}
