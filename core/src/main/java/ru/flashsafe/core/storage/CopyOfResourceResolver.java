package ru.flashsafe.core.storage;

import java.util.List;
import java.util.NoSuchElementException;

import ru.flashsafe.core.old.storage.FlashSafeStorageFileObject;
import ru.flashsafe.core.old.storage.util.StorageDirectoryIdsTree;
import ru.flashsafe.core.old.storage.util.StorageDirectoryIdsTree.StorageDirectoryNode;

public class CopyOfResourceResolver {

    private final StorageDirectoryIdsTree directoryIdsTree = new StorageDirectoryIdsTree(Long.valueOf(0));

    private final FlashSafeStorageService storageService;

    public CopyOfResourceResolver(FlashSafeStorageService storageService) {
        this.storageService = storageService;
    }

    public long resolveResource(String resourcePath) {
        String[] pathElements = resourcePath.split("/");
        StorageDirectoryNode currentNode = directoryIdsTree.getRoot();
        for (String currentPathElement : pathElements) {
            StorageDirectoryNode nextNode = currentNode.getChildByName(currentPathElement);
            if (nextNode == null) {
                FlashSafeStorageFileObject element = searchElementOnServer(currentNode.get(), currentPathElement);
                nextNode = currentNode.addChild(currentPathElement, element.getId());
            }
            currentNode = nextNode;
        }
        return currentNode.get();
    }

    private FlashSafeStorageFileObject searchElementOnServer(long parentId, String name) {
        List<FlashSafeStorageFileObject> fileObjects = storageService.list(parentId);
        for (FlashSafeStorageFileObject fileObject : fileObjects) {
            if (name.equals(fileObject.getName())) {
                return fileObject;
            }
        }
        throw new NoSuchElementException();
    }

}
