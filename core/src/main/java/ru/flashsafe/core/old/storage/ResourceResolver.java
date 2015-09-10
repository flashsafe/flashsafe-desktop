package ru.flashsafe.core.old.storage;

import static java.util.Objects.requireNonNull;
import static ru.flashsafe.core.storage.util.StorageUtils.STORAGE_PATH_PREFIX;
import static ru.flashsafe.core.storage.util.StorageUtils.STORAGE_PATH_SEPARATOR;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.flashsafe.core.file.event.FileManagementEventHandlerProvider;
import ru.flashsafe.core.file.event.FileObjectSecurityEvent;
import ru.flashsafe.core.file.event.FileObjectSecurityEventResult;
import ru.flashsafe.core.file.event.FileObjectSecurityEventResult.ResultType;
import ru.flashsafe.core.file.event.FileObjectSecurityHandler;
import ru.flashsafe.core.storage.exception.FlashSafeStorageException;
import ru.flashsafe.core.storage.exception.ResourceResolverException;
import ru.flashsafe.core.storage.util.StorageUtils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * This object uses to map objects' names (paths) to storage Ids.
 * 
 * @author Andrew
 *
 */
@Singleton
public class ResourceResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceResolver.class);
    
    private static final FlashSafeStorageDirectory ROOT_DIRECTORY;

    private final FileManagementEventHandlerProvider handlerProvider;
    
    private final FlashSafeStorageIdBasedService storageService;
    
    private static final long CACHE_MAX_SIZE = 100;
    //TODO move cache to wrapper 
    private final LoadingCache<ResourceCacheKey, FlashSafeStorageFileObject> resourceCache;
    
    //TODO move out to runtime configuration
    static {
        ROOT_DIRECTORY = new FlashSafeStorageDirectory();
        ROOT_DIRECTORY.setId(0);
        ROOT_DIRECTORY.setName(StorageUtils.STORAGE_PATH_PREFIX);
        ROOT_DIRECTORY.setAbsolutePath(StorageUtils.STORAGE_PATH_PREFIX);
    }

    @Inject
    public ResourceResolver(FlashSafeStorageIdBasedService storageService, FileManagementEventHandlerProvider handlerProvider) {
        this.storageService = requireNonNull(storageService);
        this.handlerProvider = handlerProvider;
        this.resourceCache = CacheBuilder.newBuilder().maximumSize(CACHE_MAX_SIZE)
                .build(new CacheLoader<ResourceCacheKey, FlashSafeStorageFileObject>() {
                    @Override
                    public FlashSafeStorageFileObject load(ResourceCacheKey key) throws Exception {
                        LOGGER.debug("Loading resource " + key);
                        return doResolveResource(key.getParent(), key.getResourceName(), key.isExceptionIfNotExists());
                    }
                });
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
        return resolveResource(ROOT_DIRECTORY, removeStoragePrefixIfExists(resourcePath), true);
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
            throw new IllegalStateException("Unexpected behaviour", e);
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
            return resolveResource(ROOT_DIRECTORY, removeStoragePrefixIfExists(resourcePath), false);
        } catch (ResourceResolverException e) {
            throw new IllegalStateException("Unexpected behaviour", e);
        }
    }
    
    private FlashSafeStorageFileObject resolveResource(FlashSafeStorageFileObject parent, String resourcePath,
            final boolean exceptionIfNotExists) throws ResourceResolverException {
        try {
            FlashSafeStorageFileObject resource = resourceCache.get(new ResourceCacheKey(parent, resourcePath,
                    exceptionIfNotExists));
            if (resource == FlashSafeStorageNullFileObject.NULL_OBJECT) {
                resource = doResolveResource(parent, resourcePath, exceptionIfNotExists);
            }
            return resource;
        } catch (Exception e) {
            throw new ResourceResolverException(e);
        }
    }

    private FlashSafeStorageFileObject doResolveResource(FlashSafeStorageFileObject parent, String resourcePath,
            final boolean exceptionIfNotExists) throws ResourceResolverException {
        if (resourcePath.length() == 0) {
            return parent;
        }
        FlashSafeStorageFileObject currentPathObject = parent;
        String[] pathElements = resourcePath.split(STORAGE_PATH_SEPARATOR);
        for (String pathElement : pathElements) {
            currentPathObject = findResource(currentPathObject, pathElement);
            if (currentPathObject == null) {
                if (exceptionIfNotExists) {
                    throw new ResourceResolverException("Can't resolve "+ parent.getAbsolutePath() + resourcePath + ". " + pathElement + " is unknown");
                }
                return FlashSafeStorageNullFileObject.NULL_OBJECT;
            }
        }
        String absolutePath = parent.getAbsolutePath()
                + (parent.getAbsolutePath().endsWith("/") ? resourcePath : "/" + resourcePath);
        currentPathObject.setAbsolutePath(absolutePath);
        return currentPathObject;
    }
    
    private String removeStoragePrefixIfExists(String path) {
        return path.replaceFirst(STORAGE_PATH_PREFIX, StringUtils.EMPTY);
    }

    private FlashSafeStorageFileObject findResource(FlashSafeStorageFileObject parent, String resourceName) throws ResourceResolverException {
        List<FlashSafeStorageFileObject> content;
        try {
            if (parent.isNeedPassword()) {
                FileObjectSecurityHandler handler = handlerProvider.getFileObjectSecurityHandler();
                FileObjectSecurityEventResult eventResult = handler.handle(new FileObjectSecurityEvent(parent));
                if (eventResult.getResult() == ResultType.CONTINUE) {
                    content = storageService.list(parent.getId(), eventResult.getCode());
                } else {
                    // FIXME add specific exception
                    throw new FlashSafeStorageException("Security code request was canceled");
                }
            } else {
                content = storageService.list(parent.getId());
            }
        } catch (FlashSafeStorageException e) {
            LOGGER.warn("Error while finding resource with id " + parent.getId() + " name " + parent.getAbsolutePath(), e);
            throw new ResourceResolverException("Error while finding resource with id " + parent.getId(), e);
        }
        for (FlashSafeStorageFileObject resource : content) {
            if (resourceName.equals(resource.getName())) {
                return resource;
            }
        }
        return null;
    }
    
    private static class ResourceCacheKey {
        
        private final FlashSafeStorageFileObject parent;
        
        private final String resourceName;
        
        private final boolean exceptionIfNotExists;
        
        public ResourceCacheKey(FlashSafeStorageFileObject parent, String resourceName, boolean exceptionIfNotExists) {
            this.parent = parent;
            this.resourceName = resourceName;
            this.exceptionIfNotExists = exceptionIfNotExists;
        }

        public FlashSafeStorageFileObject getParent() {
            return parent;
        }

        public String getResourceName() {
            return resourceName;
        }

        public boolean isExceptionIfNotExists() {
            return exceptionIfNotExists;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((parent == null) ? 0 : parent.hashCode());
            result = prime * result + ((resourceName == null) ? 0 : resourceName.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ResourceCacheKey other = (ResourceCacheKey) obj;
            if (parent == null) {
                if (other.parent != null)
                    return false;
            } else if (!parent.equals(other.parent))
                return false;
            if (resourceName == null) {
                if (other.resourceName != null)
                    return false;
            } else if (!resourceName.equals(other.resourceName))
                return false;
            return true;
        }

        @Override
        public String toString() {
            return "ResourceCacheKey [parent=" + parent.getAbsolutePath() + ", resourceName=" + resourceName + "]";
        }
    }
}
