package ru.flashsafe.core.storage;

import java.util.List;
import static java.util.Objects.*;

import ru.flashsafe.core.old.storage.FlashSafeStorageDirectory;
import ru.flashsafe.core.old.storage.FlashSafeStorageFileObject;
import ru.flashsafe.core.storage.exception.FlashSafeStorageException;
import ru.flashsafe.core.storage.exception.ResourceResolverException;

public class ResourceResolver {

    private static final String PATH_DELIMITER = "/";
    
    private static final int PATH_OFFSET = FlashSafeStorageService.FLASH_SAFE_STORAGE_PATH_PREFIX.length();
    
    private static final FlashSafeStorageDirectory ROOT_DIRECTORY;

    private final FlashSafeStorageService storageService;
    
    //TODO move out to runtime configuration
    static {
        ROOT_DIRECTORY = new FlashSafeStorageDirectory();
        ROOT_DIRECTORY.setId(0);
        ROOT_DIRECTORY.setName("/");
    }

    public ResourceResolver(FlashSafeStorageService storageService) {
        this.storageService = requireNonNull(storageService);
    }

    public FlashSafeStorageFileObject resolveResource(FlashSafeStorageFileObject parent, String resourcePath)
            throws ResourceResolverException {
        return resolveResource(parent, resourcePath, true);
    }

    public FlashSafeStorageFileObject resolveResource(String resourcePath) throws ResourceResolverException {
        return resolveResource(ROOT_DIRECTORY, resourcePath, true);
    }

    public FlashSafeStorageFileObject resolveResourceIfExists(FlashSafeStorageFileObject parent, String resourcePath)
            throws ResourceResolverException {
        return resolveResource(parent, resourcePath, false);
    }

    public FlashSafeStorageFileObject resolveResourceIfExists(String resourcePath) throws ResourceResolverException {
        return resolveResource(ROOT_DIRECTORY, resourcePath, false);
    }

    private FlashSafeStorageFileObject resolveResource(FlashSafeStorageFileObject parent, String resourcePath,
            boolean exceptionIfNotExists) throws ResourceResolverException {
        if (resourcePath.startsWith(FlashSafeStorageService.FLASH_SAFE_STORAGE_PATH_PREFIX)) {
            resourcePath = resourcePath.substring(PATH_OFFSET);
        }
        if (resourcePath.length() == 0) {
            return parent;
        }
        FlashSafeStorageFileObject currentPathObject = parent;
        String[] pathElements = resourcePath.split(PATH_DELIMITER);
        for (String pathElement : pathElements) {
            currentPathObject = findResource(currentPathObject.getId(), pathElement);
            if (currentPathObject == null) {
                if (exceptionIfNotExists)
                    throw new ResourceResolverException("Can't resolve " + resourcePath + ". " + pathElement + " is unknown");
                return null;
            }
        }
        return currentPathObject;
    }

    private FlashSafeStorageFileObject findResource(long parentId, String resourceName) throws ResourceResolverException {
        List<FlashSafeStorageFileObject> content;
        try {
            content = storageService.list(parentId);
        } catch (FlashSafeStorageException e) {
            //TODO add message
            throw new ResourceResolverException("", e);
        }
        for (FlashSafeStorageFileObject resource : content) {
            if (resourceName.equals(resource.getName())) {
                return resource;
            }
        }
        return null;
    }

}
