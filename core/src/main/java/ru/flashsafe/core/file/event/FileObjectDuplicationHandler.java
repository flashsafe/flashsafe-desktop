package ru.flashsafe.core.file.event;

/**
 * Represents a handler for fileObject duplication event.
 * 
 * @author Andrew
 *
 */
public interface FileObjectDuplicationHandler {

    /**
     * Handles fileObject duplication event.
     * 
     * @param event fileObject duplication event
     * @return result of handle
     */
    FileObjectDuplicationEventResult handle(FileObjectDuplicationEvent event);
    
}
