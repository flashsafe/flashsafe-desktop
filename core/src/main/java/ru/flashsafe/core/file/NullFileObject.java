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
    public String getAbsolutePath() {
        return null;
    }

    @Override
    public long getSize() throws IOException {
        return 0;
    }

    @Override
    public long getCreationTime() {
        return 0;
    }

    @Override
    public long getLastModifiedTime() {
        return 0;
    }

    @Override
    public FileObjectType getType() {
        return null;
    }
    
}
