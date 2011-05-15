import java.util.*;

public class DiscreteRegionBinaryNode implements Comparable{
	private DiscreteRegionBinaryNode m_leftNode;
	private DiscreteRegionBinaryNode m_rightNode;
	private Set m_leftSurfaceSet;
	private Set m_bothSidesSet;
	private Set m_rightSurfaceSet;
	private DiscreteRegionBinaryNode m_parentNode;
	Double m_value;
	public DiscreteRegionBinaryNode(DiscreteRegionBinaryNode parent, double value){
		this(parent, new Double(value));
	}
	public DiscreteRegionBinaryNode(DiscreteRegionBinaryNode parent,Double value){
		m_value=value;
		m_parentNode=parent;
		m_leftSurfaceSet=new HashSet();
		m_rightSurfaceSet=new HashSet();
		m_bothSidesSet=new HashSet();
	}
	public Double getValue(){return m_value;}
	public DiscreteRegionBinaryNode getLeftNode(){return m_leftNode;}
	public DiscreteRegionBinaryNode getRightNode(){return m_rightNode;}
	public void inputValue(Double value){
		RiffToolbox.printDebug("DiscreteRegionBinaryNode/inputValue", "Adding this value: " + value + ", to this node with this value: " + m_value);
		if(m_value.compareTo(value)<0){
			RiffToolbox.printDebug("DiscreteRegionBinaryNode/inputValue", "New value is greater than this node's value...");
			if(m_rightNode!=null){
				RiffToolbox.printDebug("DiscreteRegionBinaryNode/inputValue", "Deferring to rightNode.");
				m_rightNode.inputValue(value);
			}else{
				RiffToolbox.printDebug("DiscreteRegionBinaryNode/inputValue", "Creating new rightNode.");
				m_rightNode = new DiscreteRegionBinaryNode(this, value);
			}
		}else if(m_value.compareTo(value)>0){
			RiffToolbox.printDebug("DiscreteRegionBinaryNode/inputValue", "New value is less than this node's value...");
			if(m_leftNode!=null){
				RiffToolbox.printDebug("DiscreteRegionBinaryNode/inputValue", "Deferring to leftNode.");
				m_leftNode.inputValue(value);
			}else{
				RiffToolbox.printDebug("DiscreteRegionBinaryNode/inputValue", "Creating new leftNode.");
				m_leftNode = new DiscreteRegionBinaryNode(this, value);
			}
		}
	}
	public void inputValue(double value){
		inputValue(new Double(value));
	}
	public List getAllListsWithin(double leftExtreme, double rightExtreme){
		return getAllListsWithin(new Double(leftExtreme), new Double(rightExtreme));
	}
	public List getAllListsWithin(Double leftExtreme, Double rightExtreme){
		if(leftExtreme.compareTo(rightExtreme)>0){return getAllListsWithin(rightExtreme, leftExtreme);}
		RiffToolbox.printDebug("DiscreteRegionBinaryNode/getAllListsWithin", "Getting all lists within these extrema: Left extrema: " + leftExtreme + ", right extrema: " + rightExtreme);
		RiffToolbox.printDebug("DiscreteRegionBinaryNode/getAllListsWithin", "This node's value: " + m_value);
		List tempList = new LinkedList();
		if(m_value.equals(leftExtreme)){
			RiffToolbox.printDebug("DiscreteRegionBinaryNode/getAllListsWithin", "Adding rightSurfaceList.");
			tempList.add(m_rightSurfaceSet);
		}
		if(m_value.equals(rightExtreme)){
			RiffToolbox.printDebug("DiscreteRegionBinaryNode/getAllListsWithin", "Adding leftSurfaceList.");
			tempList.add(m_leftSurfaceSet);
		}
		if(m_value.compareTo(leftExtreme)>0&&m_value.compareTo(rightExtreme)<0){
			RiffToolbox.printDebug("DiscreteRegionBinaryNode/getAllListsWithin", "Adding bothSidesSurfaceList.");
			tempList.add(m_bothSidesSet);
		}
		if(m_leftNode!=null){
			tempList.addAll(m_leftNode.getAllListsWithin(leftExtreme, rightExtreme));
		}
		if(m_rightNode!=null){
			tempList.addAll(m_rightNode.getAllListsWithin(leftExtreme, rightExtreme));
		}
		return tempList;
	}
	public Set getSetsFromValue(double value){
		return getSetsFromValue(new Double(value));
	}
	public Set getSetsFromValue(Double value){
		RiffToolbox.printDebug("DiscreteRegionBinaryNode/getSetsFromValue", "Getting all lists from this value: " + value);
		RiffToolbox.printDebug("DiscreteRegionBinaryNode/getSetsFromValue", "This node's value: " + m_value);
		if(m_value.compareTo(value)>0){
			RiffToolbox.printDebug("DiscreteRegionBinaryNode/getSetsFromValue", "Value is less than this node's value.");
			if(m_leftNode!=null){
				return m_leftNode.getSetsFromValue(value);
			}else{
				RiffToolbox.printDebug("DiscreteRegionBinaryNode/getSetsFromValue", "Adding left and bothSides list.");
				Set list = new HashSet();
				list.addAll(m_leftSurfaceSet);
				list.addAll(m_bothSidesSet);
				DiscreteRegionBinaryNode node = getFirstLessThanParent();
				if(node!=null){list.addAll(node.getRightSurfaceSet());}
				return list;
			}
		}else if(m_value.compareTo(value)<0){
			RiffToolbox.printDebug("DiscreteRegionBinaryNode/getSetsFromValue", "Value is greater than this node's value.");
			if(m_rightNode!=null){
				return m_rightNode.getSetsFromValue(value);
			}else{
				RiffToolbox.printDebug("DiscreteRegionBinaryNode/getSetsFromValue", "Adding right and bothSides list.");
				Set list = new HashSet();
				list.addAll(m_rightSurfaceSet);
				list.addAll(m_bothSidesSet);
				DiscreteRegionBinaryNode node = getFirstGreaterThanParent();
				if(node!=null){list.addAll(node.getLeftSurfaceSet());}
				return list;
			}
		}else{
			RiffToolbox.printDebug("DiscreteRegionBinaryNode/getSetsFromValue", "Value is equal to this node's value, returning bothSidesSet.");
			return m_bothSidesSet;
		}
	}
	public DiscreteRegionBinaryNode getFirstGreaterThanParent(){
		if(m_parentNode==null){return null;}
		if(m_value.compareTo(m_parentNode.getValue())<0){return m_parentNode;}
		return m_parentNode.getFirstGreaterThanParent();
	}
	public DiscreteRegionBinaryNode getFirstLessThanParent(){
		if(m_parentNode==null){return null;}
		if(m_value.compareTo(m_parentNode.getValue())>0){return m_parentNode;}
		return m_parentNode.getFirstLessThanParent();
	}
	public void addSurfaceToLeft(DiscreteRegion surface){m_leftSurfaceSet.add(surface);}
	public void addSurfaceToRight(DiscreteRegion surface){m_rightSurfaceSet.add(surface);}
	public void addSurfaceToBothSidesSet(DiscreteRegion surface){m_bothSidesSet.add(surface);}
	public Set getLeftSurfaceSet(){return m_leftSurfaceSet;}
	public Set getRightSurfaceSet(){return m_rightSurfaceSet;}
	public Set getBothSidesSet(){return m_bothSidesSet;}
	public String toString(){
		String string = new String();
		string += "Discrete Region Binary Node: Value: " + m_value;
		string += "\nLeft Surface Set: " + RiffToolbox.displayList(m_leftSurfaceSet);
		string += "\nRight Surface Set: " + RiffToolbox.displayList(m_rightSurfaceSet);
		string += "\nBoth-sides Surface list: " + RiffToolbox.displayList(m_bothSidesSet);
		string += "\nLeft node: " + m_leftNode;
		string += "\nRight node: " + m_rightNode;
		return string;
	}
	public int compareTo(Object o){
		return getValue().compareTo(((DiscreteRegionBinaryNode)o).getValue());
	}
}
