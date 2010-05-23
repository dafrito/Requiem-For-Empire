package com.dafrito.geom;

import java.util.LinkedList;
import java.util.List;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.util.Scenario;
import com.dafrito.debug.Exception_InternalError;
import com.dafrito.debug.Nodeable;
import com.dafrito.script.ScriptConvertible;
import com.dafrito.script.ScriptEnvironment;
import com.dafrito.script.templates.FauxTemplate_Path;
import com.dafrito.script.types.ScriptValueType;
import com.dafrito.util.RiffToolbox;

public class Point_Path extends Point implements ScriptConvertible, Nodeable {
    private List<Point> m_points = new LinkedList<Point>();
    private List<Double> m_movementCosts = new LinkedList<Double>();
    private long m_startTime;
    private Scenario m_scenario;

    public Point_Path(ScriptEnvironment env, Scenario scenario) {
        super(env, null);
        this.m_scenario = scenario;
        setStartTime(this.m_scenario.getGameTime());
    }

    public Scenario getScenario() {
        return this.m_scenario;
    }

    public void setScenario(Scenario scenario) {
        this.m_scenario = scenario;
    }

    public void addPoint(Point point, Double velocity) {
        this.m_points.add(Point.createPoint(point, point.getX(), point.getY(), point.getZ()));
        this.m_movementCosts.add(velocity);
    }

    public void removeLastPoint() {
        if(this.m_points.size() > 0) {
            this.m_points.remove(this.m_points.size() - 1);
        }
        if(this.m_movementCosts.size() > 0) {
            this.m_movementCosts.remove(0);
        }
    }

    public double getLastMovementCost() {
        return this.m_movementCosts.get(this.m_movementCosts.size() - 1).doubleValue();
    }

    public void setStartTime(long time) {
        this.m_startTime = time;
    }

    public long getStartTime() {
        return this.m_startTime;
    }

    public long getTotalTime() {
        long time = 0;
        for(int i = 0; i < this.m_points.size() - 1; i++) {
            double total = RiffToolbox.getDistance(this.m_points.get(i), this.m_points.get(i + 1));
            time += (long)(total / getVelocity(this.m_movementCosts.get(i)));
        }
        return time;
    }

    public double getVelocity(double movementCost) {
        return movementCost / 10.0d;
    }

    public Point getCurrentPoint() {
        assert LegacyDebugger.open("Path Point Retrievals", "Getting path point");
        assert LegacyDebugger.addNode(this);
        while (this.m_points.size() > 1) {
            double distance = (getScenario().getGameTime() - this.m_startTime)
                * getVelocity(this.m_movementCosts.get(0)); // distance
            // traveled
            double total = RiffToolbox.getDistance(this.m_points.get(0), this.m_points.get(1));
            double offset = distance / total;
            if(distance >= total) {
                this.m_startTime += (long)(total / getVelocity(this.m_movementCosts.get(0)));
                this.m_movementCosts.remove(0);
                this.m_points.remove(0);
                continue;
            }
            Point point = Point.createPoint(this.m_points.get(0), this.m_points.get(0).getX()
                + (this.m_points.get(1).getX() - this.m_points.get(0).getX())
                * offset, this.m_points.get(0).getY()
                + (this.m_points.get(1).getY() - this.m_points.get(0).getY())
                * offset, this.m_points.get(0).getZ()
                + (this.m_points.get(1).getZ() - this.m_points.get(0).getZ())
                * offset);
            assert LegacyDebugger.close();
            return point;
        }
        assert LegacyDebugger.close();
        return this.m_points.get(0);
    }

    // Point implementation
    @Override
    public double getX() {
        return getCurrentPoint().getX();
    }

    @Override
    public double getY() {
        return getCurrentPoint().getY();
    }

    @Override
    public double getZ() {
        return getCurrentPoint().getZ();
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

    @Override
    public Point.System getSystem() {
        return Point.System.EUCLIDEAN;
    }

    // ScriptConvertible implementation
    public Object convert() {
        FauxTemplate_Path path = new FauxTemplate_Path(getEnvironment(), ScriptValueType.createType(
            getEnvironment(),
            FauxTemplate_Path.PATHSTRING));
        path.setPoint(this);
        return path;
    }

    // Nodeable implementation
    public boolean nodificate() {
        assert LegacyDebugger.open("Path");
        assert LegacyDebugger.addSnapNode("Points", this.m_points);
        assert LegacyDebugger.addSnapNode("Movement Costs", this.m_movementCosts);
        assert LegacyDebugger.addNode("Start time: " + this.m_startTime);
        assert LegacyDebugger.close();
        return true;
    }
}
