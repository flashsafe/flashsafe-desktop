package ru.flashsafe.core.storage.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ru.flashsafe.core.util.Tree;
import ru.flashsafe.core.util.TreeNode;

public class StorageDirectoryIdsTree implements Tree<Long> {

    private final StorageDirectoryNode root;
    
    private final Long directoryId;
    
    public StorageDirectoryIdsTree(StorageDirectoryNode parent, Long directoryId) {
        root = new StorageDirectoryNode(parent, directoryId);
        this.directoryId = directoryId;
    }
    
    public StorageDirectoryIdsTree(Long directoryId) {
        this(null, directoryId);
    }
    
    @Override
    public TreeNode<Long> getParent() {
        return root.getParent();
    }

    @Override
    public Long get() {
        return directoryId;
    }

    @Override
    public Collection<? extends TreeNode<Long>> getChildren() {
        return root.getChildren();
    }

    @Override
    public StorageDirectoryNode getRoot() {
        return root;
    }
    
    public static class StorageDirectoryNode implements TreeNode<Long> {

        private final TreeNode<Long> parent;
        
        private final Long data;
        
        private final Map<String, StorageDirectoryNode> children = new HashMap<>();
        
        public StorageDirectoryNode(StorageDirectoryNode parent, Long data) {
            this.parent = parent;
            this.data = data;
        }
        
        @Override
        public TreeNode<Long> getParent() {
            return parent;
        }

        @Override
        public Long get() {
            return data;
        }

        @Override
        public Collection<StorageDirectoryNode> getChildren() {
            return children.values();
        }

        public StorageDirectoryNode getChildByName(String childName) {
            return children.get(childName);
        }

        public StorageDirectoryNode addChild(String name, Long id) {
            StorageDirectoryNode node = new StorageDirectoryNode(this, id);
            children.put(name, node);
            return node;
        }
        
        public void removeChild(String name) {
            children.remove(name);
        }
    }
}
