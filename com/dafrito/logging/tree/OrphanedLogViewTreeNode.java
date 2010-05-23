package com.dafrito.logging.tree;

import java.util.LinkedList;
import java.util.List;

import javax.swing.tree.MutableTreeNode;


public class OrphanedLogViewTreeNode extends LogViewTreeNode {

    private List<MutableTreeNode> pathList = new LinkedList<MutableTreeNode>();

    public OrphanedLogViewTreeNode(LogViewTreeNode node) {
        super(node.getUnique(), node.getGroupCode(), node.getDataCode());
        Object[] array = node.getTreePath("Path to Orphan").getPath();
        for(Object elem : array) {
            this.pathList.add((MutableTreeNode)elem);
        }
        for(LogViewTreeNode child : node.getChildren()) {
            addChild(child.duplicate());
        }
    }

    @Override
    public List<MutableTreeNode> getRelativePathFromRoot(List<MutableTreeNode> list) {
        for(int i = 0; i < this.pathList.size(); i++) {
            list.add(i, this.pathList.get(i));
        }
        return list;
    }
}
