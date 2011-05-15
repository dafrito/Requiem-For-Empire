import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class Terrestrial implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5769369184511259491L;
	private double radius;
	private DiscreteRegionBSPNode tree;
	private ScriptEnvironment environment;
	private volatile int openThreads = 0;

	public Terrestrial(ScriptEnvironment env, double radius) {
		this.environment = env;
		this.radius = radius;
	}

	public void add(DiscreteRegion region) {
		this.openThreads++;
		PolygonPipeline pipeline = new PolygonPipeline(this, region);
		pipeline.start();
	}

	public synchronized void addValidatedRegions(List<DiscreteRegion> regions) {
		if (regions == null || regions.size() == 0) {
			return;
		}
		assert Debugger.openNode("Validated Region Additions", "Adding Validated Regions (" + regions.size() + " region(s))");
		if (this.getTree() == null) {
			this.setTree(new DiscreteRegionBSPNode(regions.get(0)));
			if (regions.size() == 1) {
				assert Debugger.closeNode();
				this.decrementOpenThreads();
				return;
			}
		}
		SplitterThread thread = new SplitterThread(this, this.getTree(), regions, true);
		thread.start();
		assert Debugger.closeNode();
	}

	public void decrementOpenThreads() {
		this.openThreads--;
	}

	public ScriptEnvironment getEnvironment() {
		return this.environment;
	}

	public Point_Path getPath(ScriptEnvironment env, Scenario scenario, ScriptTemplate_Abstract evaluator, Asset asset, Point currentPoint, Point destinationPoint) throws Exception_Nodeable {
		while (this.openThreads != 0) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException ex) {
				throw new Exception_InternalError(this.getEnvironment(), ex);
			}
		}
		assert Debugger.openNode("Pathfinding", "Getting path (" + currentPoint + " to " + destinationPoint + ")");
		Point_Path path = new Point_Path(env, scenario);
		DiscreteRegion startingRegion;
		assert this.getTree() != null : "BSP Tree is null!";
		DiscreteRegion currentRegion = startingRegion = this.getTree().getRegion(currentPoint);
		DiscreteRegion destination = this.getTree().getRegion(destinationPoint);
		List<ScriptValue_Abstract> params = new LinkedList<ScriptValue_Abstract>();
		params.add(Parser.getRiffDiscreteRegion(this.getEnvironment(), currentRegion));
		params.add(Parser.getRiffAsset(this.getEnvironment(), asset));
		path.addPoint(currentPoint, Parser.getDouble(ScriptExecutable_CallFunction.callFunction(this.getEnvironment(), null, evaluator, "evaluateMovementCost", params)));
		int ticker = 0;
		Stack<DiscreteRegion> regionPath = new Stack<DiscreteRegion>();
		regionPath.push(currentRegion);
		Set<DiscreteRegion> used = new HashSet<DiscreteRegion>();
		List<DiscreteRegion> availableNeighbors = new LinkedList<DiscreteRegion>();
		List<Point> nearestNeighborPoints = new LinkedList<Point>();
		List<Double> movementCosts = new LinkedList<Double>();
		while (!currentRegion.equals(destination)) {
			if (ticker > 100) {
				throw new Exception_InternalError("Pathfinder iteration tolerance exceeded.");
			} else {
				ticker++;
			}
			assert Debugger.openNode("Pathfinder iteration " + ticker);
			assert Debugger.addSnapNode("Current region", currentRegion);
			if (currentRegion.getNeighbors().size() == 0) {
				assert Debugger.closeNode("Current region has no neighbors, returning null list");
				path = null;
				break;
			}
			availableNeighbors.clear();
			nearestNeighborPoints.clear();
			assert Debugger.openNode("Current region's neighbors (" + currentRegion.getNeighbors().size() + " neighbor(s))");
			for (DiscreteRegion neighbor : currentRegion.getNeighbors()) {
				assert Debugger.openNode("Discrete Region (" + neighbor.getPoints().size() + " point(s))");
				assert Debugger.addSnapNode("Properties", neighbor.getProperties());
				assert Debugger.addSnapNode("Points", neighbor.getPoints());
				assert Debugger.closeNode();
			}
			assert Debugger.closeNode();
			assert Debugger.openNode("Getting valid neighbors");
			for (DiscreteRegion neighbor : currentRegion.getNeighbors()) {
				if (used.contains(neighbor)) {
					continue;
				}
				assert Debugger.addSnapNode("Placing neighbor in neighbors list", neighbor);
				assert Debugger.openNode("Retrieving nearest colinear point");
				Point[] line = RiffPolygonToolbox.getAdjacentEdge(currentRegion, neighbor);
				Point point = RiffPolygonToolbox.getMinimumPointBetweenLine(line[0], line[1], currentPoint);
				assert Debugger.addNode("Adding neighbor and point (" + point + ")", neighbor);
				availableNeighbors.add(neighbor);
				nearestNeighborPoints.add(point);
				assert Debugger.closeNode();
			}
			assert Debugger.closeNode("Available neighbors (" + availableNeighbors.size() + " neighbor(s))", availableNeighbors);
			if (availableNeighbors.isEmpty()) {
				assert Debugger.addNode("Stepping back");
				if (currentRegion.equals(startingRegion)) {
					assert Debugger.closeNode("No route available.");
					path = null;
					break;
				}
				path.removeLastPoint();
				used.add(currentRegion);
				assert regionPath.size() != 0;
				currentRegion = regionPath.pop();
				assert Debugger.closeNode("No neighbors available, stepping back");
				continue;
			}
			movementCosts.clear();
			assert Debugger.openNode("Getting movement costs (" + availableNeighbors.size() + " neighbor(s))");
			for (DiscreteRegion region : availableNeighbors) {
				params.clear();
				params.add(Parser.getRiffDiscreteRegion(this.getEnvironment(), region));
				params.add(Parser.getRiffAsset(this.getEnvironment(), asset));
				movementCosts.add(Parser.getDouble(ScriptExecutable_CallFunction.callFunction(this.getEnvironment(), null, evaluator, "evaluateMovementCost", params)));
			}
			assert Debugger.closeNode("Movement costs", movementCosts);
			double minimumValue = Double.POSITIVE_INFINITY;
			int optimum = -1;
			assert Debugger.openNode("Getting optimum region (" + availableNeighbors.size() + " option(s))");
			for (int i = 0; i < availableNeighbors.size(); i++) {
				double value = RiffToolbox.getDistance(currentPoint, nearestNeighborPoints.get(i)) * path.getLastMovementCost();
				assert Debugger.addNode("Movement cost from current location to border of current region (" + value + ")");
				value += RiffToolbox.getDistance(nearestNeighborPoints.get(i), destinationPoint) * movementCosts.get(i);
				assert Debugger.addSnapNode("Current neighbor (Total movement cost: " + value + ")", availableNeighbors.get(i));
				if (RiffToolbox.isLessThan(value, minimumValue)) {
					assert Debugger.addNode("Value is less than current minimum, setting as new value (" + minimumValue + " to " + value + ")");
					minimumValue = value;
					optimum = i;
				}
			}
			assert Debugger.closeNode();
			currentPoint = nearestNeighborPoints.get(optimum);
			path.addPoint(currentPoint, movementCosts.get(optimum));
			used.add(currentRegion);
			currentRegion = availableNeighbors.get(optimum);
			assert Debugger.addSnapNode("New optimum region (At optimum point: " + currentPoint + ")", currentRegion);
			regionPath.push(currentRegion);
			assert Debugger.addSnapNode("Region Path", regionPath);
			assert Debugger.closeNode("Current path", path);
		}
		if (path != null) {
			params.clear();
			params.add(Parser.getRiffDiscreteRegion(this.getEnvironment(), destination));
			params.add(Parser.getRiffAsset(this.getEnvironment(), asset));
			path.addPoint(destinationPoint, Parser.getDouble(ScriptExecutable_CallFunction.callFunction(this.getEnvironment(), null, evaluator, "evaluateMovementCost", params)));
			assert Debugger.closeNode("Path", path);
		} else {
			throw new Exception_InternalError("No route available");
		}
		return path;
	}

	public double getRadius() {
		return this.radius;
	}

	public DiscreteRegionBSPNode getTree() {
		return this.tree;
	}

	public void setTree(DiscreteRegionBSPNode tree) {
		this.tree = tree;
	}
}
