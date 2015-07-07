package ru.flashsafe.core.storage;

import java.util.List;
import java.util.NoSuchElementException;

import ru.flashsafe.core.old.storage.FlashSafeStorageDirectory;
import ru.flashsafe.core.old.storage.FlashSafeStorageFileObject;

public class ResourceResolver {

    private static final String PATH_DELIMITER = "/";
    
    private static final FlashSafeStorageDirectory ROOT_DIRECTORY;

    private final FlashSafeStorageService storageService;
    
    //TODO move out to runtime configuration
    static {
        ROOT_DIRECTORY = new FlashSafeStorageDirectory();
        ROOT_DIRECTORY.setId(0);
        ROOT_DIRECTORY.setName("/");
    }

    public ResourceResolver(FlashSafeStorageService storageService) {
        this.storageService = storageService;
    }

    public FlashSafeStorageFileObject resolveResource(String resourcePath) {
        String[] pathElements = resourcePath.split(PATH_DELIMITER);
        FlashSafeStorageFileObject currentPathObject = ROOT_DIRECTORY;
        for (String pathElement : pathElements) {
            currentPathObject = findResource(currentPathObject.getId(), pathElement);
            if (currentPathObject == null)
                throw new NoSuchElementException("Can't resolve " + resourcePath + ". " + pathElement + " is unknown");
        }
        return currentPathObject;
    }

    private FlashSafeStorageFileObject findResource(long parentId, String resourceName) {
        List<FlashSafeStorageFileObject> content = storageService.list(parentId);
        for (FlashSafeStorageFileObject resource : content) {
            if (resourceName.equals(resource.getName())) {
                return resource;
            }
        }
        return null;
    }

}
