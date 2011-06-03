package com.dafrito.rfe.geom;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.dafrito.rfe.ArchetypeMapNode;
import com.dafrito.rfe.Asset;
import com.dafrito.rfe.geom.points.Point;
import com.dafrito.rfe.geom.points.Points;
import com.dafrito.rfe.gui.debug.Debugger;
import com.dafrito.rfe.inspect.Nodeable;
import com.dafrito.rfe.script.exceptions.Exception_InternalError;

public class DiscreteRegionBSPNode implements Nodeable {
	private final Point pointA;
	private final Point pointB;
	private DiscreteRegionBSPNode leftNode, rightNode;
	private final Set<DiscreteRegion> leftNeighbors = new HashSet<DiscreteRegion>();
	private final Set<DiscreteRegion> rightNeighbors = new HashSet<DiscreteRegion>();
	private final Set<DiscreteRegion> tempList = new HashSet<DiscreteRegion>();
	private final DiscreteRegionBSPNode root;

	public DiscreteRegionBSPNode(DiscreteRegion region) {
		this.root = this;
		this.pointA = region.getPoints().get(0);
		this.pointB = region.getPoints().get(1);
		this.addRegion(region);
	}

	public DiscreteRegionBSPNode(DiscreteRegionBSPNode root, Point pointA, Point pointB) {
		this.root = root;
		this.pointA = pointA;
		this.pointB = pointB;
	}

	public synchronized void addLine(DiscreteRegion owner, Point pointA, Point pointB) {
		assert Debugger.openNode("BSP Line Additions", "Adding line to BSP tree (" + pointA + ", " + pointB + ")");
		assert Debugger.addSnapNode("Current node (" + this.pointA + ", " + this.pointB + ")", this);
		PointSideStruct struct = Polygons.getPointSideList(this.pointA, this.pointB, pointA, pointB);
		assert Debugger.addNode(struct);
		if (Polygons.testForColinearity(pointA, pointB, this.pointA, this.pointB) || struct.isColinear()) {
			assert Debugger.closeNode();
			return;
		}
		if (struct.isLessThan()) {
			assert Debugger.addNode("Line is less than this node's line.");
			if (this.leftNode != null) {
				assert Debugger.openNode("Deferring to left node");
				this.leftNode.addLine(owner, pointA, pointB);
				assert Debugger.closeNode();
				assert Debugger.closeNode();
				return;
			}
			this.leftNode = new DiscreteRegionBSPNode(this.root, pointA, pointB);
			assert Debugger.addNode("Creating new left node.", this.leftNode);
			this.leftNode.categorizeRegion(owner);
		} else if (struct.isGreaterThan()) {
			assert Debugger.addNode("Line is greater than this node's line.");
			if (this.rightNode != null) {
				assert Debugger.openNode("Deferring to right node");
				this.rightNode.addLine(owner, pointA, pointB);
				assert Debugger.closeNode();
				assert Debugger.closeNode();
				return;
			}
			this.rightNode = new DiscreteRegionBSPNode(this.root, pointA, pointB);
			assert Debugger.addNode("Creating new right node.", this.rightNode);
			this.rightNode.categorizeRegion(owner);
		}
		assert Debugger.closeNode();
	}

