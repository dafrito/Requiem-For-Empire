package com.dafrito.geom;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Exception_InternalError;
import com.dafrito.debug.Nodeable;
import com.dafrito.economy.ArchetypeMapNode;
import com.dafrito.economy.Asset;
import com.dafrito.util.RiffToolbox;

public class DiscreteRegionBSPNode implements Nodeable {
    private Point pointA, pointB;
    private DiscreteRegionBSPNode leftNode, rightNode;
    private Set<DiscreteRegion> leftNeighbors;
    private Set<DiscreteRegion> rightNeighbors;
    private Set<DiscreteRegion> tempList;
    private DiscreteRegionBSPNode root;

    public DiscreteRegionBSPNode(DiscreteRegionBSPNode root, Object a, Object b) {
        this(root, (Point)a, (Point)b);
    }

    public DiscreteRegionBSPNode(DiscreteRegion region) {
        this(null, region.getPoints().get(0), region.getPoints().get(1));
        this.root = this;
        addRegion(region);
    }

    public DiscreteRegionBSPNode(DiscreteRegionBSPNode root, Point pointA, Point pointB) {
        this.root = root;
        this.pointA = pointA;
        this.pointB = pointB;
        this.leftNeighbors = new HashSet<DiscreteRegion>();
        this.rightNeighbors = new HashSet<DiscreteRegion>();
        this.tempList = new HashSet<DiscreteRegion>();
    }

    public synchronized List<Asset> getAllAssets() {
        List<Asset> assets = new LinkedList<Asset>();
        for(DiscreteRegion region : this.leftNeighbors) {
            assets.addAll(((ArchetypeMapNode)region.getProperty("Archetypes")).getAllAssets());
        }
        for(DiscreteRegion region : this.rightNeighbors) {
            assets.addAll(((ArchetypeMapNode)region.getProperty("Archetypes")).getAllAssets());
        }
        if(this.leftNode != null) {
            assets.addAll(this.leftNode.getAllAssets());
        }
        if(this.rightNode != null) {
            assets.addAll(this.rightNode.getAllAssets());
        }
        return assets;
    }

    public synchronized void addToTempList(Collection<DiscreteRegion> regions) {
        assert LegacyDebugger.addSnapNode(
            "Temporary Region List Additions",
            "Adding regions to temporary region list",
            regions);
        this.tempList.addAll(regions);
    }

    public synchronized Set<DiscreteRegion> getTempList() {
        return this.tempList;
    }

    public synchronized void addToTempList(DiscreteRegion region) {
        assert LegacyDebugger.addSnapNode("Temporary Region List Additions", "Adding region to temporary region list", region);
        this.tempList.add(region);
    }

    public synchronized void clearTempList() {
        assert LegacyDebugger.addNode("Clearing temporary region list");
        this.tempList.clear();
    }

    public synchronized void removeFromTempList(DiscreteRegion region) {
        assert LegacyDebugger.addSnapNode(
            "Temporary Region List Removals",
            "Removing region from temporary region list",
            region);
        this.tempList.remove(region);
    }

    public synchronized Set<DiscreteRegion> getPotentialList(DiscreteRegion region) {
        assert LegacyDebugger.open("BSP Potential List Creations", "Retrieving Potentially-Intersecting List");
        assert LegacyDebugger.addSnapNode("Testing Region", region);
        assert LegacyDebugger.addSnapNode("Current node (" + this.pointA + ", " + this.pointB + ")", this);
        RiffPolygonToolbox.optimizePolygon(region);
        PointSideStruct struct = RiffPolygonToolbox.getPointSideList(region, this.pointA, this.pointB);
        if(struct.isStraddling()) {
            assert LegacyDebugger.addNode("Region is straddling this line, returning full list.");
            Set<DiscreteRegion> polys = new HashSet<DiscreteRegion>();
            polys.addAll(getRegionList());
            assert LegacyDebugger.close();
            return polys;
        } else if(struct.isLessThan()) {
            assert LegacyDebugger.addNode("Region is less than this line.");
            if(this.leftNode != null) {
                assert LegacyDebugger.open("Deferring to left node");
                Set<DiscreteRegion> returnList = this.leftNode.getPotentialList(region);
                assert LegacyDebugger.close();
                assert LegacyDebugger.close();
                return returnList;
            }
            assert LegacyDebugger.close("Left node is null, so returning left neighbors.", this.leftNeighbors);
            return this.leftNeighbors;
        } else if(struct.isGreaterThan()) {
            assert LegacyDebugger.addNode("Region is greater than this line.");
            if(this.rightNode != null) {
                assert LegacyDebugger.open("Deferring to right node");
                Set<DiscreteRegion> returnList = this.rightNode.getPotentialList(region);
                assert LegacyDebugger.close();
                assert LegacyDebugger.close();
                return returnList;
            }
            assert LegacyDebugger.close("Right node is null, so returning right neighbors.", this.rightNeighbors);
            return this.rightNeighbors;
        }
        throw new Exception_InternalError("Defaulted in getPotentialList in DiscreteRegionBSPNode");
    }

