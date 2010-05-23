package com.dafrito.geom;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Nodeable;
import com.dafrito.util.RiffToolbox;

public class RiffPolygonToolbox {
    // Returns the slope of these lines.
    public static double getSlope(Point pointA, Point pointB) {
        return (pointB.getY() - pointA.getY()) / (pointB.getX() - pointA.getX());
    }

    public static boolean areSlopesEqual(Point pointA, Point pointB, Point testPointA, Point testPointB) {
        if(RiffToolbox.areEqual(pointA, pointB.getX(), pointA.getX())) {
            if(RiffToolbox.areEqual(testPointA, testPointA.getX(), testPointB.getX())) {
                return true;
            }
        }
        return RiffToolbox.areEqual(
            Point.System.EUCLIDEAN,
            Math.abs(RiffPolygonToolbox.getSlope(pointA, pointB)),
            Math.abs(RiffPolygonToolbox.getSlope(testPointA, testPointB)));
    }

    // Tests if a polygon is convex.
    public static boolean isPolygonConvex(DiscreteRegion region) {
        List<Point> pointList = region.getPoints();
        Boolean myBool = null;
        for(int i = 0; i < pointList.size(); i++) {
            Point linePointA = pointList.get(i);
            Point linePointB = pointList.get((i + 1) % pointList.size());
            for(int k = 0; k < pointList.size(); k++) {
                Point testPoint = pointList.get(k);
                // (y - y0) (x1 - x0) - (x - x0) (y1 - y0)
                double value = (testPoint.getY() - linePointA.getY())
                    * (linePointB.getX() - linePointA.getX())
                    - (testPoint.getX() - linePointA.getX())
                    * (linePointB.getY() - linePointA.getY());
                if(myBool == null) {
                    if(value > 0) {
                        myBool = new Boolean(true);
                    } else if(value < 0) {
                        myBool = new Boolean(false);
                    }
                } else {
                    if(value > 0) {
                        if(myBool.booleanValue() == true) {
                            continue;
                        }
                        return false;
                    } else if(value < 0) {
                        if(myBool.booleanValue() == false) {
                            continue;
                        }
                        return false;
                    } else {
                        continue;
                    }
                }
            }
        }
        return true;
    }

    public static boolean testForColinearity(Point pointA, DiscreteRegion region) {
        List<Point> pointList = region.getPoints();
        for(int i = 0; i < pointList.size(); i++) {
            if(pointA.equals(pointList.get(i)) || pointA.equals(pointList.get((i + 1) % pointList.size()))) {
                continue;
            }
            if(RiffToolbox.areEqual(pointA, pointA.getY(), RiffPolygonToolbox.getSlope(
                pointList.get(i),
                pointList.get((i + 1) % pointList.size()))
                * (pointA.getX() - (pointList.get(i)).getX())
                + (pointList.get(i)).getY())) {
                return true;
            }
        }
        return false;
    }

    public static DiscreteRegion clip(DiscreteRegion region, DiscreteRegion clip) {
        assert LegacyDebugger.open("Clipping operations", "Clipping region");
        assert LegacyDebugger.addSnapNode("Clip", clip);
        assert LegacyDebugger.addSnapNode("Unclipped Region", region);
        DiscreteRegion clippedRegion = new DiscreteRegion(region);
        optimizePolygon(clippedRegion);
        optimizePolygon(clip);
        if(!getBoundingRectIntersection(clippedRegion, clip)) {
            assert LegacyDebugger.close("Region is entirely outside the clip,returning null.");
            return null;
        }
        List<Point> points = clip.getPoints();
        for(int i = 0; i < points.size(); i++) {
            PointSideStruct struct = getPointSideList(clippedRegion, points.get(i), points.get((i + 1) % points.size()));
            if(struct.getLeftPoints().size() != 0 && struct.getRightPoints().size() != 0) {
                DiscreteRegion interim = splitPolygonUsingEdge(clippedRegion, points.get(i), points.get((i + 1)
                    % points.size()), true);
                if(interim != null) {
                    assert LegacyDebugger.open("Testing for polygon which is still valid");
                    struct = getPointSideList(clippedRegion, points.get(i), points.get((i + 1) % points.size()));
                    assert LegacyDebugger.close();
                    if(struct.getLeftPoints().size() != 0) {
                        clippedRegion = interim;
                    }
                    assert LegacyDebugger.addSnapNode("Valid polygon", clippedRegion);
                }
            }
        }
        if(!getBoundingRectIntersection(clippedRegion, clip)) {
            assert LegacyDebugger.close("Region is entirely outside the clip,returning null.");
            return null;
        }
        assert LegacyDebugger.close();
        return clippedRegion;
    }

    public static DiscreteRegion splitPolygonUsingEdge(DiscreteRegion otherRegion, Point pointA, Point pointB,
        boolean hyperPlane) {
        assert LegacyDebugger.open("Polygon Plane-Splitting Operations", "Polygon Plane-Splitting");
        assert LegacyDebugger.addNode("Splitting edge: " + pointA + ", " + pointB);
        assert LegacyDebugger.addSnapNode("Region to split", otherRegion);
        assert LegacyDebugger.addNode("Hyperplane: " + hyperPlane);
        List<Point> otherRegionPoints = otherRegion.getPoints();
        List<Point> extendedPointsList = RiffPolygonToolbox.getExtensionPoints(pointA, pointB, otherRegion);
        List<RiffIntersectionPoint> intersectedList = RiffPolygonToolbox.getIntersections(
            extendedPointsList.get(0),
            extendedPointsList.get(1),
            otherRegion);
        if(intersectedList.size() != 2) {
            assert LegacyDebugger.close("No, insufficient, or too many intersections found, returning null.");
            return null;
        }
        if(!hyperPlane
            && !RiffPolygonToolbox.getBoundingRectIntersection(
                pointA,
                pointB,
                intersectedList.get(0).getIntersection(),
                intersectedList.get(1).getIntersection())) {
            assert LegacyDebugger.close("Bounding rect test failed between splitting edge and intersecting points, returning null.");
            return null;
        }
        assert LegacyDebugger.addSnapNode("Valid intersections found, so creating new polygon.", intersectedList);
        DiscreteRegion newRegion = new DiscreteRegion(otherRegion.getEnvironment(), otherRegion.getProperties());
        newRegion.addPoint(intersectedList.get(0).getIntersection());
        for(int q = Math.min(intersectedList.get(0).getLocation(), intersectedList.get(1).getLocation()); q < Math.max(
            intersectedList.get(0).getLocation(),
            intersectedList.get(1).getLocation()); q++) {
            newRegion.addPoint(otherRegionPoints.get(q + 1));
        }
        newRegion.addPoint(intersectedList.get(1).getIntersection());
        assert LegacyDebugger.addSnapNode("New region formed.", newRegion);
        if(intersectedList.get(0).getLocation() < intersectedList.get(1).getLocation()) {
            otherRegion.addPointAt(intersectedList.get(0).getLocation() + 1, intersectedList.get(0).getIntersection());
            otherRegion.addPointAt(intersectedList.get(1).getLocation() + 2, intersectedList.get(1).getIntersection());
        } else {
            otherRegion.addPointAt(intersectedList.get(1).getLocation() + 1, intersectedList.get(0).getIntersection());
            otherRegion.addPointAt(intersectedList.get(0).getLocation() + 2, intersectedList.get(1).getIntersection());
        }
        assert LegacyDebugger.addSnapNode("Old region after intersect-point addition", otherRegion);
        for(int q = Math.min(intersectedList.get(0).getLocation(), intersectedList.get(1).getLocation()); q < Math.max(
            intersectedList.get(0).getLocation(),
            intersectedList.get(1).getLocation()); q++) {
            otherRegion.removePoint((2 + Math.min(
                intersectedList.get(0).getLocation(),
                intersectedList.get(1).getLocation()))
                % otherRegion.getPoints().size());
            assert LegacyDebugger.addSnapNode("Point-Removals", "Current State", otherRegion);
        }
        assert LegacyDebugger.addSnapNode("Old region", otherRegion);
        RiffPolygonToolbox.optimizePolygon(otherRegion);
        RiffPolygonToolbox.optimizePolygon(newRegion);
        assert LegacyDebugger.close();
        return newRegion;
    }

    // Takes a collection of discrete regions and optimizes them one by one,
    // returning the optimized polygon list at the end.
    public static List<DiscreteRegion> optimizePolygons(Collection<DiscreteRegion> list) {
        List<DiscreteRegion> polygons = new LinkedList<DiscreteRegion>();
        for(DiscreteRegion region : list) {
            region = RiffPolygonToolbox.optimizePolygon(region);
            if(region == null) {
                continue;
            }
            polygons.add(region);
        }
        return polygons;
    }

