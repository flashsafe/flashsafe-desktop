package ru.flashsafe.core.old.storage.util;

import ru.flashsafe.core.old.storage.FlashSafeStorageDirectory;
import ru.flashsafe.core.old.storage.util.StorageDirectoryTree.DirectoryTreeNode;

public class StorageDirectoryTest {

    public static void main(String[] args) {
        StorageDirectoryTree tree = new StorageDirectoryTree(new FlashSafeStorageDirectory());
        for (int i = 0; i < 5; i ++) {
            FlashSafeStorageDirectory directory = new FlashSafeStorageDirectory();
            directory.setName("Name " + i);
            tree.getRoot().addChild(directory);
        }
        
        for (DirectoryTreeNode treeNode : tree.getChildren()) {
            System.out.println(treeNode.get().getName());
        }
    }

}
