package ru.flashsafe.core.old.storage.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ru.flashsafe.core.old.storage.FlashSafeStorageDirectory;
import ru.flashsafe.core.util.AbstractTreeNode;
import ru.flashsafe.core.util.Tree;
import ru.flashsafe.core.util.TreeNode;

public class StorageDirectoryTree implements Tree<FlashSafeStorageDirectory> {

    private final DirectoryTreeNode root;
    
    public StorageDirectoryTree(DirectoryTreeNode rootNode) {
        root = rootNode;
    }
    
    public StorageDirectoryTree(FlashSafeStorageDirectory root) {
        this(new DirectoryTreeNode(null, root));
    }
    
    @Override
    public DirectoryTreeNode getRoot() {
        return root;
    }
    
    @Override
    public TreeNode<FlashSafeStorageDirectory> getParent() {
        return root.getParent();
    }

    @Override
    public FlashSafeStorageDirectory get() {
        return root.get();
    }

    @Override
    public Collection<DirectoryTreeNode> getChildren() {
        return root.getChildren();
    }
    
    public static class DirectoryTreeNode extends AbstractTreeNode<FlashSafeStorageDirectory> {

        private final Map<String, DirectoryTreeNode> nameToDirectoryMap;

        public DirectoryTreeNode(TreeNode<FlashSafeStorageDirectory> parent, FlashSafeStorageDirectory data) {
            super(parent, data);
            nameToDirectoryMap = data.getCount() > 0 ? new HashMap<String, DirectoryTreeNode>(data.getCount())
                    : new HashMap<String, DirectoryTreeNode>();
        }

        @Override
        public Collection<DirectoryTreeNode> getChildren() {
            return nameToDirectoryMap.values();
        }

        public DirectoryTreeNode getChildByDirectoryName(String name) {
            return nameToDirectoryMap.get(name);
        }

        public void addChild(FlashSafeStorageDirectory data) {
            nameToDirectoryMap.put(data.getName(), new DirectoryTreeNode(this, data));
        }
    }
}
