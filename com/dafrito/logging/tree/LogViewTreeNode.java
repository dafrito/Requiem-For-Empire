package com.dafrito.logging.tree;

import com.dafrito.logging.tree.*;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.dafrito.logging.DebugString;
import com.dafrito.debug.Exception_InternalError;

class Incrementor {
    private int value;

    public Incrementor() {
        this(0);
    }

    public Incrementor(int initial) {
        this.value = initial;
    }

    public void increment() {
        increment(1);
    }

    public void increment(int incrementingValue) {
        this.value += incrementingValue;
    }

    public int getValue() {
        return this.value;
    }
}

class Debug_CacheElement implements Comparable<Debug_CacheElement> {
    private int value = 1;
    private Object data;

    public Debug_CacheElement(Object data, int value) {
        this.data = data;
        this.value = value;
    }

    public Object getData() {
        return this.data;
    }

    public int getAccessed() {
        return this.value;
    }

    public void increment() {
        this.value++;
    }

    public int compareTo(Debug_CacheElement elem) {
        if(this.value < elem.getAccessed()) {
            return -1;
        }
        if(this.value == elem.getAccessed()) {
            return 0;
        }
        return 1;
    }

    @Override
    public String toString() {
        return "(Used " + this.value + " time(s)) '" + this.data + "'";
    }
}

public class LogViewTreeNode implements MutableTreeNode {
    
    private static final Map<DebugString, Object> precacheData = new EnumMap<DebugString, Object>(DebugString.class);
    
    static {
        LogViewTreeNode.addPrecached(DebugString.ELEMENTS, "Elements");
        LogViewTreeNode.addPrecached(DebugString.SCRIPTGROUPPARENTHETICAL, "Script Group (parenthetical)");
        LogViewTreeNode.addPrecached(DebugString.SCRIPTGROUPCURLY, "Script Group (curly)");
        LogViewTreeNode.addPrecached(DebugString.NUMERICSCRIPTVALUESHORT, "Numeric Script-Value (short)");
        LogViewTreeNode.addPrecached(DebugString.NUMERICSCRIPTVALUEINT, "Numeric Script-Value (int)");
        LogViewTreeNode.addPrecached(DebugString.NUMERICSCRIPTVALUELONG, "Numeric Script-Value (long)");
        LogViewTreeNode.addPrecached(DebugString.NUMERICSCRIPTVALUEFLOAT, "Numeric Script-Value (float)");
        LogViewTreeNode.addPrecached(DebugString.NUMERICSCRIPTVALUEDOUBLE, "Numeric Script-Value (double)");
        LogViewTreeNode.addPrecached(DebugString.PERMISSIONNULL, "Permission: null");
        LogViewTreeNode.addPrecached(DebugString.PERMISSIONPRIVATE, "Permission: private");
        LogViewTreeNode.addPrecached(DebugString.PERMISSIONPROTECTED, "Permission: protected");
        LogViewTreeNode.addPrecached(DebugString.PERMISSIONPUBLIC, "Permission: public");
        LogViewTreeNode.addPrecached(DebugString.SCRIPTLINE, "Script Line: ");
        LogViewTreeNode.addPrecached(DebugString.SCRIPTOPERATOR, "Script Operator: ");
        LogViewTreeNode.addPrecached(DebugString.ORIGINALSTRING, "Original String: '");
        LogViewTreeNode.addPrecached(DebugString.REFERENCEDELEMENTNULL, "Referenced Element: null");
        LogViewTreeNode.addPrecached(DebugString.OUTPUTTREE, "Output Tree");
    }

    
    private static int identifier = 0;
    private static Vector<Object> cacheData = new Vector<Object>();
    private static Vector<Incrementor> cacheDataRepetitions = new Vector<Incrementor>();
    private static java.util.Map<Object, Integer> cacheMap = new HashMap<Object, Integer>();
    protected final Object dataCode, groupCode;
    private int unique;
    protected MutableTreeNode parent, practicalParent;
    protected List<LogViewTreeNode> children = new LinkedList<LogViewTreeNode>();

    public LogViewTreeNode(Object data) {
        this(identifier++, null, data);
    }

    public LogViewTreeNode(Object group, Object data) {
        this(identifier++, group, data);
    }

