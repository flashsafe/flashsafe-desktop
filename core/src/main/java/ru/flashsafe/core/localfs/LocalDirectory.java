package ru.flashsafe.core.localfs;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

import ru.flashsafe.core.file.Directory;
import ru.flashsafe.core.file.FileObjectType;
import ru.flashsafe.core.file.util.FileUtils;

//TODO cache the results of getSize() and count() operations
public class LocalDirectory implements Directory {

    private final FileObjectType objectType;
    
    private final Path directoryPath;

    protected LocalDirectory(Path directoryPath) {
        this.directoryPath = Objects.requireNonNull(directoryPath);
        objectType = FileObjectType.DIRECTORY;
    }

    @Override
    public String getName() {
        return directoryPath.getFileName().toString();
    }

    @Override
    public String getAbsolutePath() {
        return directoryPath.toString();
    }

    @Override
    public long getSize() throws IOException {
        return FileUtils.countSizeForPath(directoryPath);
    }

    //TODO implement!
    @Override
    public int getCount() {
        return -1;
    }
    
    @Override
    public long getCreationTime() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getLastModifiedTime() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public FileObjectType getType() {
        return objectType;
    }

}
