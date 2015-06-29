package ru.flashsafe.core.file;

import java.util.List;

/**
 * 
 * @author Andrew
 *
 */
public interface FileManager {

    List<FileObject> list(String path);
    
    File createFile(String path);
    
    Directory createDirectory(String path);
    
    void copy(String fromPath, String toPath);
    
    void move(String fromPath, String toPath);
    
    void delete(String path);
    
}