	public synchronized void addRegion(DiscreteRegion region) {
		assert Debugger.openNode("BSP Region Additions", "Adding region to BSP tree");
		assert Debugger.addNode(region);
		assert Debugger.addSnapNode("Current node (" + this.pointA + ", " + this.pointB + ")", this);
		Polygons.optimizePolygon(region);
		PointSideStruct struct = Polygons.getPointSideList(region, this.pointA, this.pointB);
		if (struct.isStraddling()) {
			assert Debugger.addNode("Region is straddling this node's line, splitting.");
			this.root.removeRegion(region);
			DiscreteRegion splitPolygon = Polygons.splitPolygonUsingEdge(region, this.pointA, this.pointB, true);
			if (splitPolygon == null) {
				assert Debugger.addNode("Unexpected null region from split.");
				assert Debugger.addNode(struct);
			}
			this.root.addRegion(region);
			this.root.addRegion(splitPolygon);
			assert Debugger.closeNode();
			return;
		}
		if (struct.isLessThan()) {
			assert Debugger.addNode("Region is less than this node's line.");
			if (struct.hasIndeterminates()) {
				assert Debugger.addNode("Region has points that are colinear with this node's line, so adding it to left neighbors.");
				this.leftNeighbors.add(region);
				region.addRegionNeighbors(this.rightNeighbors);
			}
			if (this.leftNode != null) {
				assert Debugger.openNode("Deferring to left node");
				this.leftNode.addRegion(region);
				assert Debugger.closeNode();
				assert Debugger.closeNode();
				return;
			}
			List<Point> pointList = region.getPoints();
			for (int i = 0; i < pointList.size(); i++) {
				this.addLine(region, pointList.get(i), pointList.get((i + 1) % pointList.size()));
			}
		} else if (struct.isGreaterThan()) {
			assert Debugger.addNode("Region is greater than this node's line.");
			if (struct.hasIndeterminates()) {
				assert Debugger.addNode("Region has points that are colinear with this node's line, so adding it to right neighbors.");
				this.rightNeighbors.add(region);
				region.addRegionNeighbors(this.leftNeighbors);
			}
			if (this.rightNode != null) {
				assert Debugger.openNode("Deferring to right node");
				this.rightNode.addRegion(region);
				assert Debugger.closeNode();
				assert Debugger.closeNode();
				return;
			}
			List<Point> pointList = region.getPoints();
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
		this.tempList.addAll(regions);
	}

	public synchronized void addToTempList(DiscreteRegion region) {
		assert Debugger.addSnapNode("Temporary Region List Additions", "Adding region to temporary region list", region);
		this.tempList.add(region);
	}

	public synchronized void categorizeRegion(DiscreteRegion region) {
		assert Debugger.openNode("Region Categorizations", "Categorizing Region");
		assert Debugger.addNode(region);
		assert Debugger.addNode(this);
		Polygons.optimizePolygon(region);
		PointSideStruct struct = Polygons.getPointSideList(region, this.pointA, this.pointB);
		assert Debugger.addNode(struct);
		if (struct.isLessThan() && struct.hasIndeterminates()) {
			assert Debugger.addNode("Region has points which are less than or equal to this node's line, adding to left neighbors.");
			this.leftNeighbors.add(region);
			region.addRegionNeighbors(this.rightNeighbors);
		}
		if (struct.isGreaterThan() && struct.hasIndeterminates()) {
			assert Debugger.addNode("Region has points which are greater than or equal to this node's line, adding to right neighbors.");
			this.rightNeighbors.add(region);
			region.addRegionNeighbors(this.leftNeighbors);
		}
		assert Debugger.closeNode();
	}

	public synchronized void clearTempList() {
		assert Debugger.addNode("Clearing temporary region list");
		this.tempList.clear();
	}

	public synchronized List<Asset> getAllAssets() {
		List<Asset> assets = new LinkedList<Asset>();
		for (DiscreteRegion region : this.leftNeighbors) {
			assets.addAll(((ArchetypeMapNode) region.getProperty("Archetypes")).getAllAssets());
		}
		for (DiscreteRegion region : this.rightNeighbors) {
			assets.addAll(((ArchetypeMapNode) region.getProperty("Archetypes")).getAllAssets());
		}
		if (this.leftNode != null) {
			assets.addAll(this.leftNode.getAllAssets());
		}
		if (this.rightNode != null) {
			assets.addAll(this.rightNode.getAllAssets());
		}
		return assets;
	}

	public synchronized Set<DiscreteRegion> getPotentialList(DiscreteRegion region) {
		assert Debugger.openNode("BSP Potential List Creations", "Retrieving Potentially-Intersecting List");
		assert Debugger.addSnapNode("Testing Region", region);
		assert Debugger.addSnapNode("Current node (" + this.pointA + ", " + this.pointB + ")", this);
		Polygons.optimizePolygon(region);
		PointSideStruct struct = Polygons.getPointSideList(region, this.pointA, this.pointB);
		if (struct.isStraddling()) {
			assert Debugger.addNode("Region is straddling this line, returning full list.");
			Set<DiscreteRegion> polys = new HashSet<DiscreteRegion>();
			polys.addAll(this.getRegionList());
			assert Debugger.closeNode();
			return polys;
		} else if (struct.isLessThan()) {
			assert Debugger.addNode("Region is less than this line.");
			if (this.leftNode != null) {
				assert Debugger.openNode("Deferring to left node");
				Set<DiscreteRegion> returnList = this.leftNode.getPotentialList(region);
				assert Debugger.closeNode();
				assert Debugger.closeNode();
				return returnList;
			}
			assert Debugger.closeNode("Left node is null, so returning left neighbors.", this.leftNeighbors);
			return this.leftNeighbors;
		} else if (struct.isGreaterThan()) {
			assert Debugger.addNode("Region is greater than this line.");
			if (this.rightNode != null) {
				assert Debugger.openNode("Deferring to right node");
				Set<DiscreteRegion> returnList = this.rightNode.getPotentialList(region);
				assert Debugger.closeNode();
				assert Debugger.closeNode();
				return returnList;
			}
			assert Debugger.closeNode("Right node is null, so returning right neighbors.", this.rightNeighbors);
			return this.rightNeighbors;
		}
		throw new Exception_InternalError("Defaulted in getPotentialList in DiscreteRegionBSPNode");
	}

	public DiscreteRegion getRegion(Point point) {
		Set<DiscreteRegion> set = this.getRegions(point);
		if (set.size() > 1) {
			throw new Exception_InternalError("More than one polygon found for supposedly single-polygon query (" + point + ")");
		} else if (set.size() == 0) {
			assert false;
			throw new Exception_InternalError("No polygon found at location (" + point + ")");
		}
		return set.iterator().next();
	}

	public Set<DiscreteRegion> getRegionList() {
		Set<DiscreteRegion> list = new HashSet<DiscreteRegion>();
		list.addAll(this.leftNeighbors);
		list.addAll(this.rightNeighbors);
		if (this.leftNode != null) {
			list.addAll(this.leftNode.getRegionList());
		}
		if (this.rightNode != null) {
			list.addAll(this.rightNode.getRegionList());
		}
		return list;
	}

	public Set<DiscreteRegion> getRegions(Point point) {
		assert Debugger.openNode("BSP Polygon Retrievals", "Finding polygon by point: " + point);
		double value = Polygons.testPointAgainstLine(point, this.pointA, this.pointB);
		assert Debugger.addNode("Point-side test result: " + value);
		Set<DiscreteRegion> polyList = new HashSet<DiscreteRegion>();
		if (Points.isGreaterThan(value, 0.0d)) {
			assert Debugger.addNode("Value is greater than zero.");
			if (this.rightNode != null) {
				assert Debugger.openNode("Deferring to right node");
				Set<DiscreteRegion> set = this.rightNode.getRegions(point);
				assert Debugger.closeNode();
				assert Debugger.closeNode("Returning region set (" + set.size() + " region(s))", set);
				return set;
			} else {
				assert Debugger.addSnapNode("Adding all right neighbors.", this.rightNeighbors);
				polyList.addAll(this.rightNeighbors);
			}
		}
		if (Points.isLessThan(value, 0.0d)) {
			assert Debugger.addNode("Value is less than zero.");
			if (this.leftNode != null) {
				assert Debugger.openNode("Deferring to left node");
				Set<DiscreteRegion> set = this.leftNode.getRegions(point);
				assert Debugger.closeNode();
				assert Debugger.closeNode("Returning region set (" + set.size() + " region(s))", set);
				return set;
			} else {
				assert Debugger.addSnapNode("Adding all left neighbors.", this.leftNeighbors);
				polyList.addAll(this.leftNeighbors);
			}
		}
		if (Points.areEqual(Point.System.EUCLIDEAN, value, 0.0d)) {
			assert Debugger.addNode("Value is equal to zero, adding both lists.");
			polyList.addAll(this.leftNeighbors);
			polyList.addAll(this.rightNeighbors);
		}
		assert Debugger.closeNode("Returning region set (" + polyList.size() + " region(s))", polyList);
		return polyList;
	}

	public synchronized Set<DiscreteRegion> getTempList() {
		return this.tempList;
	}

	@Override
	public void nodificate() {
		assert Debugger.openNode("BSP Tree Node (" + this.pointA + ", " + this.pointB + ")");
		assert Debugger.addSnapNode("Left Neighbors", this.leftNeighbors);
		assert Debugger.addSnapNode("Right Neighbors", this.rightNeighbors);
		if (this.leftNode == null) {
			assert Debugger.addNode("Left node: null");
		} else {
			assert Debugger.addSnapNode("Left node", this.leftNode);
		}
		if (this.rightNode == null) {
			assert Debugger.addNode("Right node: null");
		} else {
			assert Debugger.addSnapNode("Right node", this.rightNode);
		}
		assert Debugger.closeNode();
	}

	public synchronized void removeFromTempList(DiscreteRegion region) {
		assert Debugger.addSnapNode("Temporary Region List Removals", "Removing region from temporary region list", region);
		this.tempList.remove(region);
	}

	public synchronized void removeRegion(DiscreteRegion region) {
		assert Debugger.openNode("BSP Region Removals", "Removing region from BSP tree");
		assert Debugger.addNode(region);
		assert Debugger.addSnapNode("Current node (" + this.pointA + ", " + this.pointB + ")", this);
		PointSideStruct struct = Polygons.getPointSideList(region, this.pointA, this.pointB);
		assert Debugger.addNode(struct);
		if (struct.isLessThan()) {
			assert Debugger.addNode("Region is less than this node's line.");
			if (struct.hasIndeterminates()) {
				assert Debugger.addNode("Removing region from left neighbors.");
				this.leftNeighbors.remove(region);
				Iterator<DiscreteRegion> polys = this.rightNeighbors.iterator();
				while (polys.hasNext()) {
					(polys.next()).removeRegionNeighbor(region);
				}
				assert Debugger.addSnapNode("Left neighbors", this.leftNeighbors);
			}
			if (this.leftNode != null) {
				assert Debugger.openNode("Deferring to left node");
				this.leftNode.removeRegion(region);
				assert Debugger.closeNode();
			}
		}
		if (struct.isGreaterThan()) {
			assert Debugger.addNode("Region is greater than this node's line.");
			if (struct.hasIndeterminates()) {
				assert Debugger.addNode("Removing region from right neighbors.");
				this.rightNeighbors.remove(region);
				Iterator<DiscreteRegion> polys = this.leftNeighbors.iterator();
				while (polys.hasNext()) {
					(polys.next()).removeRegionNeighbor(region);
				}
				assert Debugger.addSnapNode("Left neighbors", this.rightNeighbors);
			}
			if (this.rightNode != null) {
				assert Debugger.openNode("Deferring to right node");
				this.rightNode.removeRegion(region);
				assert Debugger.closeNode();
			}
		}
		assert Debugger.closeNode();
	}
}
