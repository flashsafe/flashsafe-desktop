package ru.flashsafe.core.file;

import java.util.List;

import ru.flashsafe.core.file.exception.FileOperationException;

/**
 * An object that provides a set of methods (operations) to work with files and folders. 
 * This object is able to deal with local files located on current computer or FlashSafe storage. 
 * Paths to FlashSafe storage objects must begin with {@value #FLASH_SAFE_STORAGE_PATH_PREFIX}.
 * 
 * An implementation of this interface has to be thread-safe.
 * 
 * @author Andrew
 *
 */
public interface FileManager {

    /**
     * Prefix for any FlashSafe storage objects.
     * So the path to file on FlashSafe storage has to be defined as: {@code fls://path/to/resource}
     */
    String FLASH_SAFE_STORAGE_PATH_PREFIX = "fls://";

    /**
     * 
     * 
     * @param path
     * @return
     * @throws FileOperationException
     */
    List<FileObject> list(String path) throws FileOperationException;
    
    /**
     * 
     * 
     * @param path
     * @return
     * @throws FileOperationException
     */
    File createFile(String path) throws FileOperationException;
    
    /**
     * Creates directory in the specified path. The path 
     * 
     * @param path
     * @return
     * @throws FileOperationException
     */
    Directory createDirectory(String path) throws FileOperationException;
    
    /**
     * Copies file/directory {@link fromPath} into {@link toPath}.
     * The {@link toPath} directory must exist.
     * 
     * @param fromPath
     * @param toPath
     * @return
     * @throws FileOperationException
     */
    FileOperationStatus copy(String fromPath, String toPath) throws FileOperationException;
    
    /**
     * Moves file/directory {@link fromPath} into {@link toPath}.
     * The {@link toPath} directory must exist.
     * 
     * @param fromPath
     * @param toPath
     * @return
     * @throws FileOperationException
     */
    FileOperationStatus move(String fromPath, String toPath) throws FileOperationException;
    
    /**
     * Deletes file/directory in the specified path.
     * 
     * @param path to delete
     * @return 
     * @throws FileOperationException
     */
    FileOperationStatus delete(String path) throws FileOperationException;
    
}
