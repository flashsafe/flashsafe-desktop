package ru.flashsafe.core.operation.monitor;

import java.io.IOException;
import java.io.InputStream;

import ru.flashsafe.core.file.util.SingleFileOperation;

/**
 * 
 * 
 * @author Andrew
 *
 */
//TODO implement mark/reset logic with operation's status progress
public class ProcessMonitorInputStream extends InputStream {
    
    private final InputStream inputStream;
    
    private final SingleFileOperation fileOperation;
    
    public ProcessMonitorInputStream(InputStream inputStream, SingleFileOperation fileOperation) {
        this.inputStream = inputStream;
        this.fileOperation = fileOperation;
    }
    
    @Override
    public int available() throws IOException {
        return inputStream.available();
    }
    
    @Override
    public void mark(int readlimit) {
        inputStream.mark(readlimit);
        //TODO apply changes to counter;
    }
    
    @Override
    public boolean markSupported() {
        return inputStream.markSupported();
    }
    
    @Override
    public int read() throws IOException {
        int byteData = inputStream.read();
        long processed = fileOperation.getProcessedBytes() + 1;
        fileOperation.setProcessedBytes(processed);
        return byteData;
    }
    
    @Override
    public int read(byte[] data) throws IOException {
        int byteCount = inputStream.read(data);
        if (byteCount > 0) {
            long processed = fileOperation.getProcessedBytes() + byteCount;
            fileOperation.setProcessedBytes(processed);
        }
        return byteCount;
    }
    
    @Override
    public int read(byte[] data, int offset, int length) throws IOException {
        int byteCount = inputStream.read(data, offset, length);
        if (byteCount > 0) {
            long processed = fileOperation.getProcessedBytes() + byteCount;
            fileOperation.setProcessedBytes(processed);
        }
        return byteCount;
    }
    
    @Override
    public void reset() throws IOException {
        inputStream.reset();
        //TODO apply reset to counter;
    }
    
    public long skip(long length) throws IOException {
        long byteCount = inputStream.skip(length);
        if (byteCount > 0) {
            long processed = fileOperation.getProcessedBytes() + byteCount;
            fileOperation.setProcessedBytes(processed);
        }
        return byteCount;
    }
    
    public void close() throws IOException {
        inputStream.close();
    }

}