    // Removes overlapping points, points that are not essential, and also
    // confirms that the polygon is at least three points, and invalidate
    // self-intersecting polygons.
    public static DiscreteRegion optimizePolygon(DiscreteRegion region) {
        if(region.isOptimized()) {
            return region;
        }
        assert LegacyDebugger.open("Optimizing Polygons", "Optimizing Polygon");
        assert LegacyDebugger.addNode(region);
        List<Point> pointList = region.getPoints();
        // Fail if not at least a triangle
        if(pointList.size() < 3) {
            assert LegacyDebugger.close("Polygon invalid because it has less than 3 points, returning null.");
            return null;
        }
        RiffPolygonToolbox.assertCCWPolygon(region);
        // Remove all overlapping points.
        for(int i = 0; i < pointList.size(); i++) {
            for(int j = 0; j < pointList.size(); j++) {
                if(i == j) {
                    continue;
                }
                if(pointList.get(i).equals(pointList.get(j))) {
                    assert LegacyDebugger.addNode(
                        "Overlapping Point Removal",
                        "Removing this point because it is redundant: " + pointList.get(j));
                    pointList.remove(j);
                    j--;
                }
            }
        }
        // Remove all unnecessary points.
        for(int i = 0; i < pointList.size(); i++) {
            if(pointList.size() < 3) {
                assert LegacyDebugger.close("Polygon invalid because it has less than 3 points, returning null.");
                return null;
            }
            Point pointA = pointList.get(i);
            for(int j = 1; j < pointList.size(); j++) {
                if(i == j) {
                    continue;
                }
                Point pointB = pointList.get(j);
                double slope = RiffPolygonToolbox.getSlope(pointA, pointB);
                for(int k = 0; k < pointList.size(); k++) {
                    if(k == i || k == j) {
                        continue;
                    }
                    Point pointC = pointList.get(k);
                    double testSlope = RiffPolygonToolbox.getSlope(pointA, pointC);
                    if(testSlope == slope) {
                        double y = slope * (pointC.getX() - pointA.getX()) + pointA.getY();
                        if(y == pointC.getY()) {
                            if(RiffToolbox.getDistance(pointA, pointB) + RiffToolbox.getDistance(pointB, pointC) == RiffToolbox.getDistance(
                                pointA,
                                pointC)) {
                                if(RiffPolygonToolbox.confirmInteriorLine(pointA, pointC, region)) {
                                    assert LegacyDebugger.addNode("Redundant Colinear Point Removals", "Removing this point:"
                                        + pointB);
                                    assert LegacyDebugger.addNode("Redundant Colinear Point Removals", "More optimal line: "
                                        + pointA
                                        + ", "
                                        + pointC);
                                    pointList.remove(j);
                                    j--;
                                }
                                continue;
                            }
                        } else if(RiffToolbox.getDistance(pointB, pointC) + RiffToolbox.getDistance(pointA, pointC) == RiffToolbox.getDistance(
                            pointA,
                            pointB)) {
                            if(RiffPolygonToolbox.confirmInteriorLine(pointA, pointB, region)) {
                                assert LegacyDebugger.addNode("Redundant Colinear Point Removals", "Removing this point:"
                                    + pointC);
                                assert LegacyDebugger.addNode("Redundant Colinear Point Removals", "More optimal line: "
                                    + pointA
                                    + ", "
                                    + pointB);
                                pointList.remove(k);
                                k--;
                            }
                            continue;
                        } else if(RiffToolbox.getDistance(pointB, pointA) + RiffToolbox.getDistance(pointA, pointC) == RiffToolbox.getDistance(
                            pointC,
                            pointB)) {
                            if(RiffPolygonToolbox.confirmInteriorLine(pointC, pointB, region)) {
                                assert LegacyDebugger.addNode("Redundant Colinear Point Removals", "Removing this point:"
                                    + pointA);
                                assert LegacyDebugger.addNode("Redundant Colinear Point Removals", "More optimal line: "
                                    + pointC
                                    + ", "
                                    + pointB);
                                pointList.remove(i);
                                i--;
                            }
                            continue;
                        }
                    }
                }
            }
        }
        // Test for self-intersections
        for(int i = 0; i < pointList.size(); i++) {
            for(int j = 0; j < pointList.size(); j++) {
                if(i == j) {
                    continue;
                }
                IntersectionPoint point = RiffPolygonToolbox.getIntersection(pointList.get(i), pointList.get((i + 1)
                    % pointList.size()), pointList.get(j), pointList.get((j + 1) % pointList.size()));
                if(point == null || point.isTangent()) {
                    continue;
                }
                assert LegacyDebugger.close("Polygon intersects itself and is invalid, returning null.");
                return null;
            }
        }
        // Fail if not at least a triangle
        if(pointList.size() < 3) {
            assert LegacyDebugger.close("Polygon invalid because it has less than 3 points, returning null.");
            return null;
        }
        region.setPointList(pointList);
        region.setOptimized(true);
        assert LegacyDebugger.close("Optimization complete", region);
        return region;
    }

    // Tests how many times a line, using the coord's as the first point, and
    // the extreme right point of the poly, crosses any other border.
    public static int getCrosses(double xCoord, double yCoord, DiscreteRegion region) {
        assert LegacyDebugger.open("Polygon/getCrosses", String.format(
            "Testing for crosses for x-coord: %d and y-coord: %d",
            xCoord,
            yCoord));
        List<Point> pointList = region.getPoints();
        int crosses = 0;
        double xExtremeCoord = Double.NEGATIVE_INFINITY;
        for(Point testPoint : pointList) {
            if(testPoint.getX() > xExtremeCoord) {
                xExtremeCoord = testPoint.getX();
            }
        }
        xExtremeCoord += 1.0f;
        assert LegacyDebugger.addNode(String.format("This line extends to x-coord: %d and y-coord: %d", xExtremeCoord, yCoord));
        Point crossExtremePoint = createPoint(pointList.get(0), "Extreme right-point", xExtremeCoord, yCoord);
        Point crossMidPoint = createPoint(pointList.get(0), "MidPoint", xCoord, yCoord);
        List<Point> overlappedVertices = new LinkedList<Point>();
        for(int i = 0; i < pointList.size(); i++) {
            Point testPointA = pointList.get(i);
            Point testPointB = pointList.get((i + 1) % pointList.size());
            if(testPointA.getY() == yCoord) {
                if(overlappedVertices.contains(testPointA)) {
                    continue;
                }
                overlappedVertices.add(testPointA);
            } else if(testPointB.getY() == yCoord) {
                if(overlappedVertices.contains(testPointB)) {
                    continue;
                }
                overlappedVertices.add(testPointB);
            }
            IntersectionPoint intersect = RiffPolygonToolbox.getIntersection(
                crossMidPoint,
                crossExtremePoint,
                testPointA,
                testPointB);
            if(intersect == null || intersect.isTangent()) {
                continue;
            }
            crosses++;
        }
        assert LegacyDebugger.close("Total Crosses: " + crosses);
        return crosses;
    }

    public static Point createPoint(Point referencePoint, String name, double x, double y) {
        if(referencePoint instanceof Point_Euclidean) {
            return new Point_Euclidean(referencePoint.getEnvironment(), name, x, y, 0.0d);
        }
        return new Point_Spherical(referencePoint.getEnvironment(), name, x, y, 0.0d);
    }

    public static boolean testforRegionPointSideIntersection(DiscreteRegion region, DiscreteRegion otherRegion) {
        assert LegacyDebugger.open(
            "Polygon/testforRegionPointSideIntersection",
            "Testing for point-side intersection between these two regions.");
        assert LegacyDebugger.addNode(region);
        assert LegacyDebugger.addNode(otherRegion);
        List<Point> regionList = region.getPoints();
        assert LegacyDebugger.addNode("Beginning line-by-line point-side check.");
        for(int i = 0; i < regionList.size(); i++) {
            PointSideStruct struct = RiffPolygonToolbox.getPointSideList(
                otherRegion,
                regionList.get(i),
                regionList.get((i + 1) % regionList.size()));
            if(struct.isGreaterThan()) {
                assert LegacyDebugger.addNode("This line guarantees a pass of the point-side test, returning false. "
                    + String.format("Line: %s, %s", regionList.get(i), regionList.get((i + 1) % regionList.size())));
                assert LegacyDebugger.close();
                return false;
            }
        }
        assert LegacyDebugger.close("These polygons may intersect, returning true.");
        return true;
    }

    public static boolean getBoundingRectIntersection(Point pointA, Point pointB, Point pointC, Point pointD) {
        return getBoundingRectIntersection(pointA, pointB, pointC, pointD, true);
    }

    public static boolean getBoundingRectIntersection(Point pointA, Point pointB, Point pointC, Point pointD,
        boolean includeTangents) {
        if(pointA.equals(pointC) && pointB.equals(pointD)) {
            return true;
        }
        if(pointA.equals(pointD) && pointB.equals(pointC)) {
            return true;
        }
        if(RiffToolbox.areEqual(
            Point.System.EUCLIDEAN,
            Math.abs(RiffPolygonToolbox.getSlope(pointA, pointB)),
            Double.POSITIVE_INFINITY)
            && RiffToolbox.areEqual(
                Point.System.EUCLIDEAN,
                Math.abs(RiffPolygonToolbox.getSlope(pointC, pointD)),
                Double.POSITIVE_INFINITY)) {
            if(RiffToolbox.areEqual(pointA, pointA.getX(), pointC.getX())) {
                return false;
            }
        } else {
            if(RiffToolbox.isLessThan(Math.max(pointA.getX(), pointB.getX()), Math.min(pointC.getX(), pointD.getX()))) {
                return false;
            }
            if(RiffToolbox.areEqual(pointA, Math.max(pointA.getX(), pointB.getX()), Math.min(
                pointC.getX(),
                pointD.getX()))
                && !includeTangents) {
                return false;
            }
            if(RiffToolbox.isGreaterThan(Math.min(pointA.getX(), pointB.getX()), Math.max(pointC.getX(), pointD.getX()))) {
                return false;
            }
            if(RiffToolbox.areEqual(pointA, Math.min(pointA.getX(), pointB.getX()), Math.max(
                pointC.getX(),
                pointD.getX()))
                && !includeTangents) {
                return false;
            }
        }
        if(RiffToolbox.areEqual(pointA, RiffPolygonToolbox.getSlope(pointA, pointB), 0.0d)
            && RiffToolbox.areEqual(Point.System.EUCLIDEAN, RiffPolygonToolbox.getSlope(pointC, pointD), 0.0d)) {
            if(!RiffToolbox.areEqual(pointA, pointA.getY(), pointC.getY())) {
                return false;
            }
        } else {
            if(RiffToolbox.isLessThan(Math.max(pointA.getY(), pointB.getY()), Math.min(pointC.getY(), pointD.getY()))) {
                return false;
            }
            if(RiffToolbox.areEqual(pointA, Math.max(pointA.getY(), pointB.getY()), Math.min(
                pointC.getY(),
                pointD.getY()))
                && !includeTangents) {
                return false;
            }
            if(RiffToolbox.isGreaterThan(Math.min(pointA.getY(), pointB.getY()), Math.max(pointC.getY(), pointD.getY()))) {
                return false;
            }
            if(RiffToolbox.areEqual(pointA, Math.min(pointA.getY(), pointB.getY()), Math.max(
                pointC.getY(),
                pointD.getY()))
                && !includeTangents) {
                return false;
            }
        }
        return true;
    }

