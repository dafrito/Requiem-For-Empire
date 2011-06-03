/**
 * 
 */
package com.dafrito.rfe.gui.debug;

import java.util.LinkedList;
import java.util.List;

import javax.swing.tree.MutableTreeNode;

class Debug_TreeNode_Orphaned extends Debug_TreeNode {
	private List<MutableTreeNode> pathList = new LinkedList<MutableTreeNode>();

	public Debug_TreeNode_Orphaned(Debug_TreeNode node) {
		super(node.getUnique(), node.getGroupCode(), node.getDataCode());
		Object[] array = node.getTreePath("Path to Orphan").getPath();
		for (Object elem : array) {
			this.pathList.add((MutableTreeNode) elem);
		}
		for (Debug_TreeNode child : node.getChildren()) {
			this.addChild(child.duplicate());
		}
	}

	@Override
	public List<MutableTreeNode> getRelativePathFromRoot(List<MutableTreeNode> list) {
		for (int i = 0; i < this.pathList.size(); i++) {
			list.add(i, this.pathList.get(i));
		}
		return list;
	}
}