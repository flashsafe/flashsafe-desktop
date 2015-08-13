package ru.flashsafe.core.storage;

import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * 
 * @author Andrew
 *
 */
public class ProcessMonitorInputStream extends InputStream {

    private final InputStream inputStream;

    private final StorageOperationStatusImpl fileOperationStatus;

    public ProcessMonitorInputStream(InputStream inputStream, StorageOperationStatusImpl fileOperationStatus) {
        this.inputStream = inputStream;
        this.fileOperationStatus = fileOperationStatus;
    }

    @Override
    public int available() throws IOException {
        return inputStream.available();
    }

    @Override
    public void mark(int readlimit) {
        inputStream.mark(readlimit);
        // TODO apply changes to counter;
    }

    @Override
    public boolean markSupported() {
        return inputStream.markSupported();
    }

    @Override
    public int read() throws IOException {
        int byteData = inputStream.read();
        fileOperationStatus.incrementProcessedBytes(1);
        return byteData;
    }

    @Override
    public int read(byte[] data) throws IOException {
        int byteCount = inputStream.read(data);
        if (byteCount > 0) {
            fileOperationStatus.incrementProcessedBytes(byteCount);
        }
        return byteCount;
    }

    @Override
    public int read(byte[] data, int offset, int length) throws IOException {
        int byteCount = inputStream.read(data, offset, length);
        if (byteCount > 0) {
            fileOperationStatus.incrementProcessedBytes(byteCount);
        }
        return byteCount;
    }

    @Override
    public void reset() throws IOException {
        inputStream.reset();
        // TODO apply reset to counter;
    }

    public long skip(long length) throws IOException {
        long byteCount = inputStream.skip(length);
        if (byteCount > 0) {
            fileOperationStatus.incrementProcessedBytes(byteCount);
        }
        return byteCount;
    }

    public void close() throws IOException {
        inputStream.close();
    }

}

