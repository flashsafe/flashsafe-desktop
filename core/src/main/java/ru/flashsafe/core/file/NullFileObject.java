package ru.flashsafe.core.file;

import java.io.IOException;

public class NullFileObject implements FileObject {

    public static final NullFileObject NULL_OBJECT = new NullFileObject();
    
    private NullFileObject() {
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public long getSize() throws IOException {
        return 0;
    }

    @Override
    public FileObjectType getType() {
        return null;
    }

    @Override
    public String getHash() {
        return null;
    }

    @Override
    public String getParentHash() {
        return null;
    }

    @Override
    public String getMimeType() {
        return null;
    }

    @Override
    public String getExt() {
        return null;
    }
    
    
    
}
