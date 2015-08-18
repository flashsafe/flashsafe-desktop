package ru.flashsafe.core.storage;

import static java.util.Objects.requireNonNull;
import static ru.flashsafe.core.storage.util.StorageUtils.STORAGE_PATH_PREFIX;
import static ru.flashsafe.core.storage.util.StorageUtils.STORAGE_PATH_SEPARATOR;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.flashsafe.core.old.storage.FlashSafeStorageDirectory;
import ru.flashsafe.core.old.storage.FlashSafeStorageFileObject;
import ru.flashsafe.core.storage.exception.FlashSafeStorageException;
import ru.flashsafe.core.storage.exception.ResourceResolverException;
import ru.flashsafe.core.storage.util.StorageUtils;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sun.xml.internal.txw2.IllegalSignatureException;

/**
 * This object uses to map objects' names (paths) to storage Ids.
 * 
 * @author Andrew
 *
 */
@Singleton
public class ResourceResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceResolver.class);
    
    private static final int PATH_OFFSET = StorageUtils.STORAGE_PATH_PREFIX.length();
    
    private static final FlashSafeStorageDirectory ROOT_DIRECTORY;

    private final FlashSafeStorageService storageService;
    
    //TODO move out to runtime configuration
    static {
        ROOT_DIRECTORY = new FlashSafeStorageDirectory();
        ROOT_DIRECTORY.setId(0);
        ROOT_DIRECTORY.setName("/");
    }

    @Inject
    public ResourceResolver(FlashSafeStorageService storageService) {
        this.storageService = requireNonNull(storageService);
    }

    /**
     * Resolves resource with path {@code resourcePath} and {@code parent} object.
     * 
     * @param parent parent object (directory) of resource
     * @param resourcePath path of resource
     * @return storage fileObject
     * @throws ResourceResolverException if resource does not exist 
     */
    public FlashSafeStorageFileObject resolveResource(FlashSafeStorageFileObject parent, String resourcePath)
            throws ResourceResolverException {
        return resolveResource(parent, resourcePath, true);
    }

    /**
     * Resolves resource with path {@code resourcePath}.
     * 
     * @param resourcePath path of resource
     * @return storage fileObject
     * @throws ResourceResolverException if resource does not exist 
     */
    public FlashSafeStorageFileObject resolveResource(String resourcePath) throws ResourceResolverException {
        return resolveResource(ROOT_DIRECTORY, resourcePath, true);
    }

    /**
     * Resolves resource with path {@code resourcePath} and {@code parent} object.
     * 
     * @param parent parent object (directory) of resource
     * @param resourcePath path of resource
     * @return storage fileObject if resource exists, {@code null} otherwise
     */
    public FlashSafeStorageFileObject resolveResourceIfExists(FlashSafeStorageFileObject parent, String resourcePath) {
        try {
            return resolveResource(parent, resourcePath, false);
        } catch (ResourceResolverException e) {
            throw new IllegalSignatureException("Unexpected behaviour", e);
        }
    }

    /**
     * Resolves resource with path {@code resourcePath}.
     * 
     * @param resourcePath path of resource
     * @return storage fileObject if resource exists, {@code null} otherwise
     */
    public FlashSafeStorageFileObject resolveResourceIfExists(String resourcePath) {
        try {
            return resolveResource(ROOT_DIRECTORY, resourcePath, false);
        } catch (ResourceResolverException e) {
            throw new IllegalSignatureException("Unexpected behaviour", e);
        }
    }

    private FlashSafeStorageFileObject resolveResource(FlashSafeStorageFileObject parent, String resourcePath,
            final boolean exceptionIfNotExists) throws ResourceResolverException {
        if (resourcePath.startsWith(STORAGE_PATH_PREFIX)) {
            resourcePath = resourcePath.substring(PATH_OFFSET);
        }
        if (resourcePath.length() == 0) {
            return parent;
        }
        FlashSafeStorageFileObject currentPathObject = parent;
        String[] pathElements = resourcePath.split(STORAGE_PATH_SEPARATOR);
        for (String pathElement : pathElements) {
            currentPathObject = findResource(currentPathObject.getId(), pathElement);
            if (currentPathObject == null) {
                if (exceptionIfNotExists) {
                    throw new ResourceResolverException("Can't resolve " + resourcePath + ". " + pathElement + " is unknown");
                }
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
            LOGGER.warn("Error while finding resource with id " + parentId, e);
            throw new ResourceResolverException("Error while finding resource with id " + parentId, e);
        }
        for (FlashSafeStorageFileObject resource : content) {
            if (resourceName.equals(resource.getName())) {
                return resource;
            }
        }
        return null;
    }

}