    public DiscreteRegion getRegion(Point point) {
        Set<DiscreteRegion> set = this.getRegions(point);
        if(set.size() > 1) {
            throw new Exception_InternalError("More than one polygon found for supposedly single-polygon query ("
                + point
                + ")");
        } else if(set.isEmpty()) {
            assert false;
            throw new Exception_InternalError("No polygon found at location (" + point + ")");
        }
        return set.iterator().next();
    }

    public Set<DiscreteRegion> getRegions(Point point) {
        assert LegacyDebugger.open("BSP Polygon Retrievals", "Finding polygon by point: " + point);
        double value = RiffPolygonToolbox.testPointAgainstLine(point, this.pointA, this.pointB);
        assert LegacyDebugger.addNode("Point-side test result: " + value);
        Set<DiscreteRegion> polyList = new HashSet<DiscreteRegion>();
        if(RiffToolbox.isGreaterThan(value, 0.0d)) {
            assert LegacyDebugger.addNode("Value is greater than zero.");
            if(this.rightNode != null) {
                assert LegacyDebugger.open("Deferring to right node");
                Set<DiscreteRegion> set = this.rightNode.getRegions(point);
                assert LegacyDebugger.close();
                assert LegacyDebugger.close("Returning region set (" + set.size() + " region(s))", set);
                return set;
            }
            assert LegacyDebugger.addSnapNode("Adding all right neighbors.", this.rightNeighbors);
            polyList.addAll(this.rightNeighbors);
        }
        if(RiffToolbox.isLessThan(value, 0.0d)) {
            assert LegacyDebugger.addNode("Value is less than zero.");
            if(this.leftNode != null) {
                assert LegacyDebugger.open("Deferring to left node");
                Set<DiscreteRegion> set = this.leftNode.getRegions(point);
                assert LegacyDebugger.close();
                assert LegacyDebugger.close("Returning region set (" + set.size() + " region(s))", set);
                return set;
            }
            assert LegacyDebugger.addSnapNode("Adding all left neighbors.", this.leftNeighbors);
            polyList.addAll(this.leftNeighbors);
        }
        if(RiffToolbox.areEqual(Point.System.EUCLIDEAN, value, 0.0d)) {
            assert LegacyDebugger.addNode("Value is equal to zero, adding both lists.");
            polyList.addAll(this.leftNeighbors);
            polyList.addAll(this.rightNeighbors);
        }
        assert LegacyDebugger.close("Returning region set (" + polyList.size() + " region(s))", polyList);
        return polyList;
    }

    public Set<DiscreteRegion> getRegionList() {
        Set<DiscreteRegion> list = new HashSet<DiscreteRegion>();
        list.addAll(this.leftNeighbors);
        list.addAll(this.rightNeighbors);
        if(this.leftNode != null) {
            list.addAll(this.leftNode.getRegionList());
        }
        if(this.rightNode != null) {
            list.addAll(this.rightNode.getRegionList());
        }
        return list;
    }

    public synchronized void addRegions(Collection<DiscreteRegion> regions) {
        for(DiscreteRegion region : regions) {
            addRegion(region);
        }
    }

