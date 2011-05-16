package com.dafrito.rfe;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

class Debug_CacheElement implements Comparable<Debug_CacheElement> {
	private int value = 1;
	private Object data;

	public Debug_CacheElement(Object data, int value) {
		this.data = data;
		this.value = value;
	}

	@Override
	public int compareTo(Debug_CacheElement elem) {
		if (this.value < elem.getAccessed()) {
			return -1;
		}
		if (this.value == elem.getAccessed()) {
			return 0;
		}
		return 1;
	}

	public int getAccessed() {
		return this.value;
	}

	public Object getData() {
		return this.data;
	}

	public void increment() {
		this.value++;
	}

	@Override
	public String toString() {
		return "(Used " + this.value + " time(s)) '" + this.data + "'";
	}
}

public class Debug_TreeNode implements MutableTreeNode {
	private static int identifier = 0;
	private static Vector<Object> cacheData = new Vector<Object>();
	private static Vector<Incrementor> cacheDataRepetitions = new Vector<Incrementor>();
	private static java.util.Map<Object, Integer> cacheMap = new HashMap<Object, Integer>();
	public static java.util.Map<DebugString, Object> precacheData = new EnumMap<DebugString, Object>(DebugString.class);

	public static void addPrecached(DebugString key, Object value) {
		precacheData.put(key, value);
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

	public static Object getPrecached(DebugString key) {
		return precacheData.get(key);
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

	private int unique;

	protected MutableTreeNode parent, practicalParent;

	protected List<Debug_TreeNode> children = new LinkedList<Debug_TreeNode>();

	public Debug_TreeNode(int unique, Object group, Object data) {
		this.unique = unique;
		assert data != null;
		if (group != null) {
			if (group instanceof DebugString) {
				this.groupCode = group;
			} else if (group instanceof Integer) {
				this.groupCode = group;
			} else if (cacheMap.containsKey(group)) {
				this.groupCode = cacheMap.get(group);
				cacheDataRepetitions.get(((Integer) this.groupCode).intValue()).increment();
			} else {
				cacheData.add(group);
				cacheMap.put(group, new Integer(cacheData.size() - 1));
				cacheDataRepetitions.add(new Incrementor(1));
				this.groupCode = new Integer(cacheData.size() - 1);
			}
		} else {
			this.groupCode = null;
		}
		if (data instanceof DebugString) {
			this.dataCode = data;
		} else if (data instanceof Integer) {
			this.dataCode = data;
		} else if (cacheMap.containsKey(data)) {
			this.dataCode = cacheMap.get(data);
			cacheDataRepetitions.get(((Integer) this.dataCode).intValue()).increment();
		} else {
			cacheData.add(data);
			cacheMap.put(data, new Integer(cacheData.size() - 1));
			cacheDataRepetitions.add(new Incrementor(1));
			this.dataCode = new Integer(cacheData.size() - 1);
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

	public synchronized Debug_TreeNode addChild(Debug_TreeNode node) {
		this.children.add(node);
		if (node.getParent() == null) {
			node.setParent(this);
		}
		if (node.getPracticalParent() == null) {
			node.setPracticalParent(this);
		}
		return node;
	}

	public synchronized Debug_TreeNode addChild(Object data) {
		return this.addChild(new Debug_TreeNode(data));
	}

	@Override
	public Enumeration<Debug_TreeNode> children() {
		return Collections.enumeration(this.children);
	}

	public Debug_TreeNode duplicate() {
		Debug_TreeNode node = new Debug_TreeNode(this.getUnique(), this.getGroupCode(), this.getDataCode());
		for (Debug_TreeNode child : this.getChildren()) {
			node.addChild(child.duplicate());
		}
		return node;
	}

	@Override
	public boolean equals(Object o) {
		return this.unique == ((Debug_TreeNode) o).getUnique();
	}

	public Debug_TreeNode filterByData(DefaultListModel data) {
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
		return this.children.get(x);
	}

	@Override
	public int getChildCount() {
		return this.children.size();
	}

	// TreeNode functions
	public List<Debug_TreeNode> getChildren() {
		return Collections.unmodifiableList(this.children);
	}

	public synchronized List<Debug_TreeNode> getChildrenByFilter(DefaultListModel data) {
		List<Debug_TreeNode> filtered = new LinkedList<Debug_TreeNode>();
		boolean flag;
		for (Debug_TreeNode node : this.children) {
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
		return this.children.indexOf(node);
	}

	public Debug_TreeNode getLastChild() {
		return this.children.get(this.children.size() - 1);
	}

	public Debug_TreeNode getNode(Debug_TreeNode node) {
		for (Debug_TreeNode child : this.children) {
			if (child.equals(node)) {
				return child;
			}
		}
		return null;
	}

	public List<Debug_TreeNode> getNodesByData(Object data) {
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

	public int getUnique() {
		return this.unique;
	}

	public boolean hasChildren() {
		return this.children.size() > 0;
	}

	@Override
	public void insert(MutableTreeNode child, int index) {
		throw new Exception_InternalError("Unimplemented feature");
	}

	@Override
	public boolean isLeaf() {
		return this.children.size() == 0;
	}

	public boolean isRoot() {
		return this.parent == null;
	}

	// MutableTreeNode functions
	@Override
	public void remove(int index) {
		this.children.remove(index);
	}

	@Override
	public void remove(MutableTreeNode node) {
		this.children.remove(node);
	}

	@Override
	public void removeFromParent() {
		this.parent.remove(this);
		this.parent = null;
	}

	public Debug_TreeNode removeLastChild() {
		return this.children.remove(this.children.size() - 1);
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
		throw new Exception_InternalError("Unimplemented feature");
	}

	@Override
	public String toString() {
		if (this.getData() != null) {
			return this.getData().toString();
		}
		return null;
	}
}

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

class Incrementor {
	private int value;

	public Incrementor() {
		this(0);
	}

	public Incrementor(int initial) {
		this.value = initial;
	}

	public int getValue() {
		return this.value;
	}

	public void increment() {
		this.increment(1);
	}

	public void increment(int value) {
		this.value += value;
	}
}
