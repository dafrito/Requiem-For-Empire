import java.util.*;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.DefaultListModel;
import javax.swing.tree.DefaultMutableTreeNode;
class Incrementor{
	private int m_value;
	public Incrementor(){this(0);}
	public Incrementor(int initial){m_value=initial;}
	public void increment(){increment(1);}
	public void increment(int value){m_value+=value;}
	public int getValue(){return m_value;}
}
class Debug_CacheElement implements Comparable<Debug_CacheElement>{
	private int m_value=1;
	private Object m_data;
	public Debug_CacheElement(Object data,int value){
		m_data=data;
		m_value=value;
	}
	public Object getData(){return m_data;}
	public int getAccessed(){return m_value;}
	public void increment(){m_value++;}
	public int compareTo(Debug_CacheElement elem){
		if(m_value<elem.getAccessed()){return -1;}
		if(m_value==elem.getAccessed()){return 0;}
		return 1;
	}
	public String toString(){return "(Used "+m_value+" time(s)) '"+m_data+"'";}
}
public class Debug_TreeNode implements MutableTreeNode{
	private static int m_identifier=0;
	private static Vector<Object>m_cacheData=new Vector<Object>();
	private static Vector<Incrementor>m_cacheDataRepetitions=new Vector<Incrementor>();
	private static java.util.Map<Object,Integer>m_cacheMap=new HashMap<Object,Integer>();
	public static java.util.Map<DebugString,Object>m_precacheData=new EnumMap<DebugString,Object>(DebugString.class);
	protected final Object m_dataCode,m_groupCode;
	private int m_unique;
	protected MutableTreeNode m_parent,m_practicalParent;
	protected List<Debug_TreeNode>m_children=new LinkedList<Debug_TreeNode>();
	public Debug_TreeNode(Object data){this(m_identifier++,null,data);}
	public Debug_TreeNode(Object group,Object data){this(m_identifier++,group,data);}
	public Debug_TreeNode(int unique,Object group,Object data){
		m_unique=unique;
		assert data!=null;
		if(group!=null){
			if(group instanceof DebugString){
				m_groupCode=(DebugString)group;
			}else if(group instanceof Integer){
				m_groupCode=group;
			}else if(m_cacheMap.containsKey(group)){
				m_groupCode=m_cacheMap.get(group);
				m_cacheDataRepetitions.get(((Integer)m_groupCode).intValue()).increment();
			}else{
				m_cacheData.add(group);
				m_cacheMap.put(group,new Integer(m_cacheData.size()-1));
				m_cacheDataRepetitions.add(new Incrementor(1));
				m_groupCode=new Integer(m_cacheData.size()-1);
			}
		}else{m_groupCode=null;}
		if(data instanceof DebugString){
			m_dataCode=(DebugString)data;
		}else if(data instanceof Integer){
			m_dataCode=data;
		}else if(m_cacheMap.containsKey(data)){
			m_dataCode=m_cacheMap.get(data);
			m_cacheDataRepetitions.get(((Integer)m_dataCode).intValue()).increment();
		}else{
			m_cacheData.add(data);
			m_cacheMap.put(data,new Integer(m_cacheData.size()-1));
			m_cacheDataRepetitions.add(new Incrementor(1));
			m_dataCode=new Integer(m_cacheData.size()-1);
		}
	}
	public static void reset(){
		m_cacheDataRepetitions.clear();
		m_cacheMap.clear();
		m_cacheData.clear();
	}
	public static void addPrecached(DebugString key,Object value){m_precacheData.put(key,value);}
	public static Object getPrecached(DebugString key){return m_precacheData.get(key);}
	public static List<Debug_CacheElement>getCacheData(){
		List<Debug_CacheElement>cacheElements=new LinkedList<Debug_CacheElement>();
		for(int i=0;i<m_cacheData.size();i++){
			cacheElements.add(new Debug_CacheElement(m_cacheData.get(i),m_cacheDataRepetitions.get(i).getValue()));
		}
		Collections.sort(cacheElements);
		Collections.reverse(cacheElements);
		return cacheElements;
	}
	public static void report(){
		java.util.List<Debug_CacheElement>cache=getCacheData();
		System.out.println("Debug Tree Node Report ("+cache.size()+" unique element(s) currently in play)");
		for(int i=0;i<10&&i<cache.size();i++){
			System.out.println(cache.get(i));
		}
	}
	public boolean equals(Object o){
		return m_unique==((Debug_TreeNode)o).getUnique();
	}
	public int getUnique(){return m_unique;}
	public Debug_TreeNode duplicate(){
		Debug_TreeNode node=new Debug_TreeNode(getUnique(),getGroupCode(),getDataCode());
		for(Debug_TreeNode child:getChildren()){
			node.addChild(child.duplicate());
		}
		return node;
	}
	public Debug_TreeNode getNode(Debug_TreeNode node){
		for(Debug_TreeNode child:m_children){
			if(child.equals(node)){return child;}
		}
		return null;
	}
	public List<Debug_TreeNode> getNodesByData(Object data){
		List<Debug_TreeNode>nodes=new LinkedList<Debug_TreeNode>();
		for(Debug_TreeNode child:m_children){
			if(child.getData().equals(data)){
				nodes.add(child);
			}
		}
		return nodes;
	}
	// TreeNode functions
	public List<Debug_TreeNode>getChildren(){return Collections.unmodifiableList(m_children);}
	public Enumeration children(){return Collections.enumeration(m_children);}
	public boolean getAllowsChildren(){return true;}
	public TreeNode getChildAt(int x){return (TreeNode)m_children.get(x);}
	public int getChildCount(){return m_children.size();}
	public int getIndex(TreeNode node){return m_children.indexOf(node);}
	public TreeNode getParent(){return m_parent;}
	public boolean isLeaf(){return m_children.size()==0;}
	// MutableTreeNode functions
	public void remove(int index){m_children.remove(index);}
	public void remove(MutableTreeNode node){m_children.remove(node);}
	public void removeFromParent(){m_parent.remove(this);m_parent=null;}
	public void setParent(MutableTreeNode parent){assert parent!=this;m_parent=parent;}
	public void setPracticalParent(MutableTreeNode parent){m_practicalParent=parent;}
	public TreeNode getPracticalParent(){return m_practicalParent;}
	public void setUserObject(Object data){throw new Exception_InternalError("Unimplemented feature");}
	public void insert(MutableTreeNode child, int index){throw new Exception_InternalError("Unimplemented feature");}
	// Debug_TreeNode extensions
	public TreePath getTreePath(){return getTreePath(getData().toString());}
	public TreePath getTreePath(String name){
		return new NamedTreePath(name,getPathFromRoot(new LinkedList<MutableTreeNode>()).toArray());
	}
	public TreePath getRelativeTreePath(){return getRelativeTreePath(getData().toString());}
	public TreePath getRelativeTreePath(String name){
		return new NamedTreePath(name,getRelativePathFromRoot(new LinkedList<MutableTreeNode>()).toArray());
	}
	public List<MutableTreeNode>getPathFromRoot(List<MutableTreeNode>list){
		list.add(0,this);
		if(!isRoot()){((Debug_TreeNode)getParent()).getPathFromRoot(list);}
		return list;
	}
	public List<MutableTreeNode>getRelativePathFromRoot(List<MutableTreeNode>list){
		list.add(0,this);
		if(!isRoot()){((Debug_TreeNode)getParent()).getRelativePathFromRoot(list);}
		return list;
	}
	public boolean hasChildren(){return m_children.size()>0;}
	public int getTotalChildren(){
		Iterator iter=m_children.iterator();
		int value=m_children.size()+1;
		while(iter.hasNext()){
			value+=((Debug_TreeNode)iter.next()).getChildCount();
		}
		return value;
	}
	public void addAll(List<Debug_TreeNode> nodes){
		for(Debug_TreeNode node:nodes){addChild(node);}
	}
	public synchronized Debug_TreeNode addChild(Object data){return addChild(new Debug_TreeNode(data));}
	public synchronized Debug_TreeNode addChild(Debug_TreeNode node){
		m_children.add(node);
		if(node.getParent()==null){node.setParent(this);}
		if(node.getPracticalParent()==null){node.setPracticalParent(this);}
		return node;
	}
	public Debug_TreeNode getRoot(){
		if(m_parent==null){
			return this;
		}
		assert m_parent!=this:"Node's parent is itself.";
		return ((Debug_TreeNode)getParent()).getRoot();
	}
	public Debug_TreeNode filterByData(DefaultListModel data){
		if(!isRoot()){return getRoot().filterByData(data);}
		Debug_TreeNode filterNode=new Debug_TreeNode("Filtered List: " + data.get(0));
		filterNode.addAll(getChildrenByFilter(data));
		return filterNode;
	}
	public synchronized List<Debug_TreeNode> getChildrenByFilter(DefaultListModel data){
		List<Debug_TreeNode> filtered=new LinkedList<Debug_TreeNode>();
		boolean flag;
		for(Debug_TreeNode node:m_children){
			flag=false;
			for(int i=0;i<data.size();i++){
				if(node.getData().equals(data.get(i))||(node.getGroup()!=null&&node.getGroup().equals(data.get(i)))){
					filtered.add(new Debug_TreeNode_Orphaned(node));flag=true;break;
				}
			}
			if(!flag){filtered.addAll(node.getChildrenByFilter(data));}
		}
		return filtered;
	}
	public Object getData(){
		if(m_dataCode instanceof Integer){
			if(m_cacheData.size()<=((Integer)m_dataCode).intValue()||m_cacheData.get(((Integer)m_dataCode).intValue())==null){return "Unknown Element";}
			return m_cacheData.get(((Integer)m_dataCode).intValue());
		}
		return m_precacheData.get((DebugString)m_dataCode);
	}
	public Object getGroup(){
		if(m_groupCode==null){return null;}
		if(m_groupCode instanceof Integer){
			if(m_cacheData.size()<=((Integer)m_groupCode).intValue()||m_cacheData.get(((Integer)m_groupCode).intValue())==null){return "Unknown Element";}
			return m_cacheData.get(((Integer)m_groupCode).intValue());
		}
		return m_precacheData.get((DebugString)m_groupCode);
	}
	public Object getGroupCode(){return m_groupCode;}
	public Object getDataCode(){return m_dataCode;}
	public boolean isRoot(){return m_parent==null;}
	public Debug_TreeNode getLastChild(){return (Debug_TreeNode)m_children.get(m_children.size()-1);}
	public Debug_TreeNode removeLastChild(){return (Debug_TreeNode)m_children.remove(m_children.size()-1);}
	public String toString(){if(getData()!=null){return getData().toString();}return null;}
}
class Debug_TreeNode_Orphaned extends Debug_TreeNode{
	private List<MutableTreeNode> m_pathList=new LinkedList<MutableTreeNode>();
	public Debug_TreeNode_Orphaned(Debug_TreeNode node){
		super(node.getUnique(),node.getGroupCode(),node.getDataCode());
		Object[]array=node.getTreePath("Path to Orphan").getPath();
		for(Object elem:array){
			m_pathList.add((MutableTreeNode)elem);
		}
		for(Debug_TreeNode child:node.getChildren()){
			addChild(child.duplicate());
		}
	}
	public List<MutableTreeNode>getRelativePathFromRoot(List<MutableTreeNode>list){
		for(int i=0;i<m_pathList.size();i++){
			list.add(i,m_pathList.get(i));
		}
		return list;
	}
}
