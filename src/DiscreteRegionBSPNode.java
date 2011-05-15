import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class DiscreteRegionBSPNode implements Nodeable {
	private Point m_pointA, m_pointB;
	private DiscreteRegionBSPNode m_leftNode, m_rightNode;
	private Set<DiscreteRegion> m_leftNeighbors;
	private Set<DiscreteRegion> m_rightNeighbors;
	private Set<DiscreteRegion> m_tempList;
	private DiscreteRegionBSPNode m_root;
	private int m_openThreads = 0;

	public DiscreteRegionBSPNode(DiscreteRegion region) {
		this(null, region.getPoints().get(0), region.getPoints().get(1));
		this.m_root = this;
		this.addRegion(region);
	}

	public DiscreteRegionBSPNode(DiscreteRegionBSPNode root, Object a, Object b) {
		this(root, (Point) a, (Point) b);
	}

	public DiscreteRegionBSPNode(DiscreteRegionBSPNode root, Point pointA, Point pointB) {
		this.m_root = root;
		this.m_pointA = pointA;
		this.m_pointB = pointB;
		this.m_leftNeighbors = new HashSet<DiscreteRegion>();
		this.m_rightNeighbors = new HashSet<DiscreteRegion>();
		this.m_tempList = new HashSet<DiscreteRegion>();
	}

	public void addLine(DiscreteRegion owner, Object pointA, Object pointB) {
		this.addLine(owner, (Point) pointA, (Point) pointB);
	}

	public synchronized void addLine(DiscreteRegion owner, Point pointA, Point pointB) {
		assert Debugger.openNode("BSP Line Additions", "Adding line to BSP tree (" + pointA + ", " + pointB + ")");
		assert Debugger.addSnapNode("Current node (" + this.m_pointA + ", " + this.m_pointB + ")", this);
		PointSideStruct struct = RiffPolygonToolbox.getPointSideList(this.m_pointA, this.m_pointB, pointA, pointB);
		assert Debugger.addNode(struct);
		if (RiffPolygonToolbox.testForColinearity(pointA, pointB, this.m_pointA, this.m_pointB) || struct.isColinear()) {
			assert Debugger.closeNode();
			return;
		}
		if (struct.isLessThan()) {
			assert Debugger.addNode("Line is less than this node's line.");
			if (this.m_leftNode != null) {
				assert Debugger.openNode("Deferring to left node");
				this.m_leftNode.addLine(owner, pointA, pointB);
				assert Debugger.closeNode();
				assert Debugger.closeNode();
				return;
			}
			this.m_leftNode = new DiscreteRegionBSPNode(this.m_root, pointA, pointB);
			assert Debugger.addNode("Creating new left node.", this.m_leftNode);
			this.m_leftNode.categorizeRegion(owner);
		} else if (struct.isGreaterThan()) {
			assert Debugger.addNode("Line is greater than this node's line.");
			if (this.m_rightNode != null) {
				assert Debugger.openNode("Deferring to right node");
				this.m_rightNode.addLine(owner, pointA, pointB);
				assert Debugger.closeNode();
				assert Debugger.closeNode();
				return;
			}
			this.m_rightNode = new DiscreteRegionBSPNode(this.m_root, pointA, pointB);
			assert Debugger.addNode("Creating new right node.", this.m_rightNode);
			this.m_rightNode.categorizeRegion(owner);
		}
		assert Debugger.closeNode();
	}

	public synchronized void addRegion(DiscreteRegion region) {
		assert Debugger.openNode("BSP Region Additions", "Adding region to BSP tree");
		assert Debugger.addNode(region);
		assert Debugger.addSnapNode("Current node (" + this.m_pointA + ", " + this.m_pointB + ")", this);
		RiffPolygonToolbox.optimizePolygon(region);
		PointSideStruct struct = RiffPolygonToolbox.getPointSideList(region, this.m_pointA, this.m_pointB);
		if (struct.isStraddling()) {
			assert Debugger.addNode("Region is straddling this node's line, splitting.");
			this.m_root.removeRegion(region);
			DiscreteRegion splitPolygon = RiffPolygonToolbox.splitPolygonUsingEdge(region, this.m_pointA, this.m_pointB, true);
			if (splitPolygon == null) {
				assert Debugger.addNode("Unexpected null region from split.");
				assert Debugger.addNode(struct);
			}
			this.m_root.addRegion(region);
			this.m_root.addRegion(splitPolygon);
			assert Debugger.closeNode();
			return;
		}
		if (struct.isLessThan()) {
			assert Debugger.addNode("Region is less than this node's line.");
			if (struct.hasIndeterminates()) {
				assert Debugger.addNode("Region has points that are colinear with this node's line, so adding it to left neighbors.");
				this.m_leftNeighbors.add(region);
				region.addRegionNeighbors(this.m_rightNeighbors);
			}
			if (this.m_leftNode != null) {
				assert Debugger.openNode("Deferring to left node");
				this.m_leftNode.addRegion(region);
				assert Debugger.closeNode();
				assert Debugger.closeNode();
				return;
			}
			List pointList = region.getPoints();
			for (int i = 0; i < pointList.size(); i++) {
				this.addLine(region, pointList.get(i), pointList.get((i + 1) % pointList.size()));
			}
		} else if (struct.isGreaterThan()) {
			assert Debugger.addNode("Region is greater than this node's line.");
			if (struct.hasIndeterminates()) {
				assert Debugger.addNode("Region has points that are colinear with this node's line, so adding it to right neighbors.");
				this.m_rightNeighbors.add(region);
				region.addRegionNeighbors(this.m_leftNeighbors);
			}
			if (this.m_rightNode != null) {
				assert Debugger.openNode("Deferring to right node");
				this.m_rightNode.addRegion(region);
				assert Debugger.closeNode();
				assert Debugger.closeNode();
				return;
			}
			List pointList = region.getPoints();
			for (int i = 0; i < pointList.size(); i++) {
				this.addLine(region, pointList.get(i), pointList.get((i + 1) % pointList.size()));
			}
		}
		assert Debugger.closeNode();
	}

	public synchronized void addRegions(Collection<DiscreteRegion> regions) {
		for (DiscreteRegion region : regions) {
			this.addRegion(region);
		}
	}

	public synchronized void addToTempList(Collection<DiscreteRegion> regions) {
		assert Debugger.addSnapNode("Temporary Region List Additions", "Adding regions to temporary region list", regions);
		this.m_tempList.addAll(regions);
	}

	public synchronized void addToTempList(DiscreteRegion region) {
		assert Debugger.addSnapNode("Temporary Region List Additions", "Adding region to temporary region list", region);
		this.m_tempList.add(region);
	}

	public synchronized void categorizeRegion(DiscreteRegion region) {
		assert Debugger.openNode("Region Categorizations", "Categorizing Region");
		assert Debugger.addNode(region);
		assert Debugger.addNode(this);
		RiffPolygonToolbox.optimizePolygon(region);
		PointSideStruct struct = RiffPolygonToolbox.getPointSideList(region, this.m_pointA, this.m_pointB);
		assert Debugger.addNode(struct);
		if (struct.isLessThan() && struct.hasIndeterminates()) {
			assert Debugger.addNode("Region has points which are less than or equal to this node's line, adding to left neighbors.");
			this.m_leftNeighbors.add(region);
			region.addRegionNeighbors(this.m_rightNeighbors);
		}
		if (struct.isGreaterThan() && struct.hasIndeterminates()) {
			assert Debugger.addNode("Region has points which are greater than or equal to this node's line, adding to right neighbors.");
			this.m_rightNeighbors.add(region);
			region.addRegionNeighbors(this.m_leftNeighbors);
		}
		assert Debugger.closeNode();
	}

	public synchronized void clearTempList() {
		assert Debugger.addNode("Clearing temporary region list");
		this.m_tempList.clear();
	}

	public synchronized List<Asset> getAllAssets() {
		List<Asset> assets = new LinkedList<Asset>();
		for (DiscreteRegion region : this.m_leftNeighbors) {
			assets.addAll(((ArchetypeMapNode) region.getProperty("Archetypes")).getAllAssets());
		}
		for (DiscreteRegion region : this.m_rightNeighbors) {
			assets.addAll(((ArchetypeMapNode) region.getProperty("Archetypes")).getAllAssets());
		}
		if (this.m_leftNode != null) {
			assets.addAll(this.m_leftNode.getAllAssets());
		}
		if (this.m_rightNode != null) {
			assets.addAll(this.m_rightNode.getAllAssets());
		}
		return assets;
	}

	public synchronized Set<DiscreteRegion> getPotentialList(DiscreteRegion region) {
		assert Debugger.openNode("BSP Potential List Creations", "Retrieving Potentially-Intersecting List");
		assert Debugger.addSnapNode("Testing Region", region);
		assert Debugger.addSnapNode("Current node (" + this.m_pointA + ", " + this.m_pointB + ")", this);
		RiffPolygonToolbox.optimizePolygon(region);
		PointSideStruct struct = RiffPolygonToolbox.getPointSideList(region, this.m_pointA, this.m_pointB);
		if (struct.isStraddling()) {
			assert Debugger.addNode("Region is straddling this line, returning full list.");
			Set<DiscreteRegion> polys = new HashSet<DiscreteRegion>();
			polys.addAll(this.getRegionList());
			assert Debugger.closeNode();
			return polys;
		} else if (struct.isLessThan()) {
			assert Debugger.addNode("Region is less than this line.");
			if (this.m_leftNode != null) {
				assert Debugger.openNode("Deferring to left node");
				Set<DiscreteRegion> returnList = this.m_leftNode.getPotentialList(region);
				assert Debugger.closeNode();
				assert Debugger.closeNode();
				return returnList;
			}
			assert Debugger.closeNode("Left node is null, so returning left neighbors.", this.m_leftNeighbors);
			return this.m_leftNeighbors;
		} else if (struct.isGreaterThan()) {
			assert Debugger.addNode("Region is greater than this line.");
			if (this.m_rightNode != null) {
				assert Debugger.openNode("Deferring to right node");
				Set<DiscreteRegion> returnList = this.m_rightNode.getPotentialList(region);
				assert Debugger.closeNode();
				assert Debugger.closeNode();
				return returnList;
			}
			assert Debugger.closeNode("Right node is null, so returning right neighbors.", this.m_rightNeighbors);
			return this.m_rightNeighbors;
		}
		throw new Exception_InternalError("Defaulted in getPotentialList in DiscreteRegionBSPNode");
	}

	public DiscreteRegion getRegion(Point point) {
		Set set = this.getRegions(point);
		if (set.size() > 1) {
			throw new Exception_InternalError("More than one polygon found for supposedly single-polygon query (" + point + ")");
		} else if (set.size() == 0) {
			assert false;
			throw new Exception_InternalError("No polygon found at location (" + point + ")");
		}
		return (DiscreteRegion) set.iterator().next();
	}

	public Set<DiscreteRegion> getRegionList() {
		Set<DiscreteRegion> list = new HashSet<DiscreteRegion>();
		list.addAll(this.m_leftNeighbors);
		list.addAll(this.m_rightNeighbors);
		if (this.m_leftNode != null) {
			list.addAll(this.m_leftNode.getRegionList());
		}
		if (this.m_rightNode != null) {
			list.addAll(this.m_rightNode.getRegionList());
		}
		return list;
	}

	public Set<DiscreteRegion> getRegions(Point point) {
		assert Debugger.openNode("BSP Polygon Retrievals", "Finding polygon by point: " + point);
		double value = RiffPolygonToolbox.testPointAgainstLine(point, this.m_pointA, this.m_pointB);
		assert Debugger.addNode("Point-side test result: " + value);
		Set<DiscreteRegion> polyList = new HashSet<DiscreteRegion>();
		if (RiffToolbox.isGreaterThan(value, 0.0d)) {
			assert Debugger.addNode("Value is greater than zero.");
			if (this.m_rightNode != null) {
				assert Debugger.openNode("Deferring to right node");
				Set<DiscreteRegion> set = this.m_rightNode.getRegions(point);
				assert Debugger.closeNode();
				assert Debugger.closeNode("Returning region set (" + set.size() + " region(s))", set);
				return set;
			} else {
				assert Debugger.addSnapNode("Adding all right neighbors.", this.m_rightNeighbors);
				polyList.addAll(this.m_rightNeighbors);
			}
		}
		if (RiffToolbox.isLessThan(value, 0.0d)) {
			assert Debugger.addNode("Value is less than zero.");
			if (this.m_leftNode != null) {
				assert Debugger.openNode("Deferring to left node");
				Set<DiscreteRegion> set = this.m_leftNode.getRegions(point);
				assert Debugger.closeNode();
				assert Debugger.closeNode("Returning region set (" + set.size() + " region(s))", set);
				return set;
			} else {
				assert Debugger.addSnapNode("Adding all left neighbors.", this.m_leftNeighbors);
				polyList.addAll(this.m_leftNeighbors);
			}
		}
		if (RiffToolbox.areEqual(Point.System.EUCLIDEAN, value, 0.0d)) {
			assert Debugger.addNode("Value is equal to zero, adding both lists.");
			polyList.addAll(this.m_leftNeighbors);
			polyList.addAll(this.m_rightNeighbors);
		}
		assert Debugger.closeNode("Returning region set (" + polyList.size() + " region(s))", polyList);
		return polyList;
	}

	public synchronized Set<DiscreteRegion> getTempList() {
		return this.m_tempList;
	}

	@Override
	public boolean nodificate() {
		assert Debugger.openNode("BSP Tree Node (" + this.m_pointA + ", " + this.m_pointB + ")");
		assert Debugger.addSnapNode("Left Neighbors", this.m_leftNeighbors);
		assert Debugger.addSnapNode("Right Neighbors", this.m_rightNeighbors);
		if (this.m_leftNode == null) {
			assert Debugger.addNode("Left node: null");
		} else {
			assert Debugger.addSnapNode("Left node", this.m_leftNode);
		}
		if (this.m_rightNode == null) {
			assert Debugger.addNode("Right node: null");
		} else {
			assert Debugger.addSnapNode("Right node", this.m_rightNode);
		}
		assert Debugger.closeNode();
		return true;
	}

	public synchronized void removeFromTempList(DiscreteRegion region) {
		assert Debugger.addSnapNode("Temporary Region List Removals", "Removing region from temporary region list", region);
		this.m_tempList.remove(region);
	}

	public synchronized void removeRegion(DiscreteRegion region) {
		assert Debugger.openNode("BSP Region Removals", "Removing region from BSP tree");
		assert Debugger.addNode(region);
		assert Debugger.addSnapNode("Current node (" + this.m_pointA + ", " + this.m_pointB + ")", this);
		PointSideStruct struct = RiffPolygonToolbox.getPointSideList(region, this.m_pointA, this.m_pointB);
		assert Debugger.addNode(struct);
		if (struct.isLessThan()) {
			assert Debugger.addNode("Region is less than this node's line.");
			if (struct.hasIndeterminates()) {
				assert Debugger.addNode("Removing region from left neighbors.");
				this.m_leftNeighbors.remove(region);
				Iterator polys = this.m_rightNeighbors.iterator();
				while (polys.hasNext()) {
					((DiscreteRegion) polys.next()).removeRegionNeighbor(region);
				}
				assert Debugger.addSnapNode("Left neighbors", this.m_leftNeighbors);
			}
			if (this.m_leftNode != null) {
				assert Debugger.openNode("Deferring to left node");
				this.m_leftNode.removeRegion(region);
				assert Debugger.closeNode();
			}
		}
		if (struct.isGreaterThan()) {
			assert Debugger.addNode("Region is greater than this node's line.");
			if (struct.hasIndeterminates()) {
				assert Debugger.addNode("Removing region from right neighbors.");
				this.m_rightNeighbors.remove(region);
				Iterator polys = this.m_leftNeighbors.iterator();
				while (polys.hasNext()) {
					((DiscreteRegion) polys.next()).removeRegionNeighbor(region);
				}
				assert Debugger.addSnapNode("Left neighbors", this.m_rightNeighbors);
			}
			if (this.m_rightNode != null) {
				assert Debugger.openNode("Deferring to right node");
				this.m_rightNode.removeRegion(region);
				assert Debugger.closeNode();
			}
		}
		assert Debugger.closeNode();
	}
}