    public static void snapVertices(List<DiscreteRegion> polygons, DiscreteRegion region) {
        assert LegacyDebugger.open("Polygon/snapVertices", "Testing for snappable vertices.");
        assert LegacyDebugger.log("Region to be snapped", region);
        List<Point> regionPoints = region.getPoints();
        for(int i = 0; i < polygons.size(); i++) {
            List<Point> pointList = polygons.get(i).getPoints();
            for(int k = 0; k < pointList.size(); k++) {
                for(int l = 0; l < regionPoints.size(); l++) {
                    if(pointList.get(k).equals(regionPoints.get(l))) {
                        assert LegacyDebugger.addNode("Equality found."
                            + String.format(
                                "Correct vertex: %s, vertex to be snapped: %s",
                                pointList.get(k),
                                regionPoints.get(l)));
                        regionPoints.get(l).setPosition(pointList.get(k));
                    }
                }
            }
        }
        assert LegacyDebugger.close();
    }

    // Removes overlapping polygons and creates new ones with the
    // characteristics of the previous two merged together.
    public static DiscreteRegionBSPNode removeOverlappingPolygons(DiscreteRegionBSPNode root, DiscreteRegion region,
        boolean recurse) {
        assert LegacyDebugger.open("Overlapping Polygon Removals", "Removing Overlapping Polygons");
        if(region == null || root == null) {
            assert LegacyDebugger.close("Region or root are null, returning root.");
            return root;
        }
        assert LegacyDebugger.addNode(region);
        Set<DiscreteRegion> potentialList = root.getPotentialList(region);
        // RiffPolygonToolbox.snapVertices(potentialList, region);
        if(potentialList == null || potentialList.size() == 0) {
            assert LegacyDebugger.addNode("Potential intersection list from BSP tree is null or zero-size, adding region to tree.");
            root.addRegion(region);
            root.removeFromTempList(region);
            assert LegacyDebugger.close();
            return root;
        }
        List<Point> regionPoints = region.getPoints();
        Iterator<DiscreteRegion> iter = potentialList.iterator();
        assert LegacyDebugger.open("Beginning overlap-check sequence...");
        while (iter.hasNext()) {
            DiscreteRegion otherRegion = iter.next();
            assert LegacyDebugger.addSnapNode("Compared region", otherRegion);
            if(otherRegion != region && otherRegion.equals(region)) {
                assert LegacyDebugger.addNode("Regions are equal, adding this regions assets to that and returning root.");
                otherRegion.setProperty("Archetypes", region.getProperty("Archetypes"));
                assert LegacyDebugger.close();
                assert LegacyDebugger.close();
                return root;
            }
            List<Point> otherRegionPoints = otherRegion.getPoints();
            if(region.checkClearedRegionMap(otherRegion)) {
                assert LegacyDebugger.addNode("Cleared-region check returned true.");
                potentialList.remove(otherRegion);
                iter = potentialList.iterator();
                continue;
            }
            if(!RiffPolygonToolbox.getBoundingRectIntersection(region, otherRegion)) {
                assert LegacyDebugger.addNode("Polygons do not intersect with their bounding rects.");
                region.addRegionToMap(otherRegion);
                otherRegion.addRegionToMap(region);
                potentialList.remove(otherRegion);
                iter = potentialList.iterator();
                continue;
            }
            if(!RiffPolygonToolbox.testforRegionPointSideIntersection(region, otherRegion)
                && !RiffPolygonToolbox.testforRegionPointSideIntersection(otherRegion, region)) {
                assert LegacyDebugger.addNode("Polygons do not intersect according to the point-side polygon test.");
                region.addRegionToMap(otherRegion);
                otherRegion.addRegionToMap(region);
                potentialList.remove(otherRegion);
                iter = potentialList.iterator();
                continue;
            }
            assert LegacyDebugger.open("Beginning primary line-by-line overlap-check sequence.");
            for(int k = 0; k < regionPoints.size(); k++) {
                assert LegacyDebugger.addNode("Now testing using this potentially intersecting line: "
                    + regionPoints.get(k)
                    + ", "
                    + regionPoints.get((k + 1) % regionPoints.size()));
                if(!RiffPolygonToolbox.getBoundingRectIntersection(regionPoints.get(k), regionPoints.get((k + 1)
                    % regionPoints.size()), otherRegion)) {
                    assert LegacyDebugger.addNode("The current line's bounding rect does not overlap the other region's.");
                    continue;
                }
                DiscreteRegion splittingRegion = new DiscreteRegion(otherRegion);
                DiscreteRegion newRegion = RiffPolygonToolbox.splitPolygonUsingEdge(
                    splittingRegion,
                    regionPoints.get(k),
                    regionPoints.get((k + 1) % regionPoints.size()),
                    false);
                if(newRegion == null) {
                    assert LegacyDebugger.addNode("New-region is null, continuing...");
                    continue;
                }
                assert LegacyDebugger.open("Split-test confirmed and new region created.");
                assert LegacyDebugger.addSnapNode("New region", newRegion);
                assert LegacyDebugger.addSnapNode("Other region", splittingRegion);
                assert LegacyDebugger.addSnapNode("Original region", otherRegion);
                assert LegacyDebugger.close();
                root.removeRegion(otherRegion);
                if(recurse) {
                    assert LegacyDebugger.addNode("Recursing.");
                    DiscreteRegionBSPNode newRoot = root;
                    newRoot = RiffPolygonToolbox.removeOverlappingPolygons(newRoot, splittingRegion, recurse);
                    newRoot = RiffPolygonToolbox.removeOverlappingPolygons(newRoot, newRegion, recurse);
                    newRoot = RiffPolygonToolbox.removeOverlappingPolygons(newRoot, region, recurse);
                    assert LegacyDebugger.close();
                    assert LegacyDebugger.close();
                    assert LegacyDebugger.close();
                    return newRoot;
                }
                root.addToTempList(splittingRegion);
                root.addToTempList(newRegion);
                root.addToTempList(region);
                assert LegacyDebugger.close();
                assert LegacyDebugger.close();
                assert LegacyDebugger.close();
                return root;
            }
            assert LegacyDebugger.close();
            assert LegacyDebugger.open("Beginning counter line-by-line overlap-check sequence.");
            for(int k = 0; k < otherRegionPoints.size(); k++) {
                assert LegacyDebugger.addNode("Now testing using this potentially intersecting line: "
                    + otherRegionPoints.get(k)
                    + ", "
                    + otherRegionPoints.get((k + 1) % otherRegionPoints.size()));
                if(!RiffPolygonToolbox.getBoundingRectIntersection(
                    otherRegionPoints.get(k),
                    otherRegionPoints.get((k + 1) % otherRegionPoints.size()),
                    region)) {
                    assert LegacyDebugger.addNode("The current line's bounding rect does not overlap the other region's.");
                    continue;
                }
                DiscreteRegion oldRegion = new DiscreteRegion(region);
                DiscreteRegion newRegion = RiffPolygonToolbox.splitPolygonUsingEdge(
                    region,
                    otherRegionPoints.get(k),
                    otherRegionPoints.get((k + 1) % otherRegionPoints.size()),
                    false);
                if(newRegion == null) {
                    assert LegacyDebugger.addNode("New-region is null.");
                    continue;
                }
                assert LegacyDebugger.open("Split-test confirmed and new region created.");
                assert LegacyDebugger.addSnapNode("New region", newRegion);
                assert LegacyDebugger.addSnapNode("Other region", oldRegion);
                assert LegacyDebugger.addSnapNode("Original region", otherRegion);
                assert LegacyDebugger.close();
                if(recurse) {
                    assert LegacyDebugger.addNode("Recursing.");
                    DiscreteRegionBSPNode workingRoot = root;
                    workingRoot = RiffPolygonToolbox.removeOverlappingPolygons(workingRoot, newRegion, recurse);
                    workingRoot = RiffPolygonToolbox.removeOverlappingPolygons(workingRoot, region, recurse);
                    assert LegacyDebugger.close();
                    assert LegacyDebugger.close();
                    assert LegacyDebugger.close();
                    return workingRoot;
                }
                root.addToTempList(newRegion);
                root.addToTempList(region);
                assert LegacyDebugger.close();
                assert LegacyDebugger.close();
                assert LegacyDebugger.close();
                return root;
            }
            assert LegacyDebugger.close();
            region.addRegionToMap(otherRegion);
            otherRegion.addRegionToMap(region);
        }
        assert LegacyDebugger.close("Region passed all overlapping tests, clearing from temp-list and adding to BSP tree, then returning.");
        root.removeFromTempList(region);
        root.addRegion(region);
        assert LegacyDebugger.close();
        return root;
    }

    public static boolean getBoundingRectIntersection(Point pointA, Point pointB, DiscreteRegion region) {
        if(RiffToolbox.isLessThan(Math.max(pointA.getX(), pointB.getX()), region.getLeftExtreme())) {
            return false;
        }
        if(RiffToolbox.isGreaterThan(Math.min(pointA.getX(), pointB.getX()), region.getRightExtreme())) {
            return false;
        }
        if(RiffToolbox.isLessThan(Math.max(pointA.getY(), pointB.getY()), region.getBottomExtreme())) {
            return false;
        }
        if(RiffToolbox.isGreaterThan(Math.min(pointA.getY(), pointB.getY()), region.getTopExtreme())) {
            return false;
        }
        return true;
    }

