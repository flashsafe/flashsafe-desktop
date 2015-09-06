package ru.flashsafe;

import java.io.File;

public interface FileController {

    void upload(File file);
    
    void loadContent(int resourceId);
    
}
