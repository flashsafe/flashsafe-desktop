package ru.flashsafe.core.storage;

import java.util.List;

import ru.flashsafe.core.file.Directory;
import ru.flashsafe.core.file.File;
import ru.flashsafe.core.file.FileManager;
import ru.flashsafe.core.file.FileObject;
import ru.flashsafe.core.old.storage.FlashSafeStorageFileObject;

public class FlashSafeStorageFileManager implements FileManager {

    private FlashSafeStorageService storageService;

    private ResourceResolver resolver;

    public FlashSafeStorageFileManager(FlashSafeStorageService storageService) {
        this.storageService = storageService;
        resolver = new ResourceResolver(storageService);
    }

    @Override
    public List<FileObject> list(String path) {
        FlashSafeStorageFileObject resource = resolver.resolveResource(path);
        
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
    }

    @Override
    public void delete(String path) {
        FlashSafeStorageFileObject resource = resolver.resolveResource(path);
        storageService.delete(resource.getId());
    }
}