    // Tests for intersections of this line provided by the two points against
    // the lines of the region provided.
    public static List<RiffIntersectionPoint> getIntersections(Point pointA, Point pointB, DiscreteRegion region) {
        assert LegacyDebugger.open("Intersection Tests (Line vs Region)", "Intersection Test: Line against Region");
        assert LegacyDebugger.addNode(region);
        assert LegacyDebugger.addNode("Line: " + pointA + ", " + pointB);
        List<Point> pointList = region.getPoints();
        List<RiffIntersectionPoint> intersectPoints = new LinkedList<RiffIntersectionPoint>();
        if(!RiffPolygonToolbox.getBoundingRectIntersection(pointA, pointB, region)) {
            assert LegacyDebugger.close("Bounding-rect test between the line and the region returned false, returning empty list.");
            return intersectPoints;
        }
        for(int i = 0; i < pointList.size(); i++) {
            IntersectionPoint intersectPoint = RiffPolygonToolbox.getIntersection(
                pointA,
                pointB,
                pointList.get(i),
                pointList.get((i + 1) % pointList.size()));
            if(intersectPoint != null) {
                RiffIntersectionPoint intersect = new RiffIntersectionPoint(intersectPoint.getPoint(), i);
                if(!intersectPoints.contains(intersect)) {
                    intersectPoints.add(intersect);
                }
            }
            /*
             * if(intersectPoint==null){ if(tangentFlag){ assert
             * Debugger.addNode("Resetting tangent flag."); tangentFlag=false; }
             * }else if(intersectPoint.isTangent()){
             * if(intersectPoint.getPoint().equals(pointList.get(0))){ assert
             * Debugger
             * .addNode("Setting tangentFlag to true (firstTangentFlag is true)"
             * ); tangentFlag=true; } if(!tangentFlag){ tangentFlag=true; assert
             * Debugger.addNode("Setting the tangent flag."); }else{
             * if(!intersectPoint
             * .getPoint().equals(pointList.get(0))&&!intersectPoint
             * .getPoint().equals(pointList.get(i))){ assertDebugger.addNode(
             * "firstTangentFlag is unset, but our point isn't equal to the correct point, so continuing."
             * ); continue; } tangentFlag=false; intersectPoints.add(new
             * RiffIntersectionPoint(pointList.get(i),i)); assert
             * Debugger.addSnapNode
             * ("Adding this tangent intersect point",intersectPoints
             * .get(intersectPoints.size()-1)); } }else{ tangentFlag=false;
             * intersectPoints.add(new
             * RiffIntersectionPoint(intersectPoint.getPoint(),i)); assert
             * Debugger
             * .addSnapNode("Adding this intersect point",intersectPoints
             * .get(intersectPoints.size()-1)); }
             */
        }
        assert LegacyDebugger.close("Returning list (" + intersectPoints.size() + " intersection(s))", intersectPoints);
        return intersectPoints;
    }

    // Takes two points and extends their line so that the x-value provided by
    // the extension is at an endpoint of the line.
    public static double getExtensionPoint(Point pointA, Point pointB, double extension) {
        return RiffPolygonToolbox.getSlope(pointA, pointB) * (extension - pointA.getX()) + pointA.getY();
    }

    // Extends a line, provided by the two points, in both directions so that
    // their x coordinates are equal to the left and right double values
    // provided with correct slope.
    public static List<Point> getExtensionPoints(Point pointA, Point pointB, DiscreteRegion region) {
        List<Point> pointList = new LinkedList<Point>();
        assert LegacyDebugger.open("Polygon/getExtensionPoints", String.format(
            "Extending these points: %s, %s",
            pointA,
            pointB));
        assert LegacyDebugger.log("Region to extend to", region);
        double YValue = RiffPolygonToolbox.getExtensionPoint(pointA, pointB, region.getLeftExtreme() - 1.0d);
        double XValue = region.getLeftExtreme() - 1.0d;
        if(RiffToolbox.isGreaterThan(YValue, region.getTopExtreme())) {
            YValue = region.getTopExtreme() + 1.0d;
            XValue = (YValue - pointA.getY()) / RiffPolygonToolbox.getSlope(pointA, pointB) + pointA.getX();
        } else if(RiffToolbox.isLessThan(YValue, region.getBottomExtreme())) {
            YValue = region.getBottomExtreme() - 1.0d;
            XValue = (YValue - pointA.getY()) / RiffPolygonToolbox.getSlope(pointA, pointB) + pointA.getX();
        }
        pointList.add(createPoint(pointA, pointA.getName(), XValue, YValue));
        YValue = RiffPolygonToolbox.getExtensionPoint(pointA, pointB, region.getRightExtreme() + 1.0d);
        XValue = region.getRightExtreme() + 1.0d;
        if(RiffToolbox.isGreaterThan(YValue, region.getTopExtreme())) {
            YValue = region.getTopExtreme() + 1.0d;
            XValue = (YValue - pointA.getY()) / RiffPolygonToolbox.getSlope(pointA, pointB) + pointA.getX();
        } else if(RiffToolbox.isLessThan(YValue, region.getBottomExtreme())) {
            YValue = region.getBottomExtreme() - 1.0d;
            XValue = (YValue - pointA.getY()) / RiffPolygonToolbox.getSlope(pointA, pointB) + pointA.getX();
        }
        pointList.add(createPoint(pointA, pointB.getName(), XValue, YValue));
        assert LegacyDebugger.close(String.format("List of extended points: %s, %s", pointList.get(0), pointList.get(1)));
        return pointList;
    }

    /**
     * This test is used when checking for colinearity. If the result is equal
     * to testPoint's Y-value, they are colinear.
     * 
     * @param pointA Reference line's first point
     * @param pointB Reference line's second point
     * @param testPoint Test point
     * @return The result of this test. TODO: What is the result, exactly?
     */
    public static double pointSlopeTest(Point pointA, Point pointB, Point testPoint) {
        return RiffPolygonToolbox.getSlope(pointA, pointB) * (testPoint.getX() - pointA.getX()) + pointA.getY();
    }

    public static boolean testForColinearity(Point pointA, Point pointB, Point testPointA, Point testPointB) {
        assert LegacyDebugger.open("Polygon/testForColinearity");
        assert LegacyDebugger.format("Testing these points for colinearity: %s, %s", pointA, pointB);
        assert LegacyDebugger.format("Against these points: %s, %s", testPointA, testPointB);
        if((pointA.equals(testPointA) && pointB.equals(testPointB))
            || (pointA.equals(testPointB) && pointB.equals(testPointA))) {
            return true;
        }
        if(!RiffPolygonToolbox.areSlopesEqual(pointA, pointB, testPointA, testPointB)) {
            assert LegacyDebugger.close("Slopes are not equal, so returning false.");
            return false;
        }
        assert LegacyDebugger.log("Slopes are equal, so beginning point-slope test.");
        double firstPointSlopeTestResult = pointSlopeTest(pointA, pointB, testPointA);
        assert LegacyDebugger.format("First point-slope test: %s", firstPointSlopeTestResult);
        assert LegacyDebugger.log("Expected value: " + testPointA.getY());
        if(!RiffToolbox.areEqual(Point.System.EUCLIDEAN, firstPointSlopeTestResult, testPointA.getY())) {
            assert LegacyDebugger.close("First point failed point-slope test, so returning false.");
            return false;
        }

        double secondPointSlopeTestResult = pointSlopeTest(pointA, pointB, testPointB);
        assert LegacyDebugger.format("Second point-slope test: %s", secondPointSlopeTestResult);
        assert LegacyDebugger.log("Expected value: " + testPointB.getY());

        if(!RiffToolbox.areEqual(Point.System.EUCLIDEAN, RiffPolygonToolbox.getSlope(pointA, pointB)
            * (testPointB.getX() - pointA.getX())
            + pointA.getY(), testPointB.getY())) {
            assert LegacyDebugger.close("Second point failed point-slope test, so returning false.");
            return false;
        }
        assert LegacyDebugger.close("They are colinear.");
        return true;
    }

    public static boolean testForColinearity(Point linePointA, Point linePointB, Point testPoint) {
        return RiffToolbox.areEqual(testPoint, testPoint.getY(), (RiffPolygonToolbox.getSlope(linePointA, linePointB)
            * (testPoint.getX() - linePointA.getX()) + linePointA.getY()));
    }