    public LogViewTreeNode(int unique, Object group, Object data) {
        this.unique = unique;
        assert data != null;
        if(group != null) {
            if(group instanceof DebugString) {
                this.groupCode = group;
            } else if(group instanceof Integer) {
                this.groupCode = group;
            } else if(cacheMap.containsKey(group)) {
                this.groupCode = cacheMap.get(group);
                cacheDataRepetitions.get(((Integer)this.groupCode).intValue()).increment();
            } else {
                cacheData.add(group);
                cacheMap.put(group, new Integer(cacheData.size() - 1));
                cacheDataRepetitions.add(new Incrementor(1));
                this.groupCode = new Integer(cacheData.size() - 1);
            }
        } else {
            this.groupCode = null;
        }
        if(data instanceof DebugString) {
            this.dataCode = data;
        } else if(data instanceof Integer) {
            this.dataCode = data;
        } else if(cacheMap.containsKey(data)) {
            this.dataCode = cacheMap.get(data);
            cacheDataRepetitions.get(((Integer)this.dataCode).intValue()).increment();
        } else {
            cacheData.add(data);
            cacheMap.put(data, new Integer(cacheData.size() - 1));
            cacheDataRepetitions.add(new Incrementor(1));
            this.dataCode = new Integer(cacheData.size() - 1);
        }
    }

    public static void reset() {
        cacheDataRepetitions.clear();
        cacheMap.clear();
        cacheData.clear();
    }

    public static void addPrecached(DebugString key, Object value) {
        precacheData.put(key, value);
    }

    public static Object getPrecached(DebugString key) {
        return precacheData.get(key);
    }

    public static List<Debug_CacheElement> getCacheData() {
        List<Debug_CacheElement> cacheElements = new LinkedList<Debug_CacheElement>();
        for(int i = 0; i < cacheData.size(); i++) {
            cacheElements.add(new Debug_CacheElement(cacheData.get(i), cacheDataRepetitions.get(i).getValue()));
        }
        Collections.sort(cacheElements);
        Collections.reverse(cacheElements);
        return cacheElements;
    }

    public static void report() {
        java.util.List<Debug_CacheElement> cache = getCacheData();
        System.out.println("Debug Tree Node Report (" + cache.size() + " unique element(s) currently in play)");
        for(int i = 0; i < 10 && i < cache.size(); i++) {
            System.out.println(cache.get(i));
        }
    }

    @Override
    public boolean equals(Object o) {
        return this.unique == ((LogViewTreeNode)o).getUnique();
    }

    public int getUnique() {
        return this.unique;
    }

    public LogViewTreeNode duplicate() {
        LogViewTreeNode node = new LogViewTreeNode(getUnique(), getGroupCode(), getDataCode());
        for(LogViewTreeNode child : getChildren()) {
            node.addChild(child.duplicate());
        }
        return node;
    }

    public LogViewTreeNode getNode(LogViewTreeNode node) {
        for(LogViewTreeNode child : this.children) {
            if(child.equals(node)) {
                return child;
            }
        }
        return null;
    }

    public List<LogViewTreeNode> getNodesByData(Object data) {
        List<LogViewTreeNode> nodes = new LinkedList<LogViewTreeNode>();
        for(LogViewTreeNode child : this.children) {
            if(child.getData().equals(data)) {
                nodes.add(child);
            }
        }
        return nodes;
    }

    // TreeNode functions
    public List<LogViewTreeNode> getChildren() {
        return Collections.unmodifiableList(this.children);
    }

    public Enumeration<? extends TreeNode> children() {
        return Collections.enumeration(this.children);
    }

    public boolean getAllowsChildren() {
        return true;
    }

    public TreeNode getChildAt(int x) {
        return this.children.get(x);
    }

    public int getChildCount() {
        return this.children.size();
    }

    public int getIndex(TreeNode node) {
        return this.children.indexOf(node);
    }

    public TreeNode getParent() {
        return this.parent;
    }

    public boolean isLeaf() {
        return this.children.size() == 0;
    }

    // MutableTreeNode functions
    public void remove(int index) {
        this.children.remove(index);
    }

    public void remove(MutableTreeNode node) {
        this.children.remove(node);
    }

    public void removeFromParent() {
        this.parent.remove(this);
        this.parent = null;
    }

    public void setParent(MutableTreeNode parent) {
        assert parent != this;
        this.parent = parent;
    }

    public void setPracticalParent(MutableTreeNode parent) {
        this.practicalParent = parent;
    }

    public TreeNode getPracticalParent() {
        return this.practicalParent;
    }

    public void setUserObject(Object data) {
        throw new Exception_InternalError("Unimplemented feature");
    }

    public void insert(MutableTreeNode child, int index) {
        throw new Exception_InternalError("Unimplemented feature");
    }

