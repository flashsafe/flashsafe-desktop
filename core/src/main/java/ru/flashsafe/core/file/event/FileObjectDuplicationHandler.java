package ru.flashsafe.core.file.event;

public interface FileObjectDuplicationHandler {

    FileObjectDuplicationHandleResult handle(FileObjectDuplicationEvent event);
    
}