    // Tests if (pointA, pointB) intersects (testPointA, testPointB), and
    // returns the point if it does.
    public static IntersectionPoint getIntersection(Point pointA, Point pointB, Point testPointA, Point testPointB) {
        assert LegacyDebugger.open("Intersection Tests (Line vs Line)", "Intersection Test: Line against Line");
        assert LegacyDebugger.addNode("Control Points: " + pointA + ", " + pointB);
        assert LegacyDebugger.addNode("Test Points: " + testPointA + ", " + testPointB);
        boolean tangentFlag = false;
        // Check for invalid lines.
        if(pointA.equals(pointB)) {
            assert LegacyDebugger.close("First control point is equal to second control point, returning null.");
            return null;
        }
        if(testPointA.equals(testPointB)) {
            assert LegacyDebugger.close("First test point is equal to second test point, returning null.");
            return null;
        }
        if((pointA.equals(testPointB) && pointB.equals(testPointA))
            || (pointA.equals(testPointA) && pointB.equals(testPointB))) {
            assert LegacyDebugger.close("These lines are equal, returning null.");
            return null;
        }
        if(pointA.equals(testPointA) || pointA.equals(testPointB)) {
            IntersectionPoint returning = new IntersectionPoint(pointA, true);
            assert LegacyDebugger.close(
                "First control point is equal to one of the test points, yielding a tangent, so returning that tangent.",
                returning);
            return returning;
        }
        if(pointB.equals(testPointA) || pointB.equals(testPointB)) {
            IntersectionPoint returning = new IntersectionPoint(pointB, true);
            assert LegacyDebugger.close(
                "Second control point is equal to one of the test points, yielding a tangent, so returning that tangent.",
                returning);
            return returning;
        }
        // Test for colinearity of these points.
        if(RiffPolygonToolbox.testForColinearity(pointA, pointB, testPointA, testPointB)) {
            assert LegacyDebugger.close("These lines are colinear, returning null.");
            return null;
        }
        // Get slopes.
        double slope = RiffPolygonToolbox.getSlope(pointA, pointB);
        double testSlope = RiffPolygonToolbox.getSlope(testPointA, testPointB);
        // Test for infinite slopes, and if found, get real intersection points.
        if(Math.abs(slope) == Double.POSITIVE_INFINITY) {
            assert LegacyDebugger.addNode("Slope of the control points is infinite.");
            if(RiffToolbox.isGreaterThan(pointA.getX(), Math.max(testPointA.getX(), testPointB.getX()))) {
                assert LegacyDebugger.close("The X-value is greater than the maximum X-value of the test points, returning null.");
                return null;
            } else if(RiffToolbox.areEqual(pointA, pointA.getX(), Math.max(testPointA.getX(), testPointB.getX()))) {
                assert LegacyDebugger.addNode("The X-value is equal to the maximum X-value of the test points, setting tangent flag.");
                tangentFlag = true;
            }
            if(RiffToolbox.isLessThan(pointA.getX(), Math.min(testPointA.getX(), testPointB.getX()))) {
                assert LegacyDebugger.close("The X-value is less than the minimum X-value of the test points, returning null.");
                return null;
            } else if(RiffToolbox.areEqual(pointA, pointA.getX(), Math.min(testPointA.getX(), testPointB.getX()))) {
                assert LegacyDebugger.addNode("The X-value is equal to the minimum X-value of the test points, setting tangent flag.");
                tangentFlag = true;
            }
            double yIntersection = testSlope * (pointA.getX() - testPointA.getX()) + testPointA.getY();
            if(!RiffToolbox.areEqual(Point.System.EUCLIDEAN, 0.0d, testSlope)) {
                assert LegacyDebugger.addNode("The testSlope is not zero.");
                if(RiffToolbox.isLessThan(yIntersection, Math.min(testPointA.getY(), testPointB.getY()))) {
                    assert LegacyDebugger.close("The Y-intercept is less than the minimum of the Y-values of the testPoints, returning null.");
                    return null;
                } else if(RiffToolbox.areEqual(
                    testPointA,
                    yIntersection,
                    Math.min(testPointA.getY(), testPointB.getY()))) {
                    assert LegacyDebugger.addNode("The Y-intercept is equal to the minimum of the Y-values of the testPoints, setting tangent flag.");
                    tangentFlag = true;
                }
                if(RiffToolbox.isGreaterThan(yIntersection, Math.max(testPointA.getY(), testPointB.getY()))) {
                    assert LegacyDebugger.close("The Y-intercept is greater than the maximum of the Y-values of the testPoints, returning null.");
                    return null;
                } else if(RiffToolbox.areEqual(
                    testPointA,
                    yIntersection,
                    Math.max(testPointA.getY(), testPointB.getY()))) {
                    assert LegacyDebugger.addNode("The Y-intercept is equal to the maximum of the Y-values of the testpoints, setting tangent flag.");
                    tangentFlag = true;
                }
            }
            if(!RiffToolbox.areEqual(Point.System.EUCLIDEAN, 0.0d, slope)) {
                assert LegacyDebugger.addNode("The slope is not zero.");
                if(RiffToolbox.isLessThan(yIntersection, Math.min(pointA.getY(), pointB.getY()))) {
                    assert LegacyDebugger.close("The Y-intercept is less than the minimum of the Y-values of the control points, returning null.");
                    return null;
                } else if(RiffToolbox.areEqual(testPointA, yIntersection, Math.min(pointA.getY(), pointB.getY()))) {
                    assert LegacyDebugger.addNode("The Y-intercept is equal to the minimum of the Y-values of the control points, setting tangent flag.");
                    tangentFlag = true;
                }
                if(RiffToolbox.isGreaterThan(yIntersection, Math.max(pointA.getY(), pointB.getY()))) {
                    assert LegacyDebugger.close("The Y-intercept is greater than the maximum of the Y-values of the control points, returning null.");
                    return null;
                } else if(RiffToolbox.areEqual(testPointA, yIntersection, Math.max(pointA.getY(), pointB.getY()))) {
                    assert LegacyDebugger.addNode("The Y-intercept is equal to the maximum of the Y-values of the control points, setting tangent flag.");
                    tangentFlag = true;
                }
            }
            Point intersectPoint = createPoint(pointA, "("
                + pointA.getName()
                + ","
                + pointB.getName()
                + ")+("
                + testPointA.getName()
                + ","
                + testPointB.getName()
                + ")", pointA.getX(), yIntersection);
            IntersectionPoint point = new IntersectionPoint(intersectPoint, tangentFlag);
            if(tangentFlag) {
                assert LegacyDebugger.close("These lines intersect, but only on a tangent: " + intersectPoint, point);
            } else {
                assert LegacyDebugger.close("These lines intersect: " + intersectPoint, point);
            }
            return point;
        }
        if(Math.abs(testSlope) == Double.POSITIVE_INFINITY) {
            assert LegacyDebugger.addNode("Slope of the test points is infinite.");
            if(RiffToolbox.isGreaterThan(testPointA.getX(), Math.max(pointA.getX(), pointB.getX()))) {
                assert LegacyDebugger.close("The X-value is greater than the maximum X-value of the control points, returning null.");
                return null;
            } else if(RiffToolbox.areEqual(testPointA, testPointA.getX(), Math.max(pointA.getX(), pointB.getX()))) {
                assert LegacyDebugger.addNode("The X-value is equal to the maximum X-value of the control points, setting tangent flag.");
                tangentFlag = true;
            }
            if(RiffToolbox.isLessThan(testPointA.getX(), Math.min(pointA.getX(), pointB.getX()))) {
                assert LegacyDebugger.close("The X-value is less than the minimum X-value of the control points, returning null.");
                return null;
            } else if(RiffToolbox.areEqual(testPointA, testPointA.getX(), Math.min(pointA.getX(), pointB.getX()))) {
                assert LegacyDebugger.addNode("The X-value is equal to the minimum X-value of the control points, setting tangent flag.");
                tangentFlag = true;
            }
            double yIntersection = slope * (testPointA.getX() - pointA.getX()) + pointA.getY();
            if(!RiffToolbox.areEqual(Point.System.EUCLIDEAN, 0.0d, testSlope)) {
                assert LegacyDebugger.addNode("The testSlope is not zero.");
                if(RiffToolbox.isLessThan(yIntersection, Math.min(testPointA.getY(), testPointB.getY()))) {
                    assert LegacyDebugger.close("The Y-intercept is less than the minimum of the Y-values of the testPoints, returning null.");
                    return null;
                } else if(RiffToolbox.areEqual(
                    testPointA,
                    yIntersection,
                    Math.min(testPointA.getY(), testPointB.getY()))) {
                    assert LegacyDebugger.addNode("The Y-intercept is equal to the minimum of the Y-values of the testPoints, setting tangent flag.");
                    tangentFlag = true;
                }
                if(RiffToolbox.isGreaterThan(yIntersection, Math.max(testPointA.getY(), testPointB.getY()))) {
                    assert LegacyDebugger.close("The Y-intercept is greater than the maximum of the Y-values of the testPoints, returning null.");
                    return null;
                } else if(RiffToolbox.areEqual(
                    testPointA,
                    yIntersection,
                    Math.max(testPointA.getY(), testPointB.getY()))) {
                    assert LegacyDebugger.addNode("The Y-intercept is equal to the maximum of the Y-values of the testpoints, setting tangent flag.");
                    tangentFlag = true;
                }
            }
            if(!RiffToolbox.areEqual(Point.System.EUCLIDEAN, 0.0d, slope)) {
                assert LegacyDebugger.addNode("The slope is not zero.");
                if(RiffToolbox.isLessThan(yIntersection, Math.min(pointA.getY(), pointB.getY()))) {
                    assert LegacyDebugger.close("The Y-intercept is less than the minimum of the Y-values of the control points, returning null.");
                    return null;
                } else if(RiffToolbox.areEqual(testPointA, yIntersection, Math.min(pointA.getY(), pointB.getY()))) {
                    assert LegacyDebugger.addNode("The Y-intercept is equal to the minimum of the Y-values of the control points, setting tangent flag.");
                    tangentFlag = true;
                }
                if(RiffToolbox.isGreaterThan(yIntersection, Math.max(pointA.getY(), pointB.getY()))) {
                    assert LegacyDebugger.close("The Y-intercept is greater than the maximum of the Y-values of the control points, returning null.");
                    return null;
                } else if(RiffToolbox.areEqual(testPointA, yIntersection, Math.max(pointA.getY(), pointB.getY()))) {
                    assert LegacyDebugger.addNode("The Y-intercept is equal to the maximum of the Y-values of the control points, setting tangent flag.");
                    tangentFlag = true;
                }
            }
            Point intersectPoint = createPoint(pointA, "("
                + pointA.getName()
                + ","
                + pointB.getName()
                + ")+("
                + testPointA.getName()
                + ","
                + testPointB.getName()
                + ")", testPointA.getX(), yIntersection);
            IntersectionPoint returning = new IntersectionPoint(intersectPoint, tangentFlag);
            if(tangentFlag) {
                assert LegacyDebugger.close("These lines intersect, but only on a tangent: " + intersectPoint, returning);
            } else {
                assert LegacyDebugger.close("These lines intersect: " + intersectPoint, returning);
            }
            return returning;
        }
        // Bounding rect testing of the two lines.
        if(RiffToolbox.isLessThan(
            Math.max(pointB.getX(), pointA.getX()),
            Math.min(testPointA.getX(), testPointB.getX()))) {
            assert LegacyDebugger.close("The maximum x-value of the control points is less than the minimum X-value of the testPoints, returning null.");
            return null;
        } else if(RiffToolbox.areEqual(testPointA, Math.max(pointB.getX(), pointA.getX()), Math.min(
            testPointA.getX(),
            testPointB.getX()))) {
            assert LegacyDebugger.addNode("The maximum x-value of the control points is equal to the minimum X-value of the testPoints, setting tangent flag.");
            tangentFlag = true;
        }
        if(RiffToolbox.isGreaterThan(Math.min(pointB.getX(), pointA.getX()), Math.max(
            testPointA.getX(),
            testPointB.getX()))) {
            assert LegacyDebugger.close("The minimum x-value of the control points is greater than the maximum X-value of the testPoints, returning null.");
            return null;
        } else if(RiffToolbox.areEqual(testPointA, Math.min(pointB.getX(), pointA.getX()), Math.max(
            testPointA.getX(),
            testPointB.getX()))) {
            assert LegacyDebugger.addNode("The minimum x-value of the control points is equal to the maximum X-value of the testPoints, setting tangent flag.");
            tangentFlag = true;
        }
        if(RiffToolbox.isLessThan(
            Math.max(pointA.getY(), pointB.getY()),
            Math.min(testPointA.getY(), testPointB.getY()))) {
            assert LegacyDebugger.close("The maximum Y-value of the control points is less than the minimum Y-value of the testPoints, returning null.");
            return null;
        } else if(RiffToolbox.areEqual(testPointA, Math.max(pointA.getY(), pointB.getY()), Math.min(
            testPointA.getY(),
            testPointB.getY()))) {
            assert LegacyDebugger.addNode("The maximum Y-value of the control points is equal to the minimum  Y-value of the testPoints, setting tangent flag.");
            tangentFlag = true;
        }
        if(RiffToolbox.isGreaterThan(Math.min(pointA.getY(), pointB.getY()), Math.max(
            testPointA.getY(),
            testPointB.getY()))) {
            assert LegacyDebugger.close("The minimum Y-value of the control points is greater than the maximum Y-value of the testPoints, returning null.");
            return null;
        } else if(RiffToolbox.areEqual(testPointA, Math.min(pointA.getY(), pointB.getY()), Math.max(
            testPointA.getY(),
            testPointB.getY()))) {
            assert LegacyDebugger.addNode("The minimum Y-value of the control points is equal to than the maximum Y-value of the testPoints, setting tangent flag.");
            tangentFlag = true;
        }
        // X-intersection testing.
        double xIntersection = ((-slope * pointA.getX() + pointA.getY()) - (-testSlope * testPointA.getX() + testPointA.getY()))
            / (testSlope - slope);
        assert LegacyDebugger.addNode("The X-intercept between these two points is: " + xIntersection);
        if(RiffToolbox.isLessThan(xIntersection, Math.min(testPointA.getX(), testPointB.getX()))) {
            assert LegacyDebugger.close("The X-intercept is less than the minimum of the X-values of the testPoints, returning null.");
            return null;
        } else if(RiffToolbox.areEqual(testPointA, xIntersection, Math.min(testPointA.getX(), testPointB.getX()))) {
            assert LegacyDebugger.addNode("The X-intercept is equal to the minimum of the X-values of the testPoints, setting tangent flag.");
            tangentFlag = true;
        }
        if(RiffToolbox.isGreaterThan(xIntersection, Math.max(testPointA.getX(), testPointB.getX()))) {
            assert LegacyDebugger.close("The X-intercept is greater than the maximum of the X-values of the testPoints, returning null.");
            return null;
        } else if(RiffToolbox.areEqual(testPointA, xIntersection, Math.max(testPointA.getX(), testPointB.getX()))) {
            assert LegacyDebugger.addNode("The X-intercept is equal to the maximum of the X-values of the testPoints, setting tangent flag.");
            tangentFlag = true;
        }
        if(RiffToolbox.isLessThan(xIntersection, Math.min(pointA.getX(), pointB.getX()))) {
            assert LegacyDebugger.close("The X-intercept is less than the minimum of the X-values of the control points, returning null.");
            return null;
        } else if(RiffToolbox.areEqual(testPointA, xIntersection, Math.min(pointA.getX(), pointB.getX()))) {
            assert LegacyDebugger.addNode("The X-intercept is equal to the minimum of the X-values of the control points, setting tangent flag.");
            tangentFlag = true;
        }
        if(RiffToolbox.isGreaterThan(xIntersection, Math.max(pointA.getX(), pointB.getX()))) {
            assert LegacyDebugger.close("The X-intercept is greater than the maximum of the X-values of the control points, returning null.");
            return null;
        } else if(RiffToolbox.areEqual(testPointA, xIntersection, Math.max(pointA.getX(), pointB.getX()))) {
            assert LegacyDebugger.addNode("The X-intercept is equal to the maximum of the X-values of the control points, setting tangent flag.");
            tangentFlag = true;
        }
        // Y-intersection testing.
        double yIntersection = slope * xIntersection + (-slope * pointA.getX() + pointA.getY());
        assert LegacyDebugger.addNode("The Y-intercept between these two points is: " + yIntersection);
        if(!RiffToolbox.areEqual(Point.System.EUCLIDEAN, 0.0d, testSlope)) {
            assert LegacyDebugger.addNode("The testSlope is not zero.");
            if(RiffToolbox.isLessThan(yIntersection, Math.min(testPointA.getY(), testPointB.getY()))) {
                assert LegacyDebugger.close("The Y-intercept is less than the minimum of the Y-values of the testPoints, returning null.");
                return null;
            } else if(RiffToolbox.areEqual(testPointA, yIntersection, Math.min(testPointA.getY(), testPointB.getY()))) {
                assert LegacyDebugger.addNode("The Y-intercept is equal to the minimum of the Y-values of the testPoints, setting tangent flag.");
                tangentFlag = true;
            }
            if(RiffToolbox.isGreaterThan(yIntersection, Math.max(testPointA.getY(), testPointB.getY()))) {
                assert LegacyDebugger.close("The Y-intercept is greater than the maximum of the Y-values of the testPoints, returning null.");
                return null;
            } else if(RiffToolbox.areEqual(testPointA, yIntersection, Math.max(testPointA.getY(), testPointB.getY()))) {
                assert LegacyDebugger.addNode("The Y-intercept is equal to the maximum of the Y-values of the testpoints, setting tangent flag.");
                tangentFlag = true;
            }
        }
        if(!RiffToolbox.areEqual(Point.System.EUCLIDEAN, 0.0d, slope)) {
            assert LegacyDebugger.addNode("The slope is not zero.");
            if(RiffToolbox.isLessThan(yIntersection, Math.min(pointA.getY(), pointB.getY()))) {
                assert LegacyDebugger.close("The Y-intercept is less than the minimum of the Y-values of the control points, returning null.");
                return null;
            } else if(RiffToolbox.areEqual(testPointA, yIntersection, Math.min(pointA.getY(), pointB.getY()))) {
                assert LegacyDebugger.addNode("The Y-intercept is equal to the minimum of the Y-values of the control points, setting tangent flag.");
                tangentFlag = true;
            }
            if(RiffToolbox.isGreaterThan(yIntersection, Math.max(pointA.getY(), pointB.getY()))) {
                assert LegacyDebugger.close("The Y-intercept is greater than the maximum of the Y-values of the control points, returning null.");
                return null;
            } else if(RiffToolbox.areEqual(testPointA, yIntersection, Math.max(pointA.getY(), pointB.getY()))) {
                assert LegacyDebugger.addNode("The Y-intercept is equal to the maximum of the Y-values of the control points, setting tangent flag.");
                tangentFlag = true;
            }
        }
        Point intersectPoint = createPoint(pointA, "("
            + pointA.getName()
            + ","
            + pointB.getName()
            + ")+("
            + testPointA.getName()
            + ","
            + testPointB.getName()
            + ")", xIntersection, yIntersection);
        IntersectionPoint returning = new IntersectionPoint(intersectPoint, tangentFlag);
        // New point creation of intersection.
        if(tangentFlag) {
            assert LegacyDebugger.close("These lines intersect, but only on a tangent: " + intersectPoint, returning);
        } else {
            assert LegacyDebugger.close("These lines intersect: " + intersectPoint, returning);
        }
        return returning;
    }

