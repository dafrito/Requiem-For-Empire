package com.dafrito.util;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Exception_InternalError;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.economy.Asset;
import com.dafrito.geom.DiscreteRegion;
import com.dafrito.geom.DiscreteRegionBSPNode;
import com.dafrito.geom.Point;
import com.dafrito.geom.Point_Path;
import com.dafrito.geom.PolygonPipeline;
import com.dafrito.geom.RiffPolygonToolbox;
import com.dafrito.geom.SplitterThread;
import com.dafrito.script.Parser;
import com.dafrito.script.ScriptEnvironment;
import com.dafrito.script.ScriptTemplate_Abstract;
import com.dafrito.script.executable.ScriptExecutable_CallFunction;
import com.dafrito.script.types.ScriptValue_Abstract;
import com.dafrito.util.RiffToolbox;

public class Terrestrial implements Serializable {
    private double radius;
    private DiscreteRegionBSPNode tree;
    private ScriptEnvironment environment;
    private volatile int openThreads = 0;

    public Terrestrial(ScriptEnvironment env, double radius) {
        this.environment = env;
        this.radius = radius;
    }

    public ScriptEnvironment getEnvironment() {
        return this.environment;
    }

    public double getRadius() {
        return this.radius;
    }

    public void add(DiscreteRegion region) {
        this.openThreads++;
        PolygonPipeline pipeline = new PolygonPipeline(this, region);
        pipeline.start();
    }

    public synchronized void addValidatedRegions(List<DiscreteRegion> regions) {
        if(regions == null || regions.size() == 0) {
            return;
        }
        assert LegacyDebugger.open("Validated Region Additions", "Adding Validated Regions ("
            + regions.size()
            + " region(s))");
        if(getTree() == null) {
            setTree(new DiscreteRegionBSPNode(regions.get(0)));
            if(regions.size() == 1) {
                assert LegacyDebugger.close();
                decrementOpenThreads();
                return;
            }
        }
        SplitterThread thread = new SplitterThread(this, getTree(), regions, true);
        thread.start();
        assert LegacyDebugger.close();
    }

    public void decrementOpenThreads() {
        this.openThreads--;
    }

