package ru.flashsafe;

import java.io.File;

public interface FileController {

    void upload(File fileObject, String toPath);
    
    void download(String fromPath, File toFile);
    
    void loadContent(String path);
    
    String getCurrentLocation();
    
}
