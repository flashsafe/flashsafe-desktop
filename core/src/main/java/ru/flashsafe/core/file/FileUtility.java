package ru.flashsafe.core.file;

/**
 * Provides a set of utility methods for file objects like: open file, build
 * menu for file object etc.
 * 
 * @author Andrew
 *
 */
public interface FileUtility {

    /**
     * Opens provided file.
     * 
     * @param file
     *            file to open
     */
    void open(File file);

    void properties(FileObject... fileObject);

    void menu(FileObject fileObject);

}