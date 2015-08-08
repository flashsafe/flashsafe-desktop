package ru.flashsafe.core.localfs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import ru.flashsafe.core.file.File;

public class LocalFile implements File {

    private final Path filePath;

    protected LocalFile(Path filePath) {
        this.filePath = filePath;
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

}
