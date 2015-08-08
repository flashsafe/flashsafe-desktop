package ru.flashsafe.core.file;

import java.io.IOException;

/**
 * 
 * 
 * @author Andrew
 *
 */
public interface FileObject {

    String getName();

    String getAbsolutePath();
    
    long getSize() throws IOException;

}
