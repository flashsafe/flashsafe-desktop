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
     * @return absolute path to the object, includes its name
     */
    String getAbsolutePath();

    /**
     * @return size in bytes
     * @throws IOException
     */
    long getSize() throws IOException;

}