    // Confirms that the line formed by the two points is an interior line.
    public static boolean confirmInteriorLine(Point pointA, Point pointB, DiscreteRegion region) {
        List<Point> pointList = region.getPoints();
        assert LegacyDebugger.open("Polygon/confirmInteriorLine", String.format(
            "Confirming interior line with these points: %s, %s",
            pointA,
            pointB));
        assert LegacyDebugger.log(region);
        assert LegacyDebugger.log("Testing for intersections.");
        for(int i = 0; i < pointList.size(); i++) {
            Point testPointA = pointList.get(i);
            Point testPointB = pointList.get((i + 1) % pointList.size());
            if((testPointA.equals(pointA) && testPointB.equals(pointB))
                || (testPointA.equals(pointB) && testPointB.equals(pointA))) {
                return false;
            }
            IntersectionPoint intersect = RiffPolygonToolbox.getIntersection(pointA, pointB, testPointA, testPointB);
            if(intersect != null && !intersect.isTangent()) {
                assert LegacyDebugger.log("Testing line intersected the polygon, returning false.");
                assert LegacyDebugger.format("Failing line: %s, %s", testPointA, testPointB);
                assert LegacyDebugger.close();
                return false;
            }
        }
        assert LegacyDebugger.log("Checking for crosses.");
        int crosses = RiffPolygonToolbox.getCrosses(pointA.getX() + (pointB.getX() - pointA.getX()) / 2, pointA.getY()
            + (pointB.getY() - pointA.getY())
            / 2, region);
        if(crosses == 0 || crosses % 2 == 0) {
            assert LegacyDebugger.close("Cross-test failed, returning false.");
            return false;
        }
        assert LegacyDebugger.close("Interior line confirmed, returning true,");
        return true;
    }