    public synchronized void addRegion(DiscreteRegion region) {
        assert LegacyDebugger.open("BSP Region Additions", "Adding region to BSP tree");
        assert LegacyDebugger.addNode(region);
        assert LegacyDebugger.addSnapNode("Current node (" + this.pointA + ", " + this.pointB + ")", this);
        RiffPolygonToolbox.optimizePolygon(region);
        PointSideStruct struct = RiffPolygonToolbox.getPointSideList(region, this.pointA, this.pointB);
        if(struct.isStraddling()) {
            assert LegacyDebugger.addNode("Region is straddling this node's line, splitting.");
            this.root.removeRegion(region);
            DiscreteRegion splitPolygon = RiffPolygonToolbox.splitPolygonUsingEdge(region, this.pointA, this.pointB, true);
            if(splitPolygon == null) {
                assert LegacyDebugger.addNode("Unexpected null region from split.");
                assert LegacyDebugger.addNode(struct);
            }
            this.root.addRegion(region);
            this.root.addRegion(splitPolygon);
            assert LegacyDebugger.close();
            return;
        }
        if(struct.isLessThan()) {
            assert LegacyDebugger.addNode("Region is less than this node's line.");
            if(struct.hasIndeterminates()) {
                assert LegacyDebugger.addNode("Region has points that are colinear with this node's line, so adding it to left neighbors.");
                this.leftNeighbors.add(region);
                region.addRegionNeighbors(this.rightNeighbors);
            }
            if(this.leftNode != null) {
                assert LegacyDebugger.open("Deferring to left node");
                this.leftNode.addRegion(region);
                assert LegacyDebugger.close();
                assert LegacyDebugger.close();
                return;
            }
            List<Point> pointList = region.getPoints();
            for(int i = 0; i < pointList.size(); i++) {
                addLine(region, pointList.get(i), pointList.get((i + 1) % pointList.size()));
            }
        } else if(struct.isGreaterThan()) {
            assert LegacyDebugger.addNode("Region is greater than this node's line.");
            if(struct.hasIndeterminates()) {
                assert LegacyDebugger.addNode("Region has points that are colinear with this node's line, so adding it to right neighbors.");
                this.rightNeighbors.add(region);
                region.addRegionNeighbors(this.leftNeighbors);
            }
            if(this.rightNode != null) {
                assert LegacyDebugger.open("Deferring to right node");
                this.rightNode.addRegion(region);
                assert LegacyDebugger.close();
                assert LegacyDebugger.close();
                return;
            }
            List<Point> pointList = region.getPoints();
            for(int i = 0; i < pointList.size(); i++) {
                addLine(region, pointList.get(i), pointList.get((i + 1) % pointList.size()));
            }
        }
        assert LegacyDebugger.close();
    }

    public synchronized void removeRegion(DiscreteRegion region) {
        assert LegacyDebugger.open("BSP Region Removals", "Removing region from BSP tree");
        assert LegacyDebugger.addNode(region);
        assert LegacyDebugger.addSnapNode("Current node (" + this.pointA + ", " + this.pointB + ")", this);
        PointSideStruct struct = RiffPolygonToolbox.getPointSideList(region, this.pointA, this.pointB);
        assert LegacyDebugger.addNode(struct);
        if(struct.isLessThan()) {
            assert LegacyDebugger.addNode("Region is less than this node's line.");
            if(struct.hasIndeterminates()) {
                assert LegacyDebugger.addNode("Removing region from left neighbors.");
                this.leftNeighbors.remove(region);
                for(DiscreteRegion neighbor : this.rightNeighbors) {
                    neighbor.removeRegionNeighbor(region);
                }
                assert LegacyDebugger.addSnapNode("Left neighbors", this.leftNeighbors);
            }
            if(this.leftNode != null) {
                assert LegacyDebugger.open("Deferring to left node");
                this.leftNode.removeRegion(region);
                assert LegacyDebugger.close();
            }
        }
        if(struct.isGreaterThan()) {
            assert LegacyDebugger.addNode("Region is greater than this node's line.");
            if(struct.hasIndeterminates()) {
                assert LegacyDebugger.addNode("Removing region from right neighbors.");
                this.rightNeighbors.remove(region);
                for(DiscreteRegion neighbor : this.leftNeighbors) {
                    neighbor.removeRegionNeighbor(region);
                }
                assert LegacyDebugger.addSnapNode("Left neighbors", this.rightNeighbors);
            }
            if(this.rightNode != null) {
                assert LegacyDebugger.open("Deferring to right node");
                this.rightNode.removeRegion(region);
                assert LegacyDebugger.close();
            }
        }
        assert LegacyDebugger.close();
    }

