package ru.flashsafe.core.file.impl;

/**
 * Contains information of file operation: 
 * - source path;
 * - destination path (may be {@code null} for operation with one argument - delete for instance);
 * - name of file object to process.
 * 
 * @author Andrew
 *
 */
public class FileOperationInfo {

    private final String source;

    private final String destination;

    private final String fileObjectName;

    /**
     * @param source
     *            source path
     * @param destination
     *            destination path
     * @param objectName
     *            name of processing fileObject
     */
    public FileOperationInfo(String source, String destination, String fileObjectName) {
        this.source = source;
        this.destination = destination;
        this.fileObjectName = fileObjectName;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public String getFileObjectName() {
        return fileObjectName;
    }

}
