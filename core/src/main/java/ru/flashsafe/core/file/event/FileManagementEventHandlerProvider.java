package ru.flashsafe.core.file.event;

/**
 * This object provides methods for getting event handlers related to file
 * manager.
 * 
 * @author Andrew
 *
 */
public interface FileManagementEventHandlerProvider {

    FileObjectDuplicationHandler getFileObjectDuplicationHandler();

    FileObjectSecurityHandler getFileObjectSecurityHandler();

}
