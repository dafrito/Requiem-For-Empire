import java.util.*;
public class SplitterThread extends Thread{
	private DiscreteRegionBSPNode m_root;
	private Set<DiscreteRegion>m_regions;
	private boolean m_recurse;
	private static int m_threadNum=0;
	public static final String SPLITTERTHREADSTRING="Splitter Pipeline ";
	private Terrestrial m_terrestrial;
	public SplitterThread(Terrestrial terrestrial,DiscreteRegionBSPNode root, DiscreteRegion region, boolean recurse){
		super(SPLITTERTHREADSTRING+" "+m_threadNum++);
		m_regions=new HashSet<DiscreteRegion>();
		m_regions.add(region);
		m_root=root;
		m_recurse=recurse;
		m_terrestrial=terrestrial;
	}
	public SplitterThread(Terrestrial terrestrial,DiscreteRegionBSPNode root, Collection<DiscreteRegion>regions, boolean recurse){
		super(SPLITTERTHREADSTRING+" "+m_threadNum++);
		m_regions=new HashSet<DiscreteRegion>(regions);
		m_root=root;
		m_recurse=recurse;
		m_terrestrial=terrestrial;
	}
	public void run(){
		Debugger.hitStopWatch(Thread.currentThread().getName());
		assert Debugger.openNode("Splitter Thread Executions","Executing Splitter Thread ("+m_regions.size()+" region(s))");
		assert Debugger.addSnapNode("Regions ("+m_regions.size()+" region(s))",m_regions);
		Iterator iter=m_regions.iterator();
		while(iter.hasNext()){
			DiscreteRegion region = (DiscreteRegion)iter.next();
			List regionPoints = region.getPoints();
			m_root=RiffPolygonToolbox.removeOverlappingPolygons(m_root, region, m_recurse);
			if(!m_recurse){break;}
		}
		if(m_recurse){
			Set<DiscreteRegion>polygons=m_root.getRegionList();
			Iterator polyIter=polygons.iterator();
			Set<DiscreteRegion>neighbors=new HashSet<DiscreteRegion>();
			m_root.clearTempList();
			for(DiscreteRegion thisRegion:m_root.getRegionList()){
				thisRegion.resetNeighbors();
				thisRegion.addRegionNeighbors(polygons);
			}
			if(!neighbors.equals(polygons)){
				assert Debugger.openNode("Neighbors list does not match poly-list");
				assert Debugger.addNode("If the BSP tree is intended to be valid at all points within its maximum bounds, then this is an error.");
				assert Debugger.addNode("Otherwise, it can be safely ignored.");
				assert Debugger.addSnapNode("Neighbors ("+neighbors.size()+" neighbor(s))",neighbors);
				assert Debugger.addSnapNode("Polygons ("+polygons.size()+" polygon(s))",polygons);
				neighbors.removeAll(polygons);
				assert Debugger.addSnapNode("Offending polygons",neighbors);
				assert Debugger.closeNode();
				m_root.addToTempList(neighbors);
			}
		}
		m_terrestrial.decrementOpenThreads();
		assert Debugger.closeNode(m_root);
		Debugger.hitStopWatch(Thread.currentThread().getName());
	}
}
