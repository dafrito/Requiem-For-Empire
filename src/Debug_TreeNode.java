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
	private int m_value = 1;
	private Object m_data;

	public Debug_CacheElement(Object data, int value) {
		this.m_data = data;
		this.m_value = value;
	}

	@Override
	public int compareTo(Debug_CacheElement elem) {
		if (this.m_value < elem.getAccessed()) {
			return -1;
		}
		if (this.m_value == elem.getAccessed()) {
			return 0;
		}
		return 1;
	}

	public int getAccessed() {
		return this.m_value;
	}

	public Object getData() {
		return this.m_data;
	}

	public void increment() {
		this.m_value++;
	}

	@Override
	public String toString() {
		return "(Used " + this.m_value + " time(s)) '" + this.m_data + "'";
	}
}

public class Debug_TreeNode implements MutableTreeNode {
	private static int m_identifier = 0;
	private static Vector<Object> m_cacheData = new Vector<Object>();
	private static Vector<Incrementor> m_cacheDataRepetitions = new Vector<Incrementor>();
	private static java.util.Map<Object, Integer> m_cacheMap = new HashMap<Object, Integer>();
	public static java.util.Map<DebugString, Object> m_precacheData = new EnumMap<DebugString, Object>(DebugString.class);

	public static void addPrecached(DebugString key, Object value) {
		m_precacheData.put(key, value);
	}

	public static List<Debug_CacheElement> getCacheData() {
		List<Debug_CacheElement> cacheElements = new LinkedList<Debug_CacheElement>();
		for (int i = 0; i < m_cacheData.size(); i++) {
			cacheElements.add(new Debug_CacheElement(m_cacheData.get(i), m_cacheDataRepetitions.get(i).getValue()));
		}
		Collections.sort(cacheElements);
		Collections.reverse(cacheElements);
		return cacheElements;
	}

	public static Object getPrecached(DebugString key) {
		return m_precacheData.get(key);
	}

	public static void report() {
		java.util.List<Debug_CacheElement> cache = getCacheData();
		System.out.println("Debug Tree Node Report (" + cache.size() + " unique element(s) currently in play)");
		for (int i = 0; i < 10 && i < cache.size(); i++) {
			System.out.println(cache.get(i));
		}
	}

	public static void reset() {
		m_cacheDataRepetitions.clear();
		m_cacheMap.clear();
		m_cacheData.clear();
	}

	protected final Object m_dataCode, m_groupCode;

	private int m_unique;

	protected MutableTreeNode m_parent, m_practicalParent;

	protected List<Debug_TreeNode> m_children = new LinkedList<Debug_TreeNode>();

	public Debug_TreeNode(int unique, Object group, Object data) {
		this.m_unique = unique;
		assert data != null;
		if (group != null) {
			if (group instanceof DebugString) {
				this.m_groupCode = group;
			} else if (group instanceof Integer) {
				this.m_groupCode = group;
			} else if (m_cacheMap.containsKey(group)) {
				this.m_groupCode = m_cacheMap.get(group);
				m_cacheDataRepetitions.get(((Integer) this.m_groupCode).intValue()).increment();
			} else {
				m_cacheData.add(group);
				m_cacheMap.put(group, new Integer(m_cacheData.size() - 1));
				m_cacheDataRepetitions.add(new Incrementor(1));
				this.m_groupCode = new Integer(m_cacheData.size() - 1);
			}
		} else {
			this.m_groupCode = null;
		}
		if (data instanceof DebugString) {
			this.m_dataCode = data;
		} else if (data instanceof Integer) {
			this.m_dataCode = data;
		} else if (m_cacheMap.containsKey(data)) {
			this.m_dataCode = m_cacheMap.get(data);
			m_cacheDataRepetitions.get(((Integer) this.m_dataCode).intValue()).increment();
		} else {
			m_cacheData.add(data);
			m_cacheMap.put(data, new Integer(m_cacheData.size() - 1));
			m_cacheDataRepetitions.add(new Incrementor(1));
			this.m_dataCode = new Integer(m_cacheData.size() - 1);
		}
	}

	public Debug_TreeNode(Object data) {
		this(m_identifier++, null, data);
	}

	public Debug_TreeNode(Object group, Object data) {
		this(m_identifier++, group, data);
	}

	public void addAll(List<Debug_TreeNode> nodes) {
		for (Debug_TreeNode node : nodes) {
			this.addChild(node);
		}
	}

