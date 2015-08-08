package ru.flashsafe.core.file;

import java.util.List;

import ru.flashsafe.core.file.exception.FileOperationException;

/**
 * 
 * @author Andrew
 *
 */
public interface FileManager {

    List<FileObject> list(String path) throws FileOperationException;
    
    File createFile(String path) throws FileOperationException;
    
    Directory createDirectory(String path) throws FileOperationException;
    
    FileOperationStatus copy(String fromPath, String toPath) throws FileOperationException;
    
    FileOperationStatus move(String fromPath, String toPath) throws FileOperationException;
    
    FileOperationStatus delete(String path) throws FileOperationException;
    
}
