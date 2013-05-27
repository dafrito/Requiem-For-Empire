package com.dafrito.rfe.gui.debug;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.dafrito.rfe.Incrementor;
import com.dafrito.rfe.strings.NamedTreePath;

public class Debug_TreeNode implements MutableTreeNode {
	private static int identifier = 0;
	private static List<Object> cacheData = new ArrayList<Object>();
	private static List<Incrementor> cacheDataRepetitions = new ArrayList<Incrementor>();
	private static Map<Object, Integer> cacheMap = new HashMap<Object, Integer>();

	private static Map<CommonString, Object> precacheData = new EnumMap<CommonString, Object>(CommonString.class);

	static {
		for (CommonString str : CommonString.values()) {
			precacheData.put(str, str.getText());
		}
	}

	public static List<Debug_CacheElement> getCacheData() {
		List<Debug_CacheElement> cacheElements = new LinkedList<Debug_CacheElement>();
		for (int i = 0; i < cacheData.size(); i++) {
			cacheElements.add(new Debug_CacheElement(cacheData.get(i), cacheDataRepetitions.get(i).getValue()));
		}
		Collections.sort(cacheElements);
		Collections.reverse(cacheElements);
		return cacheElements;
	}

	public static void report() {
		java.util.List<Debug_CacheElement> cache = getCacheData();
		System.out.println("Debug Tree Node Report (" + cache.size() + " unique element(s) currently in play)");
		for (int i = 0; i < 10 && i < cache.size(); i++) {
			System.out.println(cache.get(i));
		}
	}

	public static void reset() {
		cacheDataRepetitions.clear();
		cacheMap.clear();
		cacheData.clear();
	}

	protected final Object dataCode, groupCode;

	private int id;

	protected MutableTreeNode parent, practicalParent;

	protected List<Debug_TreeNode> children;

	protected Debug_TreeNode(int id, Object group, Object data) {
		this.id = id;
		assert data != null;
		if (group != null) {
			if (group instanceof CommonString) {
				this.groupCode = group;
			} else if (group instanceof Integer) {
				this.groupCode = group;
			} else if (cacheMap.containsKey(group)) {
				this.groupCode = cacheMap.get(group);
				cacheDataRepetitions.get(((Integer) this.groupCode).intValue()).increment();
			} else {
				cacheData.add(group);
				cacheMap.put(group, Integer.valueOf(cacheData.size() - 1));
				cacheDataRepetitions.add(new Incrementor(1));
				this.groupCode = Integer.valueOf(cacheData.size() - 1);
			}
		} else {
			this.groupCode = null;
		}
		if (data instanceof CommonString) {
			this.dataCode = data;
		} else if (data instanceof Integer) {
			this.dataCode = data;
		} else if (cacheMap.containsKey(data)) {
			this.dataCode = cacheMap.get(data);
			cacheDataRepetitions.get(((Integer) this.dataCode).intValue()).increment();
		} else {
			cacheData.add(data);
			cacheMap.put(data, Integer.valueOf(cacheData.size() - 1));
			cacheDataRepetitions.add(new Incrementor(1));
			this.dataCode = Integer.valueOf(cacheData.size() - 1);
		}
	}

	public Debug_TreeNode(Object data) {
		this(identifier++, null, data);
	}

	public Debug_TreeNode(Object group, Object data) {
		this(identifier++, group, data);
	}

	public void addAll(List<Debug_TreeNode> nodes) {
		for (Debug_TreeNode node : nodes) {
			this.addChild(node);
		}
	}

	public Debug_TreeNode addChild(Debug_TreeNode node) {
		if (this.children == null) {
			this.children = new ArrayList<Debug_TreeNode>();
		}
		this.children.add(node);
		if (node.getParent() == null) {
			node.setParent(this);
		}
		if (node.getPracticalParent() == null) {
			node.setPracticalParent(this);
		}
		return node;
	}

	public Debug_TreeNode addChild(Object data) {
		return this.addChild(new Debug_TreeNode(data));
	}

	@Override
	public Enumeration<Debug_TreeNode> children() {
		if (this.children != null) {
			return Collections.enumeration(this.children);
		} else {
			return Collections.enumeration(Collections.<Debug_TreeNode> emptyList());
		}
	}

	public Debug_TreeNode duplicate() {
		Debug_TreeNode node = new Debug_TreeNode(this.getID(), this.getGroupCode(), this.getDataCode());
		for (Debug_TreeNode child : this.getChildren()) {
			node.addChild(child.duplicate());
		}
		return node;
	}

	@Override
	public boolean equals(Object o) {
		return this.id == ((Debug_TreeNode) o).getID();
	}

	@Override
	public int hashCode() {
		return this.id;
	}

	public Debug_TreeNode filterByData(DefaultListModel<?> data) {
		if (!this.isRoot()) {
			return this.getRoot().filterByData(data);
		}
		Debug_TreeNode filterNode = new Debug_TreeNode("Filtered List: " + data.get(0));
		filterNode.addAll(this.getChildrenByFilter(data));
		return filterNode;
	}

	@Override
	public boolean getAllowsChildren() {
		return true;
	}

	@Override
	public TreeNode getChildAt(int x) {
		return this.getChildren().get(x);
	}

	@Override
	public int getChildCount() {
		return getChildren().size();
	}

	// TreeNode functions
	public List<Debug_TreeNode> getChildren() {
		if (this.children != null) {
			return this.children;
		} else {
			return Collections.emptyList();
		}
	}

