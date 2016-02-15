package ru.flashsafe.core.localfs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import ru.flashsafe.core.file.File;
import ru.flashsafe.core.file.FileObjectType;

public class LocalFile implements File {

    private final FileObjectType objectType;
    
    private final Path filePath;

    protected LocalFile(Path filePath) {
        this.filePath = Objects.requireNonNull(filePath);
        objectType = FileObjectType.FILE;
    }

    @Override
    public String getName() {
        return filePath.getFileName().toString();
    }

    @Override
    public String getAbsolutePath() {
        return filePath.toString();
    }

    @Override
    public long getSize() throws IOException {
        return Files.size(filePath);
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
    public String getFileFormat() throws IOException {
        return Files.probeContentType(filePath);
    }

    @Override
    public FileObjectType getType() {
        return objectType;
    }

}
