package ru.flashsafe.core.storage;

import java.util.List;

import ru.flashsafe.core.file.Directory;
import ru.flashsafe.core.file.File;
import ru.flashsafe.core.file.FileManager;
import ru.flashsafe.core.file.FileObject;

public class FlashSafeStorageFileManager implements FileManager {

    private FlashSafeStorageService storageService;
    
    public FlashSafeStorageFileManager(FlashSafeStorageService storageService) {
        this.storageService = storageService;
    }
    
    @Override
    public List<FileObject> list(String path) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public File createFile(String path) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Directory createDirectory(String path) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void copy(String fromPath, String toPath) {
        // TODO Auto-generated method stub

    }

    @Override
    public void move(String fromPath, String toPath) {
        // TODO Auto-generated method stub

    }

    @Override
    public void delete(String path) {
        // TODO Auto-generated method stub
    }

}
