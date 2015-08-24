package ru.flashsafe.core.file.event;

import ru.flashsafe.core.file.FileObject;

public class FileObjectDuplicationEvent {
    
    private final FileObject fileObject;
    
    public FileObjectDuplicationEvent(FileObject fileObject) {
        this.fileObject = fileObject;
    }

    public FileObject getFileObject() {
        return fileObject;
    }
    
}