    // This process converts a concave polygon to a list of convex polygons.
    public static List<DiscreteRegion> convertPolyToConvex(DiscreteRegion originalRegion) {
        assert LegacyDebugger.open(
            "Polygon/convertPolyToConvex",
            "Attempting to convert this polygon into its convex parts...");
        assert LegacyDebugger.log("Original region", originalRegion);
        DiscreteRegion region = RiffPolygonToolbox.optimizePolygon(originalRegion);
        assert LegacyDebugger.log("Optimized region", region);
        if(region == null) {
            return null;
        }
        List<DiscreteRegion> convexPolygons = new LinkedList<DiscreteRegion>();
        if(RiffPolygonToolbox.isPolygonConvex(region) == true) {
            assert LegacyDebugger.close("This polygon is convex, so adding it to the list and returning.");
            convexPolygons.add(region);
            return convexPolygons;
        }
        assert LegacyDebugger.log("This polygon is concave. Attempting to subdivide...");
        DiscreteRegion testRegion;
        List<Point> pointList = region.getPoints();
        for(int i = 0; i < pointList.size(); i++) {
            boolean alreadyCreated = false;
            assert LegacyDebugger.log("Attempting to form a triangle from these points");
            assert LegacyDebugger.format(
                "First point: %s, Second Point: %s, Third Point: %s",
                pointList.get(i),
                pointList.get((i + 1) % pointList.size()),
                pointList.get((i + 2) % pointList.size()));
            if(RiffPolygonToolbox.confirmInteriorLine(pointList.get(i), pointList.get((i + 2)
                % pointList.size()), region) == false) {
                assert LegacyDebugger.close("Failed interior-line test.");
                continue;
            }
            testRegion = new DiscreteRegion(region.getEnvironment(), region.getProperties());
            testRegion.addPoint(pointList.get(i));
            testRegion.addPoint(pointList.get((i + 1) % pointList.size()));
            testRegion.addPoint(pointList.get((i + 2) % pointList.size()));
            for(int k = 0; k < convexPolygons.size(); k++) {
                if(testRegion.equals(convexPolygons.get(k))) {
                    alreadyCreated = true;
                    break;
                }
            }
            if(alreadyCreated) {
                assert LegacyDebugger.close("Polygon already created.");
                continue;
            }
            assert LegacyDebugger.log("Removing this point: " + pointList.get((i + 1) % pointList.size()));
            region.removePoint(pointList.get((i + 1) % pointList.size()));
            assert LegacyDebugger.close("Yielding the new triangle and this remaining region", region);
            convexPolygons.add(testRegion);
            convexPolygons.addAll(RiffPolygonToolbox.convertPolyToConvex(region));
            break;
        }
        return convexPolygons;
    }

    // Converts the list of polygons into convex polygons, returning the list of
    // these.
    public static List<DiscreteRegion> convertPolyToConvex(List<DiscreteRegion> polygons) {
        List<DiscreteRegion> list = new LinkedList<DiscreteRegion>();
        for(int i = 0; i < polygons.size(); i++) {
            list.addAll(RiffPolygonToolbox.convertPolyToConvex(polygons.get(i)));
        }
        return list;
    }

    // Tests for bounding rect intersection between the two regions.
    public static boolean getBoundingRectIntersection(DiscreteRegion region, DiscreteRegion otherRegion) {
        if(region.getLeftExtreme() >= otherRegion.getRightExtreme()) {
            return false;
        }
        if(region.getRightExtreme() <= otherRegion.getLeftExtreme()) {
            return false;
        }
        if(region.getTopExtreme() <= otherRegion.getBottomExtreme()) {
            return false;
        }
        if(region.getBottomExtreme() >= otherRegion.getTopExtreme()) {
            return false;
        }
        return true;
    }

    // Takes a list of convex, non-overlapping polygons, and joins them if they
    // share a common border and their joined polygon is convex. This function
    // returns this list, or the originalPolygon in a list if no polygons were
    // joined.
    public static List<DiscreteRegion> joinPolygons(List<DiscreteRegion> polyList) {
        List<DiscreteRegion> optimizedList = new LinkedList<DiscreteRegion>();
        for(int i = 0; i < polyList.size(); i++) {
            for(int j = 0; j < polyList.size(); j++) {
                if(i == j) {
                    continue;
                }
                DiscreteRegion firstRegion = polyList.get(i);
                DiscreteRegion secondRegion = polyList.get(j);
                for(int q = 0; q < firstRegion.getPoints().size(); q++) {
                    for(int x = 0; x < secondRegion.getPoints().size(); x++) {
                        if(!firstRegion.getPoints().get(q).equals(secondRegion.getPoints().get(x))
                            && !firstRegion.getPoints().get((q + 1) % firstRegion.getPoints().size()).equals(
                                secondRegion.getPoints().get(x))) {
                            continue;
                        }
                        if(!firstRegion.getPoints().get(q).equals(
                            secondRegion.getPoints().get((x + 1) % secondRegion.getPoints().size()))
                            && !firstRegion.getPoints().get((q + 1) % firstRegion.getPoints().size()).equals(
                                secondRegion.getPoints().get((x + 1) % secondRegion.getPoints().size()))) {
                            continue;
                        }
                        DiscreteRegion testRegion = new DiscreteRegion(firstRegion.getEnvironment(), firstRegion.getProperties());
                        PointSideStruct struct = RiffPolygonToolbox.getPointSideList(
                            firstRegion,
                            firstRegion.getPoints().get(q),
                            firstRegion.getPoints().get((q + 1) % firstRegion.getPoints().size()));
                        if(struct.getLeftPoints().isEmpty()) {
                            int firstPoint = q;
                            if(q > (q + 1) % firstRegion.getPoints().size()) {
                                firstPoint = (q + 1) % firstRegion.getPoints().size();
                            }
                            for(int offsetPoint = 0; offsetPoint < firstRegion.getPoints().size(); offsetPoint++) {
                                testRegion.addPoint(firstRegion.getPoints().get(
                                    (firstPoint + offsetPoint) % firstRegion.getPoints().size()));
                            }
                            firstPoint = x;
                            if(x > (x + 1) % secondRegion.getPoints().size()) {
                                firstPoint = (x + 1) % secondRegion.getPoints().size();
                            }
                            for(int offsetPoint = 1; offsetPoint < secondRegion.getPoints().size(); offsetPoint++) {
                                testRegion.addPoint(secondRegion.getPoints().get(
                                    (firstPoint + offsetPoint) % secondRegion.getPoints().size()));
                            }
                        } else {
                            int firstPoint = x;
                            if(x > (x + 1) % secondRegion.getPoints().size()) {
                                firstPoint = (x + 1) % secondRegion.getPoints().size();
                            }
                            for(int offsetPoint = 0; offsetPoint < secondRegion.getPoints().size(); offsetPoint++) {
                                testRegion.addPoint(secondRegion.getPoints().get(
                                    (firstPoint + offsetPoint) % secondRegion.getPoints().size()));
                            }
                            firstPoint = q;
                            if(q > (q + 1) % firstRegion.getPoints().size()) {
                                firstPoint = (q + 1) % firstRegion.getPoints().size();
                            }
                            for(int offsetPoint = 1; offsetPoint < firstRegion.getPoints().size(); offsetPoint++) {
                                testRegion.addPoint(firstRegion.getPoints().get(
                                    (firstPoint + offsetPoint) % firstRegion.getPoints().size()));
                            }
                        }
                        if(!RiffPolygonToolbox.isPolygonConvex(testRegion)) {
                            continue;
                        }
                        optimizedList.add(testRegion);
                        polyList.remove(firstRegion);
                        polyList.remove(secondRegion);
                        optimizedList.addAll(polyList);
                        return RiffPolygonToolbox.joinPolygons(optimizedList);
                    }
                }
            }
        }
        return polyList;
    }

    public static void assertCCWPolygon(DiscreteRegion region) {
        assert LegacyDebugger.open("Asserting CCW-rotation of region.");
        if(region.isOptimized()) {
            assert LegacyDebugger.close("Region already optimized, returning.");
            return;
        }
        assert LegacyDebugger.log(region);
        assert LegacyDebugger.log("Interior point: " + region.getInteriorPoint());
        PointSideStruct struct = RiffPolygonToolbox.getPointSideList(region, region.getInteriorPoint());
        if(struct.hasIndeterminates()) {
            assert LegacyDebugger.close("Unanticipated colinear values!");
            return;
        }
        if(struct.getLeftPoints().isEmpty()) {
            assert LegacyDebugger.log("Polygon is clockwise, so reversing points and returning.");
            region.reversePoints();
            assert LegacyDebugger.close();
        } else if(struct.getRightPoints().isEmpty()) {
            assert LegacyDebugger.close("Polygon is CCW, so returning.");
        }
    }

    public static Point getMidPointOfLine(Point pointA, Point pointB) {
        double xValue = Math.min(pointA.getX(), pointB.getX())
            + .5
            * (Math.max(pointA.getX(), pointB.getX()) - Math.min(pointA.getX(), pointB.getX()));
        double yValue = Math.min(pointA.getY(), pointB.getY())
            + .5
            * (Math.max(pointA.getY(), pointB.getY()) - Math.min(pointA.getY(), pointB.getY()));
        Point point = createPoint(pointA, null, xValue, yValue);
        return point;
    }

    public static Point[] getAdjacentEdge(DiscreteRegion region, DiscreteRegion neighbor) {
        assert LegacyDebugger.open("Adjacent Edge Finding", "Finding Adjacent Edge");
        assert LegacyDebugger.addSnapNode("Region", region);
        assert LegacyDebugger.addSnapNode("Neighbor", neighbor);
        List<Point> pointList = region.getPoints();
        IntersectionPoint intersect = null;
        for(int i = 0; i < pointList.size(); i++) {
            List<Point> otherPointList = neighbor.getPoints();
            Point pointA = pointList.get(i);
            Point pointB = pointList.get((i + 1) % pointList.size());
            assert LegacyDebugger.open("Line-by-Line Tests", "Control points: " + pointA + ", " + pointB);
            for(int j = 0; j < otherPointList.size(); j++) {
                Point pointC = otherPointList.get(j);
                Point pointD = otherPointList.get((j + 1) % otherPointList.size());
                assert LegacyDebugger.open("Testing points (" + pointC + ", " + pointD + ")");
                PointSideStruct struct = RiffPolygonToolbox.getPointSideList(pointA, pointB, pointC, pointD);
                if(struct.getIndeterminates().size() == 2) {
                    Point minPoint, otherMinPoint;
                    IntersectionPoint thisIntersect = RiffPolygonToolbox.getIntersection(pointA, pointB, pointC, pointD);
                    if(thisIntersect != null
                        && !RiffPolygonToolbox.getBoundingRectIntersection(pointA, pointB, pointC, pointD, false)) {
                        intersect = thisIntersect;
                        assert LegacyDebugger.close("They intersect on a tangent, saving and continuing (Intersection: "
                            + thisIntersect
                            + ")");
                        continue;
                    }
                    if((pointA.equals(pointC) && pointB.equals(pointD))
                        || (pointA.equals(pointD) && pointB.equals(pointC))) {
                        assert LegacyDebugger.addNode("Lines are identical.");
                        minPoint = pointA;
                        otherMinPoint = pointB;
                    } else {
                        assert LegacyDebugger.addNode("Finding middle points...");
                        if(pointA.equals(pointC)) {
                            minPoint = findMiddlePoint(pointA, pointB, pointD);
                            otherMinPoint = pointC;
                        } else if(pointB.equals(pointC)) {
                            minPoint = findMiddlePoint(pointA, pointD, pointB);
                            otherMinPoint = pointC;
                        } else if(pointA.equals(pointD)) {
                            minPoint = findMiddlePoint(pointA, pointB, pointC);
                            otherMinPoint = pointD;
                        } else if(pointB.equals(pointD)) {
                            minPoint = findMiddlePoint(pointA, pointB, pointC);
                            otherMinPoint = pointD;
                        } else {
                            minPoint = findMiddlePoint(pointA, pointB, pointC);
                            otherMinPoint = findMiddlePoint(pointB, pointC, pointD);
                        }
                    }
                    Point[] pointArray = new Point[2];
                    pointArray[0] = minPoint;
                    pointArray[1] = otherMinPoint;
                    assert LegacyDebugger.close();
                    assert LegacyDebugger.close();
                    assert LegacyDebugger.close("Returning point array", pointArray);
                    return pointArray;
                }
                assert LegacyDebugger.close("No colinearity found, continuing.");
            }
            assert LegacyDebugger.close("No conclusive matches found for this point.");
        }
        if(intersect != null) {
            Point[] pointArray = new Point[2];
            pointArray[0] = intersect.getPoint();
            pointArray[1] = intersect.getPoint();
            assert LegacyDebugger.close("Returning point array", pointArray);
            return pointArray;
        }
        assert LegacyDebugger.close("They share no adjacent edge, returning null.");
        return null;
    }

