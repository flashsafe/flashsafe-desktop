package ru.flashsafe.core.file;

/**
 * 
 * 
 * @author Andrew
 *
 */
public interface FileUtility {
    
    void open(File file);
    
    void properties(FileObject... fileObject);
    
    void menu(FileObject fileObject);

}
