package ru.flashsafe.core.file.event;

/**
 * Represents a handler for fileObject security event.
 * 
 * @author Andrew
 *
 */
public interface FileObjectSecurityHandler {

    FileObjectSecurityEventResult handle(FileObjectSecurityEvent event);
    
}