    // Debug_TreeNode extensions
    public TreePath getTreePath() {
        return getTreePath(getData().toString());
    }

    public TreePath getTreePath(String name) {
        return new NamedTreePath(name, getPathFromRoot(new LinkedList<MutableTreeNode>()).toArray());
    }

    public TreePath getRelativeTreePath() {
        return getRelativeTreePath(getData().toString());
    }

    public TreePath getRelativeTreePath(String name) {
        return new NamedTreePath(name, getRelativePathFromRoot(new LinkedList<MutableTreeNode>()).toArray());
    }

    public List<MutableTreeNode> getPathFromRoot(List<MutableTreeNode> list) {
        list.add(0, this);
        if(!isRoot()) {
            ((LogViewTreeNode)getParent()).getPathFromRoot(list);
        }
        return list;
    }

    public List<MutableTreeNode> getRelativePathFromRoot(List<MutableTreeNode> list) {
        list.add(0, this);
        if(!isRoot()) {
            ((LogViewTreeNode)getParent()).getRelativePathFromRoot(list);
        }
        return list;
    }

    public boolean hasChildren() {
        return this.children.size() > 0;
    }

    public int getTotalChildren() {
        int sum = this.getChildren().size() + 1;
        for(LogViewTreeNode node : this.getChildren()) {
            sum += node.getChildCount();
        }
        return sum;
    }

    public void addAll(List<LogViewTreeNode> nodes) {
        for(LogViewTreeNode node : nodes) {
            addChild(node);
        }
    }

    public synchronized LogViewTreeNode addChild(Object data) {
        return addChild(new LogViewTreeNode(data));
    }

    public synchronized LogViewTreeNode addChild(LogViewTreeNode node) {
        this.children.add(node);
        if(node.getParent() == null) {
            node.setParent(this);
        }
        if(node.getPracticalParent() == null) {
            node.setPracticalParent(this);
        }
        return node;
    }

    public LogViewTreeNode getRoot() {
        if(this.parent == null) {
            return this;
        }
        assert this.parent != this : "Node's parent is itself.";
        return ((LogViewTreeNode)getParent()).getRoot();
    }

    public LogViewTreeNode filterByData(DefaultListModel data) {
        if(!isRoot()) {
            return getRoot().filterByData(data);
        }
        LogViewTreeNode filterNode = new LogViewTreeNode("Filtered List: " + data.get(0));
        filterNode.addAll(getChildrenByFilter(data));
        return filterNode;
    }

    public synchronized List<LogViewTreeNode> getChildrenByFilter(DefaultListModel data) {
        List<LogViewTreeNode> filtered = new LinkedList<LogViewTreeNode>();
        boolean flag;
        for(LogViewTreeNode node : this.children) {
            flag = false;
            for(int i = 0; i < data.size(); i++) {
                if(node.getData().equals(data.get(i))
                    || (node.getGroup() != null && node.getGroup().equals(data.get(i)))) {
                    filtered.add(new OrphanedLogViewTreeNode(node));
                    flag = true;
                    break;
                }
            }
            if(!flag) {
                filtered.addAll(node.getChildrenByFilter(data));
            }
        }
        return filtered;
    }

    public Object getData() {
        if(this.dataCode instanceof Integer) {
            if(cacheData.size() <= ((Integer)this.dataCode).intValue()
                || cacheData.get(((Integer)this.dataCode).intValue()) == null) {
                return "Unknown Element";
            }
            return cacheData.get(((Integer)this.dataCode).intValue());
        }
        return precacheData.get(this.dataCode);
    }

    public Object getGroup() {
        if(this.groupCode == null) {
            return null;
        }
        if(this.groupCode instanceof Integer) {
            if(cacheData.size() <= ((Integer)this.groupCode).intValue()
                || cacheData.get(((Integer)this.groupCode).intValue()) == null) {
                return "Unknown Element";
            }
            return cacheData.get(((Integer)this.groupCode).intValue());
        }
        return precacheData.get(this.groupCode);
    }

    public Object getGroupCode() {
        return this.groupCode;
    }

    public Object getDataCode() {
        return this.dataCode;
    }

    public boolean isRoot() {
        return this.parent == null;
    }

    public LogViewTreeNode getLastChild() {
        return this.children.get(this.children.size() - 1);
    }

    public LogViewTreeNode removeLastChild() {
        return this.children.remove(this.children.size() - 1);
    }

    @Override
    public String toString() {
        if(getData() != null) {
            return getData().toString();
        }
        return null;
    }
}