    public Point_Path getPath(ScriptEnvironment env, Scenario scenario, ScriptTemplate_Abstract evaluator, Asset asset,
        Point currentPoint, Point destinationPoint) throws Exception_Nodeable {
        while (this.openThreads != 0) {
            try {
                Thread.sleep(100);
            } catch(InterruptedException ex) {
                throw new Exception_InternalError(getEnvironment(), ex);
            }
        }
        assert LegacyDebugger.open("Pathfinding", "Getting path (" + currentPoint + " to " + destinationPoint + ")");
        Point_Path path = new Point_Path(env, scenario);
        DiscreteRegion startingRegion;
        assert getTree() != null : "BSP Tree is null!";
        DiscreteRegion currentRegion = startingRegion = getTree().getRegion(currentPoint);
        DiscreteRegion destination = getTree().getRegion(destinationPoint);
        List<ScriptValue_Abstract> params = new LinkedList<ScriptValue_Abstract>();
        params.add(Parser.getRiffDiscreteRegion(currentRegion));
        params.add(Parser.getRiffAsset(asset));
        path.addPoint(currentPoint, Parser.getDouble(ScriptExecutable_CallFunction.callFunction(
            getEnvironment(),
            null,
            evaluator,
            "evaluateMovementCost",
            params)));
        int ticker = 0;
        Stack<DiscreteRegion> regionPath = new Stack<DiscreteRegion>();
        regionPath.push(currentRegion);
        Set<DiscreteRegion> used = new HashSet<DiscreteRegion>();
        List<DiscreteRegion> availableNeighbors = new LinkedList<DiscreteRegion>();
        List<Point> nearestNeighborPoints = new LinkedList<Point>();
        List<Double> movementCosts = new LinkedList<Double>();
        while (!currentRegion.equals(destination)) {
            if(ticker > 100)
                throw new Exception_InternalError("Pathfinder iteration tolerance exceeded.");
            ticker++;
            assert LegacyDebugger.open("Pathfinder iteration " + ticker);
            assert LegacyDebugger.addSnapNode("Current region", currentRegion);
            if(currentRegion.getNeighbors().size() == 0) {
                assert LegacyDebugger.close("Current region has no neighbors, returning null list");
                path = null;
                break;
            }
            availableNeighbors.clear();
            nearestNeighborPoints.clear();
            assert LegacyDebugger.open("Current region's neighbors (" + currentRegion.getNeighbors().size() + " neighbor(s))");
            for(DiscreteRegion neighbor : currentRegion.getNeighbors()) {
                assert LegacyDebugger.open("Discrete Region (" + neighbor.getPoints().size() + " point(s))");
                assert LegacyDebugger.addSnapNode("Properties", neighbor.getProperties());
                assert LegacyDebugger.addSnapNode("Points", neighbor.getPoints());
                assert LegacyDebugger.close();
            }
            assert LegacyDebugger.close();
            assert LegacyDebugger.open("Getting valid neighbors");
            for(DiscreteRegion neighbor : currentRegion.getNeighbors()) {
                if(used.contains(neighbor)) {
                    continue;
                }
                assert LegacyDebugger.addSnapNode("Placing neighbor in neighbors list", neighbor);
                assert LegacyDebugger.open("Retrieving nearest colinear point");
                Point[] line = RiffPolygonToolbox.getAdjacentEdge(currentRegion, neighbor);
                Point point = RiffPolygonToolbox.getMinimumPointBetweenLine(line[0], line[1], currentPoint);
                assert LegacyDebugger.addNode("Adding neighbor and point (" + point + ")", neighbor);
                availableNeighbors.add(neighbor);
                nearestNeighborPoints.add(point);
                assert LegacyDebugger.close();
            }
            assert LegacyDebugger.close(
                "Available neighbors (" + availableNeighbors.size() + " neighbor(s))",
                availableNeighbors);
            if(availableNeighbors.isEmpty()) {
                assert LegacyDebugger.addNode("Stepping back");
                if(currentRegion.equals(startingRegion)) {
                    assert LegacyDebugger.close("No route available.");
                    path = null;
                    break;
                }
                path.removeLastPoint();
                used.add(currentRegion);
                assert regionPath.size() != 0;
                currentRegion = regionPath.pop();
                assert LegacyDebugger.close("No neighbors available, stepping back");
                continue;
            }
            movementCosts.clear();
            assert LegacyDebugger.open("Getting movement costs (" + availableNeighbors.size() + " neighbor(s))");
            for(DiscreteRegion region : availableNeighbors) {
                params.clear();
                params.add(Parser.getRiffDiscreteRegion(region));
                params.add(Parser.getRiffAsset(asset));
                movementCosts.add(Parser.getDouble(ScriptExecutable_CallFunction.callFunction(
                    getEnvironment(),
                    null,
                    evaluator,
                    "evaluateMovementCost",
                    params)));
            }
            assert LegacyDebugger.close("Movement costs", movementCosts);
            double minimumValue = Double.POSITIVE_INFINITY;
            int optimum = -1;
            assert LegacyDebugger.open("Getting optimum region (" + availableNeighbors.size() + " option(s))");
            for(int i = 0; i < availableNeighbors.size(); i++) {
                double value = RiffToolbox.getDistance(currentPoint, nearestNeighborPoints.get(i))
                    * path.getLastMovementCost();
                assert LegacyDebugger.addNode("Movement cost from current location to border of current region ("
                    + value
                    + ")");
                value += RiffToolbox.getDistance(nearestNeighborPoints.get(i), destinationPoint) * movementCosts.get(i);
                assert LegacyDebugger.addSnapNode(
                    "Current neighbor (Total movement cost: " + value + ")",
                    availableNeighbors.get(i));
                if(RiffToolbox.isLessThan(value, minimumValue)) {
                    assert LegacyDebugger.addNode("Value is less than current minimum, setting as new value ("
                        + minimumValue
                        + " to "
                        + value
                        + ")");
                    minimumValue = value;
                    optimum = i;
                }
            }
            assert LegacyDebugger.close();
            Point neighborPoint = nearestNeighborPoints.get(optimum);
            path.addPoint(neighborPoint, movementCosts.get(optimum));
            used.add(currentRegion);
            currentRegion = availableNeighbors.get(optimum);
            assert LegacyDebugger.addSnapNode("New optimum region (At optimum point: " + neighborPoint + ")", currentRegion);
            regionPath.push(currentRegion);
            assert LegacyDebugger.addSnapNode("Region Path", regionPath);
            assert LegacyDebugger.close("Current path", path);
        }
        if(path != null) {
            params.clear();
            params.add(Parser.getRiffDiscreteRegion(destination));
            params.add(Parser.getRiffAsset(asset));
            path.addPoint(destinationPoint, Parser.getDouble(ScriptExecutable_CallFunction.callFunction(
                getEnvironment(),
                null,
                evaluator,
                "evaluateMovementCost",
                params)));
            assert LegacyDebugger.close("Path", path);
        } else {
            throw new Exception_InternalError("No route available");
        }
        return path;
    }

    public DiscreteRegionBSPNode getTree() {
        return this.tree;
    }

    public void setTree(DiscreteRegionBSPNode tree) {
        this.tree = tree;
    }
}