    public synchronized void addLine(DiscreteRegion owner, Point newPointA, Point newPointB) {
        assert LegacyDebugger.open("BSP Line Additions", "Adding line to BSP tree (" + newPointA + ", " + newPointB + ")");
        assert LegacyDebugger.addSnapNode("Current node (" + this.pointA + ", " + this.pointB + ")", this);
        PointSideStruct struct = RiffPolygonToolbox.getPointSideList(this.pointA, this.pointB, newPointA, newPointB);
        assert LegacyDebugger.addNode(struct);
        if(RiffPolygonToolbox.testForColinearity(newPointA, newPointB, this.pointA, this.pointB) || struct.isColinear()) {
            assert LegacyDebugger.close();
            return;
        }
        if(struct.isLessThan()) {
            assert LegacyDebugger.addNode("Line is less than this node's line.");
            if(this.leftNode != null) {
                assert LegacyDebugger.open("Deferring to left node");
                this.leftNode.addLine(owner, newPointA, newPointB);
                assert LegacyDebugger.close();
                assert LegacyDebugger.close();
                return;
            }
            this.leftNode = new DiscreteRegionBSPNode(this.root, newPointA, newPointB);
            assert LegacyDebugger.addNode("Creating new left node.", this.leftNode);
            this.leftNode.categorizeRegion(owner);
        } else if(struct.isGreaterThan()) {
            assert LegacyDebugger.addNode("Line is greater than this node's line.");
            if(this.rightNode != null) {
                assert LegacyDebugger.open("Deferring to right node");
                this.rightNode.addLine(owner, newPointA, newPointB);
                assert LegacyDebugger.close();
                assert LegacyDebugger.close();
                return;
            }
            this.rightNode = new DiscreteRegionBSPNode(this.root, newPointA, newPointB);
            assert LegacyDebugger.addNode("Creating new right node.", this.rightNode);
            this.rightNode.categorizeRegion(owner);
        }
        assert LegacyDebugger.close();
    }

    public synchronized void categorizeRegion(DiscreteRegion region) {
        assert LegacyDebugger.open("Region Categorizations", "Categorizing Region");
        assert LegacyDebugger.addNode(region);
        assert LegacyDebugger.addNode(this);
        RiffPolygonToolbox.optimizePolygon(region);
        PointSideStruct struct = RiffPolygonToolbox.getPointSideList(region, this.pointA, this.pointB);
        assert LegacyDebugger.addNode(struct);
        if(struct.isLessThan() && struct.hasIndeterminates()) {
            assert LegacyDebugger.addNode("Region has points which are less than or equal to this node's line, adding to left neighbors.");
            this.leftNeighbors.add(region);
            region.addRegionNeighbors(this.rightNeighbors);
        }
        if(struct.isGreaterThan() && struct.hasIndeterminates()) {
            assert LegacyDebugger.addNode("Region has points which are greater than or equal to this node's line, adding to right neighbors.");
            this.rightNeighbors.add(region);
            region.addRegionNeighbors(this.leftNeighbors);
        }
        assert LegacyDebugger.close();
    }

    public boolean nodificate() {
        assert LegacyDebugger.open("BSP Tree Node (" + this.pointA + ", " + this.pointB + ")");
        assert LegacyDebugger.addSnapNode("Left Neighbors", this.leftNeighbors);
        assert LegacyDebugger.addSnapNode("Right Neighbors", this.rightNeighbors);
        if(this.leftNode == null) {
            assert LegacyDebugger.addNode("Left node: null");
        } else {
            assert LegacyDebugger.addSnapNode("Left node", this.leftNode);
        }
        if(this.rightNode == null) {
            assert LegacyDebugger.addNode("Right node: null");
        } else {
            assert LegacyDebugger.addSnapNode("Right node", this.rightNode);
        }
        assert LegacyDebugger.close();
        return true;
    }
}
