package ru.flashsafe.core.file;

import java.io.IOException;

/**
 * Represents a file.
 * 
 * @author Andrew
 *
 */
public interface File extends FileObject {
    
    String getFileFormat() throws IOException;

}
