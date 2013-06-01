package com.dafrito.rfe.geom;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.dafrito.rfe.Terrestrial;
import com.dafrito.rfe.logging.Logs;

public class SplitterThread extends Thread {
	private DiscreteRegionBSPNode root;
	private Set<DiscreteRegion> regions;
	private boolean recurse;
	private static int threadNum = 0;
	public static final String SPLITTERTHREADSTRING = "Splitter Pipeline";
	private Terrestrial terrestrial;

	public SplitterThread(Terrestrial terrestrial, DiscreteRegionBSPNode root, Collection<DiscreteRegion> regions, boolean recurse) {
		super(SPLITTERTHREADSTRING + " " + threadNum++);
		this.regions = new HashSet<DiscreteRegion>(regions);
		this.root = root;
		this.recurse = recurse;
		this.terrestrial = terrestrial;
	}

	public SplitterThread(Terrestrial terrestrial, DiscreteRegionBSPNode root, DiscreteRegion region, boolean recurse) {
		super(SPLITTERTHREADSTRING + " " + threadNum++);
		this.regions = new HashSet<DiscreteRegion>();
		this.regions.add(region);
		this.root = root;
		this.recurse = recurse;
		this.terrestrial = terrestrial;
	}

	@Override
	public void run() {
		Logs.hitStopWatch();
		assert Logs.openNode("Splitter Thread Executions", "Executing Splitter Thread (" + this.regions.size() + " region(s))");
		assert Logs.addSnapNode("Regions (" + this.regions.size() + " region(s))", this.regions);
		for (DiscreteRegion region : this.regions) {
			this.root = Polygons.removeOverlappingPolygons(this.root, region, this.recurse);
			if (!this.recurse) {
				break;
			}
		}
		if (this.recurse) {
			Set<DiscreteRegion> polygons = this.root.getRegionList();
			Set<DiscreteRegion> neighbors = new HashSet<DiscreteRegion>();
			this.root.clearTempList();
			for (DiscreteRegion thisRegion : this.root.getRegionList()) {
				thisRegion.resetNeighbors();
				thisRegion.addRegionNeighbors(polygons);
			}
			if (!neighbors.equals(polygons)) {
				assert Logs.openNode("Neighbors list does not match poly-list");
				assert Logs.addNode("If the BSP tree is intended to be valid at all points within its maximum bounds, then this is an error.");
				assert Logs.addNode("Otherwise, it can be safely ignored.");
				assert Logs.addSnapNode("Neighbors (" + neighbors.size() + " neighbor(s))", neighbors);
				assert Logs.addSnapNode("Polygons (" + polygons.size() + " polygon(s))", polygons);
				neighbors.removeAll(polygons);
				assert Logs.addSnapNode("Offending polygons", neighbors);
				assert Logs.closeNode();
				this.root.addToTempList(neighbors);
			}
		}
		this.terrestrial.decrementOpenThreads();
		assert Logs.closeNode(this.root);
		Logs.hitStopWatch();
	}
}
