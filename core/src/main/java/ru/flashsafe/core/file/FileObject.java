package ru.flashsafe.core.file;

import java.io.IOException;

/**
 * A FileObject object. It can represent a file/directory and provide common
 * methods for these objects.
 * 
 * @author Andrew
 *
 */
public interface FileObject {
    
    /**
     * @return name of current object
     */
    String getName();

    /**
     * @return size in bytes
     * @throws IOException
     */
    long getSize() throws IOException;
    
    /**
     * @return the exact type of this fileObject 
     */
    FileObjectType getType();
    
    String getHash();
    
    String getParentHash();
    
    String getMimeType();
    
    String getExt();

}
