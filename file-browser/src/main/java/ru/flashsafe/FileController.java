package ru.flashsafe;

import com.trolltech.qt.core.QFile;
import java.io.File;

public interface FileController {

    void upload(File fileObject, String toPath);
    
    void upload(QFile fileObject, String toPath);
    
    void download(String fromPath, File toFile);
    
    void download(String fromPath, QFile toFile);
    
    void loadContent(String path);
    
    void move(String fromPath, String toPath);
    
    void copy(String fromPath, String toPath);
    
    void rename(String fileObjectHash, String name);
    
    void delete(String path);
    
    String getCurrentLocation();
    
}
