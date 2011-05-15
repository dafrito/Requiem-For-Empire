import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class SplitterThread extends Thread {
	private DiscreteRegionBSPNode m_root;
	private Set<DiscreteRegion> m_regions;
	private boolean m_recurse;
	private static int m_threadNum = 0;
	public static final String SPLITTERTHREADSTRING = "Splitter Pipeline ";
	private Terrestrial m_terrestrial;

	public SplitterThread(Terrestrial terrestrial, DiscreteRegionBSPNode root, Collection<DiscreteRegion> regions, boolean recurse) {
		super(SPLITTERTHREADSTRING + " " + m_threadNum++);
		this.m_regions = new HashSet<DiscreteRegion>(regions);
		this.m_root = root;
		this.m_recurse = recurse;
		this.m_terrestrial = terrestrial;
	}

	public SplitterThread(Terrestrial terrestrial, DiscreteRegionBSPNode root, DiscreteRegion region, boolean recurse) {
		super(SPLITTERTHREADSTRING + " " + m_threadNum++);
		this.m_regions = new HashSet<DiscreteRegion>();
		this.m_regions.add(region);
		this.m_root = root;
		this.m_recurse = recurse;
		this.m_terrestrial = terrestrial;
	}

	@Override
	public void run() {
		Debugger.hitStopWatch(Thread.currentThread().getName());
		assert Debugger.openNode("Splitter Thread Executions", "Executing Splitter Thread (" + this.m_regions.size() + " region(s))");
		assert Debugger.addSnapNode("Regions (" + this.m_regions.size() + " region(s))", this.m_regions);
		Iterator iter = this.m_regions.iterator();
		while (iter.hasNext()) {
			DiscreteRegion region = (DiscreteRegion) iter.next();
			List regionPoints = region.getPoints();
			this.m_root = RiffPolygonToolbox.removeOverlappingPolygons(this.m_root, region, this.m_recurse);
			if (!this.m_recurse) {
				break;
			}
		}
		if (this.m_recurse) {
			Set<DiscreteRegion> polygons = this.m_root.getRegionList();
			Iterator polyIter = polygons.iterator();
			Set<DiscreteRegion> neighbors = new HashSet<DiscreteRegion>();
			this.m_root.clearTempList();
			for (DiscreteRegion thisRegion : this.m_root.getRegionList()) {
				thisRegion.resetNeighbors();
				thisRegion.addRegionNeighbors(polygons);
			}
			if (!neighbors.equals(polygons)) {
				assert Debugger.openNode("Neighbors list does not match poly-list");
				assert Debugger.addNode("If the BSP tree is intended to be valid at all points within its maximum bounds, then this is an error.");
				assert Debugger.addNode("Otherwise, it can be safely ignored.");
				assert Debugger.addSnapNode("Neighbors (" + neighbors.size() + " neighbor(s))", neighbors);
				assert Debugger.addSnapNode("Polygons (" + polygons.size() + " polygon(s))", polygons);
				neighbors.removeAll(polygons);
				assert Debugger.addSnapNode("Offending polygons", neighbors);
				assert Debugger.closeNode();
				this.m_root.addToTempList(neighbors);
			}
		}
		this.m_terrestrial.decrementOpenThreads();
		assert Debugger.closeNode(this.m_root);
		Debugger.hitStopWatch(Thread.currentThread().getName());
	}
}
