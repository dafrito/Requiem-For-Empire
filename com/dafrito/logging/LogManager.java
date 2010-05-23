package com.dafrito.logging;

import com.dafrito.logging.tree.LogViewTreeNode;

public interface LogManager {
    void addNode(LogViewTreeNode treeNode);
    LogViewTreeNode getLastNodeAdded();
    LogView getUnfilteredOutput();
    boolean isIgnoringThisThread();
    void closeNode();
    void closeNodeTo(Object nodeValue);
    void openNode(LogViewTreeNode treeNode);
    
    void reset();
    boolean ensureCurrentNode(Object nodeValue);
    
    String getBootstrappingClassName();
    void setBootstrappingClassName(String name);
    boolean isResetting();
    void setExceptionsMode(boolean value);

}
