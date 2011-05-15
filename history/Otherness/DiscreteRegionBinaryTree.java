import java.util.*;

public class DiscreteRegionBinaryTree{
	private DiscreteRegionBinaryNode m_rootXNode;
	private DiscreteRegionBinaryNode m_rootYNode;
	private List m_discreteRegions;
	public List getFullList(){return m_discreteRegions;}
	public DiscreteRegionBinaryTree(){m_discreteRegions=new LinkedList();}
	public void addRegions(Collection list){
		Iterator iter = list.iterator();
		while(iter.hasNext()){
			addRegion((DiscreteRegion)iter.next());
		}
	}
	public synchronized void addRegion(DiscreteRegion region){
		RiffToolbox.printDebug("DiscreteRegionBinaryTree/addRegion/heavyDebug", "Adding the following region to the binary tree: " + region);
		if(m_rootXNode==null){
			RiffToolbox.printDebug("DiscreteRegionBinaryTree/addRegion/heavyDebug", "Creating new rootXNode with this region's left extrema, left extrema: " + region.getLeftExtreme());
			m_rootXNode=new DiscreteRegionBinaryNode(null, region.getLeftExtreme());
		}else{
			RiffToolbox.printDebug("DiscreteRegionBinaryTree/addRegion/heavyDebug", "Adding left extrema to rootXNode, left extrema: " + region.getLeftExtreme());
			m_rootXNode.inputValue(region.getLeftExtreme());
		}
		RiffToolbox.printDebug("DiscreteRegionBinaryTree/addRegion/heavyDebug", "Adding right extrema to rootXNode, right extrema: " + region.getRightExtreme());
		m_rootXNode.inputValue(region.getRightExtreme());
		if(m_rootYNode==null){
			RiffToolbox.printDebug("DiscreteRegionBinaryTree/addRegion/heavyDebug", "Creating new rootYNode with this region's bottom extrema, bottom extrema: " + region.getBottomExtreme());
			m_rootYNode=new DiscreteRegionBinaryNode(null, region.getBottomExtreme());
		}else{
			RiffToolbox.printDebug("DiscreteRegionBinaryTree/addRegion/heavyDebug", "Adding bottom extrema to rootXNode, bottom extrema: " + region.getBottomExtreme());
			m_rootYNode.inputValue(region.getBottomExtreme());
		}
		RiffToolbox.printDebug("DiscreteRegionBinaryTree/addRegion/heavyDebug", "Adding top extrema to rootXNode, top extrema: " + region.getTopExtreme());
		m_rootYNode.inputValue(region.getTopExtreme());
		m_discreteRegions.add(region);
		for(int i=0;i<m_discreteRegions.size();i++){
			assertRegion((DiscreteRegion)m_discreteRegions.get(i));
		}
	}
	public synchronized void removeRegion(DiscreteRegion region){
		RiffToolbox.printDebug("DiscreteRegionBinaryTree/removeRegion/heavyDebug", "Removing this region: " + region);
		RiffToolbox.printDebug("DiscreteRegionBinaryTree/removeRegion/heavyDebug", "Getting full list between the left extrema and right extrema");
		List fullList = m_rootXNode.getAllListsWithin(region.getLeftExtreme(), region.getRightExtreme());
		for(int i=0;i<fullList.size();i++){
			Set thisList = (Set)fullList.get(i);
			thisList.remove(region);
		}
		RiffToolbox.printDebug("DiscreteRegionBinaryTree/removeRegion/heavyDebug", "Getting full list between the bottom extrema and top extrema");
		fullList = m_rootYNode.getAllListsWithin(region.getBottomExtreme(), region.getTopExtreme());
		for(int i=0;i<fullList.size();i++){
			Set thisList = (Set)fullList.get(i);
			thisList.remove(region);
		}
		m_discreteRegions.remove(region);
	}
	public synchronized void assertRegion(DiscreteRegion region){
		RiffToolbox.printDebug("DiscreteRegionBinaryTree/assertRegion/heavyDebug", "Getting full list between the left extrema and right extrema");
		List fullList = m_rootXNode.getAllListsWithin(region.getLeftExtreme(), region.getRightExtreme());
		for(int i=0;i<fullList.size();i++){
			Set thisList = (Set)fullList.get(i);
			thisList.add(region);
		}
		RiffToolbox.printDebug("DiscreteRegionBinaryTree/assertRegion/heavyDebug", "Getting full list between the bottom extrema and top extrema");
		fullList = m_rootYNode.getAllListsWithin(region.getBottomExtreme(), region.getTopExtreme());
		for(int i=0;i<fullList.size();i++){
			Set thisList = (Set)fullList.get(i);
			thisList.add(region);
		}
	}
	public synchronized void recreateTree(){
		Collections.sort(m_discreteRegions);
		RiffToolbox.printDebug("DiscreteRegionBinaryTree/recreateTree/heavyDebug", "Recreating this binary tree. Old list follows: ");
		RiffToolbox.printDebug("DiscreteRegionBinaryTree/recreateTree/heavyDebug", RiffToolbox.displayList(m_discreteRegions));
		List list = new LinkedList();
		for(int i=0;;i++){
			int size=list.size();
			if(m_discreteRegions.size()/2+i < m_discreteRegions.size()){
				list.add(m_discreteRegions.get(m_discreteRegions.size()/2+i));
			}
			if(m_discreteRegions.size()/2-i >= 0){
				list.add(m_discreteRegions.get(m_discreteRegions.size()/2-i));
			}
			if(size==list.size()){break;}
		}
		RiffToolbox.printDebug("DiscreteRegionBinaryTree/recreateTree/heavyDebug", "Sorted tree follows: ");
		RiffToolbox.printDebug("DiscreteRegionBinaryTree/recreateTree/heavyDebug", RiffToolbox.displayList(list));
		addRegions(list);
	}
	public Set testPoint(RiffAbsolutePoint point){
		RiffToolbox.printDebug("DiscreteRegionBinaryTree/testPoint/heavyDebug", "Getting X-polygon list...");
		Set regionXTestList = m_rootXNode.getSetsFromValue(point.getX());
		RiffToolbox.printDebug("DiscreteRegionBinaryTree/testPoint/heavyDebug", "Getting Y-polygon list...");
		Set regionYTestList = m_rootYNode.getSetsFromValue(point.getY());
		
		RiffToolbox.printDebug("DiscreteRegionBinaryTree/testPoint/heavyDebug", "\nX-Test List: " + RiffToolbox.displayList(regionXTestList));
		RiffToolbox.printDebug("DiscreteRegionBinaryTree/testPoint/heavyDebug", "\nY-Test List: " + RiffToolbox.displayList(regionYTestList));
		Set polyList = new HashSet();
		Iterator iter=regionXTestList.iterator();
		while(iter.hasNext()){
			DiscreteRegion region = (DiscreteRegion)iter.next();
			if(regionYTestList.contains(region)){
				polyList.add(region);
			}
		}
		return polyList;
	}
	public String toString(){
		String string = new String();
		string += "Discrete Region Binary Tree: ";
		string += "\nRoot X-node: " + m_rootXNode;
		string += "\nRoot Y-node: " + m_rootYNode;
		return string;
	}
}
