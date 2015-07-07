package ru.flashsafe.core.util;


public abstract class AbstractTreeNode<T> implements TreeNode<T> {

    private final TreeNode<T> parent;
    
    private final T data;
    
    public AbstractTreeNode(TreeNode<T> parent, T data) {
        this.parent = parent;
        this.data = data;
    }
    
    @Override
    public TreeNode<T> getParent() {
        return parent;
    }

    @Override
    public T get() {
        return data;
    }

}
