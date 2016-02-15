package ru.flashsafe.core.util;

import java.util.Collection;

public interface TreeNode<T> {

    TreeNode<T> getParent();
    
    T get();
    
    Collection<? extends TreeNode<T>> getChildren();
    
}