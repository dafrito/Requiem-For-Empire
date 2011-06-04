package com.dafrito.rfe.gui.debug;

import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;

import javax.swing.DefaultListModel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.dafrito.rfe.strings.NamedTreePath;

public class Debug_Tree extends JPanel implements ActionListener, MouseListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8224462858092721477L;
	private Debug_Filter filter;
	private boolean isReloading = false;
	private JTree tree;
	private JPopupMenu popup;
	private JMenuItem copySelectedNodeGroup, copySelectedNodeData;
	private Debug_TreeNode currentNode, lastChild;

	// Constructor
	public Debug_Tree(Debug_Filter filter) {
		this.filter = filter;
		this.setLayout(new GridLayout(1, 0));
		this.add(this.tree = new JTree(new DefaultTreeModel(this.currentNode = new Debug_TreeNode(CommonString.OUTPUTTREE))));
		this.popup = new JPopupMenu();
		this.popup.add(this.copySelectedNodeData = new JMenuItem("Copy Selected"));
		this.popup.add(this.copySelectedNodeGroup = new JMenuItem("Copy Selected's Group"));
		this.copySelectedNodeGroup.addActionListener(this);
		this.copySelectedNodeData.addActionListener(this);
		this.tree.addMouseListener(this);
	}

	// ActionListener implementation
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(this.copySelectedNodeGroup)) {
			if (this.getSelectedNode() != null && this.getSelectedNode().getGroup() != null) {
				StringSelection ss = new StringSelection(this.getSelectedNode().getGroup().toString());
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
			}
		} else if (e.getSource().equals(this.copySelectedNodeData)) {
			if (this.getSelectedNode() != null) {
				StringSelection ss = new StringSelection(this.getSelectedNode().getData().toString());
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
			}
		}
	}

	// Node shiz
	public void addNode(Debug_TreeNode node) {
		this.currentNode.addChild(node);
		this.lastChild = node;
		int[] array = new int[1];
		array[0] = this.currentNode.getChildCount() - 1;
		((DefaultTreeModel) this.tree.getModel()).nodesWereInserted(this.currentNode, array);
	}

	public void closeNode() {
		this.currentNode = (Debug_TreeNode) this.currentNode.getPracticalParent();
		assert this.currentNode != null : "CurrentNode is null after closeNode - potentially a redundant closenode has caused us to leave the tree.";
		if (this.currentNode.isRoot()) {
			this.getFilter().setListening(false);
		}
	}

	public Object[] getAvailableFilterArray() {
		if (this.getSelectedNode() == null || this.getSelectedNode().getGroup() == null) {
			return null;
		}
		Object[] array = new Object[2];
		array[0] = this.getSelectedNode().getGroup();
		array[1] = this.getSelectedNode().getData();
		return array;
	}

	public Debug_TreeNode getCurrentNode() {
		return this.currentNode;
	}

	public Debug_TreeNode getDataByFilter(DefaultListModel data) {
		return this.currentNode.filterByData(data);
	}

	// Quick retrievals
	public Debug_Filter getFilter() {
		return this.filter;
	}

	public Object getFirstAvailableFilter() {
		if (this.getSelectedNode() == null) {
			return JOptionPane.showInputDialog(null, "Insert Filter Name", "Debug Filter", JOptionPane.INFORMATION_MESSAGE);
		}
		if (this.getSelectedNode().getGroup() != null) {
			return this.getSelectedNode().getGroup();
		}
		return this.getSelectedNode().getData();
	}

	public Debug_TreeNode getLastNodeAdded() {
		return this.lastChild;
	}

	public Debug_TreeNode getSelectedNode() {
		if (this.tree.getSelectionPath() != null) {
			return (Debug_TreeNode) this.tree.getSelectionPath().getLastPathComponent();
		}
		return null;
	}

	// Seminode shiz
	public TreePath getSelectedRelativeTreePath() {
		if (this.getSelectedNode() == null) {
			return null;
		}
		TreePath path = ((Debug_TreeNode) this.tree.getSelectionPath().getLastPathComponent()).getRelativeTreePath();
		if (((Debug_TreeNode) path.getPath()[0]).getData().equals((this.getCurrentNode().getRoot().getData()))) {
			JOptionPane.showMessageDialog(null, "This node, or one of its parents, was created during a capture operation and thus has no real parent.", "Invalid Jump", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		return path;
	}

	public TreePath getSelectionPath() {
		return this.tree.getSelectionPath();
	}

	public boolean isReloading() {
		return this.isReloading;
	}

	private void maybeShowPopup(MouseEvent e) {
		if (!e.isPopupTrigger()) {
			return;
		}
		this.popup.show(e.getComponent(), e.getX(), e.getY());
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	// MouseListener implementation
	@Override
	public void mousePressed(MouseEvent e) {
		TreePath path = this.tree.getPathForLocation(e.getX(), e.getY());
		if (path != null) {
			this.tree.setSelectionPath(path);
		}
		this.maybeShowPopup(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		this.maybeShowPopup(e);
	}

	public void refresh() {
		((DefaultTreeModel) this.tree.getModel()).setRoot(this.currentNode = this.getFilter().getListener().getSource().getTreePanel().getDataByFilter(this.getFilter().getFilters()));
	}

	public void reset() {
		((DefaultTreeModel) this.tree.getModel()).setRoot(this.currentNode = new Debug_TreeNode(CommonString.OUTPUTTREE));
	}

	public void setAsCurrent(Debug_TreeNode node) {
		this.currentNode = node;
		assert this.currentNode != null;
	}

	public void setSelectionPath(TreePath path) {
		this.tree.setSelectionPath(path);
	}

	public void showTreePath(TreePath path) {
		java.util.List<Debug_TreeNode> pathList = new LinkedList<Debug_TreeNode>();
		for (int i = 0; i < path.getPath().length; i++) {
			pathList.add((Debug_TreeNode) path.getPath()[i]);
		}
		Debug_TreeNode node = this.currentNode.getRoot();
		if (!node.equals(pathList.get(0))) {
			assert false : "Invalid path (Roots do not match) Root: " + node.getData() + " OtherRoot: " + pathList.get(0);
		}
		for (int i = 1; i < pathList.size(); i++) {
			Debug_TreeNode pathNode = pathList.get(i); // first child
			if (node.getNode(pathNode) != null) {
				node = node.getNode(pathNode);
				continue;
			}
			if (pathNode.getGroup() == null) {
				assert false : "Invalid Path (PathNode has no group)";
			}
			java.util.List<Debug_TreeNode> groupNodes = node.getNodesByData(pathNode.getGroup());
			boolean flag = false;
			for (Debug_TreeNode groupNode : groupNodes) {
				if (groupNode.getNode(pathNode) != null) {
					pathList.add(i, groupNode);
					i++;
					node = groupNode.getNode(pathNode);
					flag = true;
					break;
				}
			}
			if (!flag) {
				assert false : "Invalid path (GroupNode could not be found)";
			}
		}
		if (path instanceof NamedTreePath) {
			path = new NamedTreePath(((NamedTreePath) path).getName(), pathList.toArray());
		} else {
			path = new TreePath(pathList.toArray());
		}
		this.tree.setSelectionPath(path);
		this.tree.makeVisible(path);
	}
}
