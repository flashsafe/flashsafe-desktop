package ru.flashsafe.core.localfs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import ru.flashsafe.core.file.Directory;

public class LocalDirectory implements Directory {

    private final Path directoryPath;
    
    protected LocalDirectory(Path directoryPath) {
        this.directoryPath = directoryPath;
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
    public long getSize() {
        try {
            return Files.size(directoryPath);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return -1;
    }

}
