import java.util.LinkedList;
import java.util.List;

public class Point_Path extends Point implements ScriptConvertible, Nodeable {
	private List<Point> m_points = new LinkedList<Point>();
	private List<Double> m_movementCosts = new LinkedList<Double>();
	private long m_startTime;
	private Scenario m_scenario;

	public Point_Path(ScriptEnvironment env, Scenario scenario) {
		this(env, null, scenario);
	}

	public Point_Path(ScriptEnvironment env, String name, Scenario scenario) {
		super(env, null);
		this.m_scenario = scenario;
		this.setStartTime(this.m_scenario.getGameTime());
	}

	public void addPoint(Point point, Double velocity) {
		this.m_points.add(Point.createPoint(point, point.getX(), point.getY(), point.getZ()));
		this.m_movementCosts.add(velocity);
	}

	// ScriptConvertible implementation
	@Override
	public Object convert() {
		FauxTemplate_Path path = new FauxTemplate_Path(this.getEnvironment(), ScriptValueType.createType(this.getEnvironment(), FauxTemplate_Path.PATHSTRING));
		path.setPoint(this);
		return path;
	}

	public Point getCurrentPoint() {
		assert Debugger.openNode("Path Point Retrievals", "Getting path point");
		assert Debugger.addNode(this);
		while (this.m_points.size() > 1) {
			double distance = (this.getScenario().getGameTime() - this.m_startTime) * this.getVelocity(this.m_movementCosts.get(0)); // distance traveled
			double total = RiffToolbox.getDistance(this.m_points.get(0), this.m_points.get(1));
			double offset = distance / total;
			if (distance >= total) {
				this.m_startTime += (long) (total / this.getVelocity(this.m_movementCosts.get(0)));
				this.m_movementCosts.remove(0);
				this.m_points.remove(0);
				continue;
			}
			Point point = Point.createPoint(
					this.m_points.get(0),
					this.m_points.get(0).getX() + (this.m_points.get(1).getX() - this.m_points.get(0).getX()) * offset,
					this.m_points.get(0).getY() + (this.m_points.get(1).getY() - this.m_points.get(0).getY()) * offset,
					this.m_points.get(0).getZ() + (this.m_points.get(1).getZ() - this.m_points.get(0).getZ()) * offset
					);
			assert Debugger.closeNode();
			return point;
		}
		assert Debugger.closeNode();
		return this.m_points.get(0);
	}

	public double getLastMovementCost() {
		return this.m_movementCosts.get(this.m_movementCosts.size() - 1).doubleValue();
	}

	public Scenario getScenario() {
		return this.m_scenario;
	}

	public long getStartTime() {
		return this.m_startTime;
	}

	@Override
	public Point.System getSystem() {
		return Point.System.EUCLIDEAN;
	}

	public long getTotalTime() {
		long time = 0;
		for (int i = 0; i < this.m_points.size() - 1; i++) {
			double total = RiffToolbox.getDistance(this.m_points.get(i), this.m_points.get(i + 1));
			time += (long) (total / this.getVelocity(this.m_movementCosts.get(i)));
		}
		return time;
	}

	public double getVelocity(double movementCost) {
		return movementCost / 10.0d;
	}

	// Point implementation
	@Override
	public double getX() {
		return this.getCurrentPoint().getX();
	}

	@Override
	public double getY() {
		return this.getCurrentPoint().getY();
	}

	@Override
	public double getZ() {
		return this.getCurrentPoint().getZ();
	}

	// Nodeable implementation
	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Path");
		assert Debugger.addSnapNode("Points", this.m_points);
		assert Debugger.addSnapNode("Movement Costs", this.m_movementCosts);
		assert Debugger.addNode("Start time: " + this.m_startTime);
		assert Debugger.closeNode();
		return true;
	}

	public void removeLastPoint() {
		if (this.m_points.size() > 0) {
			this.m_points.remove(this.m_points.size() - 1);
		}
		if (this.m_movementCosts.size() > 0) {
			this.m_movementCosts.remove(0);
		}
	}

	public void setScenario(Scenario scenario) {
		this.m_scenario = scenario;
	}

	public void setStartTime(long time) {
		this.m_startTime = time;
	}

	@Override
	public void setX(double x) {
		throw new Exception_InternalError("Unsupported operation");
	}

	@Override
	public void setY(double y) {
		throw new Exception_InternalError("Unsupported operation");
	}

	@Override
	public void setZ(double z) {
		throw new Exception_InternalError("Unsupported operation");
	}
}
