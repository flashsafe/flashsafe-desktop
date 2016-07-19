package ru.flashsafe;

import java.io.File;

public interface FileController {

    void upload(File fileObject, String toPath);
    
    void download(String fromPath, File toFile);
    
    void loadContent(String path);
    
    void move(String fromPath, String toPath);
    
    void copy(String fromPath, String toPath);
    
    void rename(long fileObjectId, String name);
    
    void delete(String path);
    
    String getCurrentLocation();
    
}