    public static Point getMinimumPointBetweenLine(Point pointA, Point pointB, Point source) {
        assert LegacyDebugger.open("Polygon/getMinimumPointBetweenLine");
        assert LegacyDebugger.format("Source: %s, Points: %s, %s", source, pointA, pointB);
        if(pointA.equals(pointB)) {
            assert LegacyDebugger.close("PointA is equal to PointB, returning pointA.");
            return pointA;
        }
        double omegaValue = ((source.getX() - pointA.getX()) * (pointB.getX() - pointA.getX()) + (source.getY() - pointA.getY())
            * (pointB.getY() - pointA.getY()))
            / Math.pow(RiffToolbox.getDistance(pointA, pointB), 2);
        double xValue = pointA.getX() + omegaValue * (pointB.getX() - pointA.getX());
        double yValue = pointA.getY() + omegaValue * (pointB.getY() - pointA.getY());
        Point minimumPoint = createPoint(pointA, null, xValue, yValue);
        assert LegacyDebugger.close("Point yielded: " + minimumPoint);
        return RiffPolygonToolbox.findMiddlePoint(pointA, pointB, minimumPoint);
    }

    public static boolean getBoundingRectIntersection(double xMin, double xMax, double yMin, double yMax, Point point,
        boolean allowTangent) {
        if(RiffToolbox.isLessThan(point.getX(), xMin)) {
            return false;
        }
        if(RiffToolbox.areEqual(point, point.getX(), xMin) && !allowTangent) {
            return false;
        }
        if(RiffToolbox.isGreaterThan(point.getX(), xMax)) {
            return false;
        }
        if(RiffToolbox.areEqual(point, point.getX(), xMax) && !allowTangent) {
            return false;
        }
        if(RiffToolbox.isLessThan(point.getY(), yMin)) {
            return false;
        }
        if(RiffToolbox.areEqual(point, point.getY(), yMin) && !allowTangent) {
            return false;
        }
        if(RiffToolbox.isGreaterThan(point.getY(), yMax)) {
            return false;
        }
        if(RiffToolbox.areEqual(point, point.getY(), yMax) && !allowTangent) {
            return false;
        }
        return true;
    }

    public static Point findMiddlePoint(Point pointA, Point pointB, Point pointC) {
        assert LegacyDebugger.open("Polygon/findMiddlePoint", String.format(
            "Points to test: %s, %s, %s",
            pointA,
            pointB,
            pointC));
        if(RiffPolygonToolbox.getBoundingRectIntersection(
            Math.min(pointA.getX(), pointB.getX()),
            Math.max(pointA.getX(), pointB.getX()),
            Math.min(pointA.getY(), pointB.getY()),
            Math.max(pointA.getY(), pointB.getY()),
            pointC,
            true)) {
            assert LegacyDebugger.close("Returning pointC.");
            return pointC;
        }
        if(RiffPolygonToolbox.getBoundingRectIntersection(
            Math.min(pointA.getX(), pointC.getX()),
            Math.max(pointA.getX(), pointC.getX()),
            Math.min(pointA.getY(), pointC.getY()),
            Math.max(pointA.getY(), pointC.getY()),
            pointB,
            true)) {
            assert LegacyDebugger.close("Returning pointB.");
            return pointB;
        }
        assert LegacyDebugger.close("Returning pointA.");
        return pointA;
    }

    public static double testPointAgainstLine(Point testPoint, Point linePointA, Point linePointB) {
        return -1
            * ((testPoint.getY() - linePointA.getY()) * (linePointB.getX() - linePointA.getX()) - (testPoint.getX() - linePointA.getX())
                * (linePointB.getY() - linePointA.getY()));
    }

    // Using the two given points to create a line, it returns a list of the
    // distribution of points from the polygon.
    // This list contains two lists, one containing all the points on the left
    // side, and one containing all the points on the right.
    public static void doPointSideTest(PointSideStruct struct, Point point, double value) {
        if(RiffToolbox.areEqual(Point.System.EUCLIDEAN, value, 0.0d)) {
            struct.addIndeterminate(point);
        } else if(RiffToolbox.isGreaterThan(value, 0.0d)) {
            struct.addRight(point, value);
        } else {
            struct.addLeft(point, value);
        }
    }

    public static PointSideStruct getPointSideList(DiscreteRegion region, Point linePointA, Point linePointB) {
        assert LegacyDebugger.open("Point-Side Tests", "Point-Side Test (Region vs. Line)");
        assert LegacyDebugger.addSnapNode("Test-Line", linePointA + ", " + linePointB);
        assert LegacyDebugger.addSnapNode("Region", region);
        List<Point> pointList = region.getPoints();
        PointSideStruct struct = new PointSideStruct();
        for(int k = 0; k < pointList.size(); k++) {
            Point testPoint = pointList.get(k);
            if(RiffPolygonToolbox.testForColinearity(linePointA, linePointB, testPoint)) {
                struct.addIndeterminate(testPoint);
                continue;
            }
            doPointSideTest(struct, testPoint, RiffPolygonToolbox.testPointAgainstLine(
                testPoint,
                linePointA,
                linePointB));
        }
        struct.validate();
        assert LegacyDebugger.close(struct);
        return struct;
    }

    public static PointSideStruct getPointSideList(Point linePointA, Point linePointB, Point testPointA,
        Point testPointB) {
        assert LegacyDebugger.open("Point-Side Tests", "Point-Side Test (Line vs. Line)");
        assert LegacyDebugger.addSnapNode("First-Line", linePointA + ", " + linePointB);
        assert LegacyDebugger.addSnapNode("Test-Line", testPointA + ", " + testPointB);
        PointSideStruct struct = new PointSideStruct();
        if(RiffPolygonToolbox.testForColinearity(linePointA, linePointB, testPointA)) {
            struct.addIndeterminate(testPointA);
        } else {
            doPointSideTest(struct, testPointA, RiffPolygonToolbox.testPointAgainstLine(
                testPointA,
                linePointA,
                linePointB));
        }
        if(RiffPolygonToolbox.testForColinearity(linePointA, linePointB, testPointB)) {
            struct.addIndeterminate(testPointB);
        } else {
            doPointSideTest(struct, testPointB, RiffPolygonToolbox.testPointAgainstLine(
                testPointB,
                linePointA,
                linePointB));
        }
        struct.validate();
        assert LegacyDebugger.close(struct);
        return struct;
    }

    public static PointSideStruct getPointSideList(DiscreteRegion region, Point testPoint) {
        List<Point> pointList = region.getPoints();
        assert LegacyDebugger.open("Point-Side Tests", "Point-Side Test (Point vs. Region)");
        assert LegacyDebugger.addNode("Test-Point: " + testPoint);
        assert LegacyDebugger.addSnapNode("Testing-Region", region);
        PointSideStruct struct = new PointSideStruct();
        for(int k = 0; k < pointList.size(); k++) {
            Point linePointA = pointList.get(k);
            Point linePointB = pointList.get((k + 1) % pointList.size());
            if(RiffPolygonToolbox.testForColinearity(linePointA, linePointB, testPoint)) {
                struct.addIndeterminate(testPoint);
                continue;
            }
            doPointSideTest(struct, testPoint, RiffPolygonToolbox.testPointAgainstLine(
                testPoint,
                linePointA,
                linePointB));
        }
        struct.validate();
        assert LegacyDebugger.close(struct);
        return struct;
    }

    public static class RiffIntersectionPoint implements Nodeable {
        private final Point m_intersect;
        private final int m_location;

        public RiffIntersectionPoint(Point intersect, int location) {
            this.m_intersect = intersect;
            this.m_location = location;
        }

        public int getLocation() {
            return this.m_location;
        }

        public Point getIntersection() {
            return this.m_intersect;
        }

        public boolean nodificate() {
            assert LegacyDebugger.open("Intersection-Point Struct");
            assert LegacyDebugger.addNode("Intersection-Point: " + this.m_intersect);
            assert LegacyDebugger.addNode("Point-list offset of intersection: " + this.m_location);
            assert LegacyDebugger.close();
            return true;
        }

        @Override
        public boolean equals(Object o) {
            if(!(o instanceof RiffIntersectionPoint)) {
                return false;
            }
            return getIntersection().equals(((RiffIntersectionPoint)o).getIntersection());
        }
    }
}