	public List<Debug_TreeNode> getChildrenByFilter(DefaultListModel<?> data) {
		List<Debug_TreeNode> filtered = new ArrayList<Debug_TreeNode>();
		boolean flag;
		for (Debug_TreeNode node : this.getChildren()) {
			flag = false;
			for (int i = 0; i < data.size(); i++) {
				if (node.getData().equals(data.get(i)) || (node.getGroup() != null && node.getGroup().equals(data.get(i)))) {
					filtered.add(new Debug_TreeNode_Orphaned(node));
					flag = true;
					break;
				}
			}
			if (!flag) {
				filtered.addAll(node.getChildrenByFilter(data));
			}
		}
		return filtered;
	}

	public Object getData() {
		if (this.dataCode instanceof Integer) {
			if (cacheData.size() <= ((Integer) this.dataCode).intValue() || cacheData.get(((Integer) this.dataCode).intValue()) == null) {
				return "Unknown Element";
			}
			return cacheData.get(((Integer) this.dataCode).intValue());
		}
		return precacheData.get(this.dataCode);
	}

	public Object getDataCode() {
		return this.dataCode;
	}

	public Object getGroup() {
		if (this.groupCode == null) {
			return null;
		}
		if (this.groupCode instanceof Integer) {
			if (cacheData.size() <= ((Integer) this.groupCode).intValue() || cacheData.get(((Integer) this.groupCode).intValue()) == null) {
				return "Unknown Element";
			}
			return cacheData.get(((Integer) this.groupCode).intValue());
		}
		return precacheData.get(this.groupCode);
	}

	public Object getGroupCode() {
		return this.groupCode;
	}

	@Override
	public int getIndex(TreeNode node) {
		return this.getChildren().indexOf(node);
	}

	public Debug_TreeNode getLastChild() {
		if (getChildren().isEmpty()) {
			return null;
		}
		return this.getChildren().get(this.getChildren().size() - 1);
	}

	public Debug_TreeNode getNode(Debug_TreeNode node) {
		for (Debug_TreeNode child : this.getChildren()) {
			if (child.equals(node)) {
				return child;
			}
		}
		return null;
	}

	public List<Debug_TreeNode> getNodesByData(Object data) {
		if (this.children == null) {
			return Collections.emptyList();
		}
		List<Debug_TreeNode> nodes = new LinkedList<Debug_TreeNode>();
		for (Debug_TreeNode child : this.children) {
			if (child.getData().equals(data)) {
				nodes.add(child);
			}
		}
		return nodes;
	}

	@Override
	public TreeNode getParent() {
		return this.parent;
	}

	public List<MutableTreeNode> getPathFromRoot(List<MutableTreeNode> list) {
		list.add(0, this);
		if (!this.isRoot()) {
			((Debug_TreeNode) this.getParent()).getPathFromRoot(list);
		}
		return list;
	}

	public TreeNode getPracticalParent() {
		return this.practicalParent;
	}

	public List<MutableTreeNode> getRelativePathFromRoot(List<MutableTreeNode> list) {
		list.add(0, this);
		if (!this.isRoot()) {
			((Debug_TreeNode) this.getParent()).getRelativePathFromRoot(list);
		}
		return list;
	}

	public TreePath getRelativeTreePath() {
		return this.getRelativeTreePath(this.getData().toString());
	}

	public TreePath getRelativeTreePath(String name) {
		return new NamedTreePath(name, this.getRelativePathFromRoot(new LinkedList<MutableTreeNode>()).toArray());
	}

	public Debug_TreeNode getRoot() {
		if (this.parent == null) {
			return this;
		}
		assert this.parent != this : "Node's parent is itself.";
		return ((Debug_TreeNode) this.getParent()).getRoot();
	}

	public int getTotalChildren() {
		if (this.children == null) {
			return 1;
		}
		Iterator<Debug_TreeNode> iter = this.children.iterator();
		int value = this.children.size() + 1;
		while (iter.hasNext()) {
			value += (iter.next()).getChildCount();
		}
		return value;
	}

	// Debug_TreeNode extensions
	public TreePath getTreePath() {
		return this.getTreePath(this.getData().toString());
	}

	public TreePath getTreePath(String name) {
		return new NamedTreePath(name, this.getPathFromRoot(new LinkedList<MutableTreeNode>()).toArray());
	}

	public int getID() {
		return this.id;
	}

	public boolean hasChildren() {
		return this.children != null && !this.children.isEmpty();
	}

	@Override
	public void insert(MutableTreeNode child, int index) {
		throw new UnsupportedOperationException("Unimplemented feature");
	}

	@Override
	public boolean isLeaf() {
		return this.children == null || this.children.isEmpty();
	}

	public boolean isRoot() {
		return this.parent == null;
	}

	// MutableTreeNode functions
	@Override
	public void remove(int index) {
		if (this.children != null) {
			this.children.remove(index);
		}
	}

	@Override
	public void remove(MutableTreeNode node) {
		if (this.children != null) {
			this.children.remove(node);
		}
	}

	@Override
	public void removeFromParent() {
		this.parent.remove(this);
		this.parent = null;
	}

	public Debug_TreeNode removeLastChild() {
		if (this.children != null) {
			return this.children.remove(this.children.size() - 1);
		} else {
			return null;
		}
	}

	@Override
	public void setParent(MutableTreeNode parent) {
		assert parent != this;
		this.parent = parent;
	}

	public void setPracticalParent(MutableTreeNode parent) {
		this.practicalParent = parent;
	}

	@Override
	public void setUserObject(Object data) {
		throw new UnsupportedOperationException("Unimplemented feature");
	}

	@Override
	public String toString() {
		if (this.getData() != null) {
			return this.getData().toString();
		}
		return null;
	}
}
