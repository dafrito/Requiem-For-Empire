import java.util.*;
public class Point_Path extends Point implements ScriptConvertible,Nodeable{
	private List<Point>m_points=new LinkedList<Point>();
	private List<Double>m_movementCosts=new LinkedList<Double>();
	private long m_startTime;
	private Scenario m_scenario;
	public Point_Path(ScriptEnvironment env,Scenario scenario){
		this(env,null,scenario);
	}
	public Point_Path(ScriptEnvironment env,String name,Scenario scenario){
		super(env,null);
		m_scenario=scenario;
		setStartTime(m_scenario.getGameTime());
	}
	public Scenario getScenario(){return m_scenario;}
	public void setScenario(Scenario scenario){m_scenario=scenario;}
	public void addPoint(Point point,Double velocity){
		m_points.add(Point.createPoint(point,point.getX(),point.getY(),point.getZ()));
		m_movementCosts.add(velocity);
	}
	public void removeLastPoint(){
		if(m_points.size()>0){m_points.remove(m_points.size()-1);}
		if(m_movementCosts.size()>0){m_movementCosts.remove(0);}
	}
	public double getLastMovementCost(){return m_movementCosts.get(m_movementCosts.size()-1).doubleValue();}
	public void setStartTime(long time){m_startTime=time;}
	public long getStartTime(){return m_startTime;}
	public long getTotalTime(){
		long time=0;
		for(int i=0;i<m_points.size()-1;i++){
			double total=RiffToolbox.getDistance(m_points.get(i),m_points.get(i+1));
			time+=(long)(total/getVelocity(m_movementCosts.get(i)));
		}
		return time;
	}
	public double getVelocity(double movementCost){
		return movementCost/10.0d;
	}
	public Point getCurrentPoint(){
		assert Debugger.openNode("Path Point Retrievals","Getting path point");
		assert Debugger.addNode(this);
		while(m_points.size()>1){
			double distance=(getScenario().getGameTime()-m_startTime)*getVelocity(m_movementCosts.get(0)); // distance traveled
			double total=RiffToolbox.getDistance(m_points.get(0),m_points.get(1));
			double offset=distance/total;
			if(distance>=total){
				m_startTime+=(long)(total/getVelocity(m_movementCosts.get(0)));
				m_movementCosts.remove(0);
				m_points.remove(0);
				continue;
			}
			Point point=Point.createPoint(
				m_points.get(0),
				m_points.get(0).getX()+(m_points.get(1).getX()-m_points.get(0).getX())*offset,
				m_points.get(0).getY()+(m_points.get(1).getY()-m_points.get(0).getY())*offset,
				m_points.get(0).getZ()+(m_points.get(1).getZ()-m_points.get(0).getZ())*offset
			);
			assert Debugger.closeNode();
			return point;
		}
		assert Debugger.closeNode();
		return m_points.get(0);
	}
	// Point implementation
	public double getX(){return getCurrentPoint().getX();}
	public double getY(){return getCurrentPoint().getY();}
	public double getZ(){return getCurrentPoint().getZ();}
	public void setX(double x){throw new Exception_InternalError("Unsupported operation");}
	public void setY(double y){throw new Exception_InternalError("Unsupported operation");}
	public void setZ(double z){throw new Exception_InternalError("Unsupported operation");}
	public Point.System getSystem(){return Point.System.EUCLIDEAN;}
	// ScriptConvertible implementation
	public Object convert(){
		FauxTemplate_Path path=new FauxTemplate_Path(getEnvironment(),ScriptValueType.createType(getEnvironment(),FauxTemplate_Path.PATHSTRING));
		path.setPoint(this);
		return path;
	}
	// Nodeable implementation
	public boolean nodificate(){
		assert Debugger.openNode("Path");
		assert Debugger.addSnapNode("Points",m_points);
		assert Debugger.addSnapNode("Movement Costs",m_movementCosts);
		assert Debugger.addNode("Start time: "+m_startTime);
		assert Debugger.closeNode();
		return true;
	}
}