	public synchronized Debug_TreeNode addChild(Debug_TreeNode node) {
		this.m_children.add(node);
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
	public Enumeration children() {
		return Collections.enumeration(this.m_children);
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
		return this.m_unique == ((Debug_TreeNode) o).getUnique();
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
		return this.m_children.get(x);
	}

	@Override
	public int getChildCount() {
		return this.m_children.size();
	}

	// TreeNode functions
	public List<Debug_TreeNode> getChildren() {
		return Collections.unmodifiableList(this.m_children);
	}

	public synchronized List<Debug_TreeNode> getChildrenByFilter(DefaultListModel data) {
		List<Debug_TreeNode> filtered = new LinkedList<Debug_TreeNode>();
		boolean flag;
		for (Debug_TreeNode node : this.m_children) {
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
		if (this.m_dataCode instanceof Integer) {
			if (m_cacheData.size() <= ((Integer) this.m_dataCode).intValue() || m_cacheData.get(((Integer) this.m_dataCode).intValue()) == null) {
				return "Unknown Element";
			}
			return m_cacheData.get(((Integer) this.m_dataCode).intValue());
		}
		return m_precacheData.get(this.m_dataCode);
	}

	public Object getDataCode() {
		return this.m_dataCode;
	}

	public Object getGroup() {
		if (this.m_groupCode == null) {
			return null;
		}
		if (this.m_groupCode instanceof Integer) {
			if (m_cacheData.size() <= ((Integer) this.m_groupCode).intValue() || m_cacheData.get(((Integer) this.m_groupCode).intValue()) == null) {
				return "Unknown Element";
			}
			return m_cacheData.get(((Integer) this.m_groupCode).intValue());
		}
		return m_precacheData.get(this.m_groupCode);
	}

	public Object getGroupCode() {
		return this.m_groupCode;
	}

	@Override
	public int getIndex(TreeNode node) {
		return this.m_children.indexOf(node);
	}

	public Debug_TreeNode getLastChild() {
		return this.m_children.get(this.m_children.size() - 1);
	}

	public Debug_TreeNode getNode(Debug_TreeNode node) {
		for (Debug_TreeNode child : this.m_children) {
			if (child.equals(node)) {
				return child;
			}
		}
		return null;
	}

	public List<Debug_TreeNode> getNodesByData(Object data) {
		List<Debug_TreeNode> nodes = new LinkedList<Debug_TreeNode>();
		for (Debug_TreeNode child : this.m_children) {
			if (child.getData().equals(data)) {
				nodes.add(child);
			}
		}
		return nodes;
	}

	@Override
	public TreeNode getParent() {
		return this.m_parent;
	}

	public List<MutableTreeNode> getPathFromRoot(List<MutableTreeNode> list) {
		list.add(0, this);
		if (!this.isRoot()) {
			((Debug_TreeNode) this.getParent()).getPathFromRoot(list);
		}
		return list;
	}

	public TreeNode getPracticalParent() {
		return this.m_practicalParent;
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
		if (this.m_parent == null) {
			return this;
		}
		assert this.m_parent != this : "Node's parent is itself.";
		return ((Debug_TreeNode) this.getParent()).getRoot();
	}

	public int getTotalChildren() {
		Iterator iter = this.m_children.iterator();
		int value = this.m_children.size() + 1;
		while (iter.hasNext()) {
			value += ((Debug_TreeNode) iter.next()).getChildCount();
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
		return this.m_unique;
	}

	public boolean hasChildren() {
		return this.m_children.size() > 0;
	}

	@Override
	public void insert(MutableTreeNode child, int index) {
		throw new Exception_InternalError("Unimplemented feature");
	}

	@Override
	public boolean isLeaf() {
		return this.m_children.size() == 0;
	}

	public boolean isRoot() {
		return this.m_parent == null;
	}

	// MutableTreeNode functions
	@Override
	public void remove(int index) {
		this.m_children.remove(index);
	}

	@Override
	public void remove(MutableTreeNode node) {
		this.m_children.remove(node);
	}

	@Override
	public void removeFromParent() {
		this.m_parent.remove(this);
		this.m_parent = null;
	}

	public Debug_TreeNode removeLastChild() {
		return this.m_children.remove(this.m_children.size() - 1);
	}

	@Override
	public void setParent(MutableTreeNode parent) {
		assert parent != this;
		this.m_parent = parent;
	}

	public void setPracticalParent(MutableTreeNode parent) {
		this.m_practicalParent = parent;
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
	private List<MutableTreeNode> m_pathList = new LinkedList<MutableTreeNode>();

	public Debug_TreeNode_Orphaned(Debug_TreeNode node) {
		super(node.getUnique(), node.getGroupCode(), node.getDataCode());
		Object[] array = node.getTreePath("Path to Orphan").getPath();
		for (Object elem : array) {
			this.m_pathList.add((MutableTreeNode) elem);
		}
		for (Debug_TreeNode child : node.getChildren()) {
			this.addChild(child.duplicate());
		}
	}

	@Override
	public List<MutableTreeNode> getRelativePathFromRoot(List<MutableTreeNode> list) {
		for (int i = 0; i < this.m_pathList.size(); i++) {
			list.add(i, this.m_pathList.get(i));
		}
		return list;
	}
}

class Incrementor {
	private int m_value;

	public Incrementor() {
		this(0);
	}

	public Incrementor(int initial) {
		this.m_value = initial;
	}

	public int getValue() {
		return this.m_value;
	}

	public void increment() {
		this.increment(1);
	}

	public void increment(int value) {
		this.m_value += value;
	}
}
