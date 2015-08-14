package ru.flashsafe.core.file;

/**
 * Represents a directory.
 * 
 * @author Andrew
 *
 */
public interface Directory extends FileObject {

    /**
     * @return number of objects inside this directory 
     */
    public int getCount();
    
}
