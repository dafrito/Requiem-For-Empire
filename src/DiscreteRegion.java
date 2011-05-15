import java.util.*;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
public class DiscreteRegion implements Nodeable,ScriptConvertible{
	private List<Point>m_points;
	private Set<DiscreteRegion>m_neighbors;
	private Map<DiscreteRegion,Integer>m_intersectionMap=new HashMap<DiscreteRegion,Integer>();
	private double m_leftExtreme, m_rightExtreme, m_topExtreme, m_bottomExtreme;
	private Point m_midPoint, m_interiorPoint;
	private int m_version;
	private boolean m_isOptimized;
	private Map<String,Object>m_properties;
	private ScriptEnvironment m_environment;
	public DiscreteRegion(ScriptEnvironment env){
		assert Debugger.openNode("Discrete Region Creation","Creating discrete region (Script environment provided)");
		if(env==null){assert Debugger.addNode("ScriptEnvironment: null");
		}else{assert Debugger.addNode(env);}
		m_environment=env;
		m_points=new Vector<Point>();
		m_neighbors=new HashSet<DiscreteRegion>();
		m_properties=new HashMap<String,Object>();
		resetExtrema();
		assert Debugger.closeNode("Region created",this);
	}
	public DiscreteRegion(ScriptEnvironment env,Map<String,Object>prop){
		assert Debugger.openNode("Discrete Region Creation","Creating discrete region (Script environment and properties provided)");
		m_environment=env;
		m_points=new Vector<Point>();
		m_neighbors=new HashSet<DiscreteRegion>();
		m_properties=prop;
		assert Debugger.closeNode("Region created",this);
	}
	public DiscreteRegion(){this((ScriptEnvironment)null);}
	public DiscreteRegion(DiscreteRegion otherRegion){
		assert Debugger.openNode("Discrete Region Creation","Creating discrete region (Duplicating otherRegion)");
		m_environment=otherRegion.getEnvironment();
		m_neighbors=new HashSet<DiscreteRegion>();
		m_points=new Vector<Point>(otherRegion.getPoints());
		m_leftExtreme=otherRegion.getLeftExtreme();
		m_rightExtreme=otherRegion.getRightExtreme();
		m_topExtreme=otherRegion.getTopExtreme();
		m_bottomExtreme=otherRegion.getBottomExtreme();
		m_isOptimized=otherRegion.isOptimized();
		m_version=otherRegion.getVersion();
		m_properties=otherRegion.getProperties();
		assert Debugger.closeNode("Region created",this);
	}
	public ScriptEnvironment getEnvironment(){return m_environment;}
	public void setProperty(String name,Object prop){m_properties.put(name,prop);}
	public Object getProperty(String name){return m_properties.get(name);}
	public Map<String,Object>getProperties(){return m_properties;}
	public void setProperties(Map<String,Object>prop){m_properties=prop;}
	public static DiscreteRegion transform(DiscreteRegion region,Point offset,Rectangle bounds,boolean zoom){
		assert Debugger.openNode("Discrete-Region Transformation","Transforming Discrete Region");
		assert Debugger.addNode(region);
		// Offset translation
		List<Point>points=new LinkedList<Point>(region.getPoints());
		DiscreteRegion transformedRegion=new DiscreteRegion(region.getEnvironment());
		transformedRegion.setProperties(region.getProperties());
		for(Point point:points){
			transformedRegion.addPoint(transformPoint(region.getEnvironment(),point,offset,bounds,zoom));
		}
		assert Debugger.closeNode("Transformed Region",transformedRegion);
		return transformedRegion;
	}
	public static Point transformPoint(ScriptEnvironment env,Point point,Point offset,Rectangle bounds,boolean zoom){
		double width=(bounds.getWidth()-bounds.getX())/2;
		double height=(bounds.getHeight()-bounds.getY())/2;
		double ax=point.getX()-offset.getX();
		double ay=point.getY()-offset.getY();
		// Orthographic zoom
		if(zoom){
			ax=ax*Math.pow(2,offset.getZ());
			ay=ay*Math.pow(2,offset.getZ());
		}
		return new Point_Euclidean(env,ax+bounds.getX()+width,ay+bounds.getY()+height,0.0d);
	}
	public static void paint(Graphics2D g2d,DiscreteRegion transformedRegion,Rectangle bounds,boolean fill){
		assert Debugger.openNode("Discrete-Region Painting","Painting Discrete Region");
		assert Debugger.addNode(transformedRegion);
		RiffPolygonToolbox.optimizePolygon(transformedRegion);
		transformedRegion=RiffPolygonToolbox.clip(transformedRegion,RiffJavaToolbox.convertToRegion(transformedRegion.getEnvironment(),bounds));
		if(transformedRegion!=null){
			// Draw transformed line
			assert Debugger.addSnapNode("Clipped Region",transformedRegion);
			if(fill){
				if(transformedRegion.getProperty("Color")!=null){
					g2d.setColor((Color)transformedRegion.getProperty("Color"));
				}else{
					g2d.setColor(Color.WHITE);
				}
				g2d.fillPolygon(RiffJavaToolbox.convertToPolygon(transformedRegion));
			}else{
				if(transformedRegion.getProperty("BorderColor")!=null){
					g2d.setColor((Color)transformedRegion.getProperty("BorderColor"));
				}else if(transformedRegion.getProperty("Color")!=null){
					g2d.setColor((Color)transformedRegion.getProperty("Color"));
				}else{
					g2d.setColor(Color.WHITE);
				}
				g2d.drawPolygon(RiffJavaToolbox.convertToPolygon(transformedRegion));
			}
		}
		assert Debugger.closeNode();
	}
	public void resetNeighbors(){m_neighbors.clear();}
	public double getLeftExtreme(){return m_leftExtreme;}
	public double getRightExtreme(){return m_rightExtreme;}
	public double getTopExtreme(){return m_topExtreme;}
	public double getBottomExtreme(){return m_bottomExtreme;}
	public Point getCenter(){
		if(m_midPoint==null){
			m_midPoint = Point.createPoint(m_points.get(0),getLeftExtreme()+((getRightExtreme()-getLeftExtreme())/2),  getBottomExtreme()+((getTopExtreme()-getBottomExtreme())/2), 0.0d);
		}
		return m_midPoint;
	}
	public Point getInteriorPoint(){
		return getCenter();
		//RiffPolygonToolbox.getMidPointOfLine(RiffPolygonToolbox.getMidPointOfLine(m_points.get(0), m_points.get(1)), RiffPolygonToolbox.getMidPointOfLine(m_points.get(1), m_points.get(2)));
		//return m_interiorPoint;
	}
	public Set<DiscreteRegion>getNeighbors(){return new HashSet<DiscreteRegion>(m_neighbors);}
	public List<Point>getPoints(){return new LinkedList<Point>(m_points);}
	public void addPoint(Point point){
		if(point==null){return;}
		assert Debugger.openNode("Discrete Region Point Additions","Adding point to discrete region ("+point+")");
		testExtrema(point);
		m_points.add(point);
		resetIntersectionMap();
		assert Debugger.closeNode(this);
	}
	public void addPointAt(int location, Point point){
		assert Debugger.openNode("Discrete Region Point Additions","Adding point to discrete region ("+point+") at index ("+location+")");
		testExtrema(point);
		m_points.add(location, point);
		assert Debugger.addSnapNode("New point list",m_points);
		resetIntersectionMap();
		assert Debugger.closeNode();
	}
	public void reversePoints(){
		assert Debugger.addNode("Reversing points");
		Collections.reverse(m_points);
	}
	public void removePoint(int pointNum){
		Point point = (Point)m_points.get(pointNum);
		removePoint(point);
	}
	public void removePoint(Point point){
		if(!m_points.contains(point)){return;}
		assert Debugger.openNode("Discrete Region Point Removals","Removing this point from discrete region ("+point+")");
		m_points.remove(point);
		if(point.getX()==m_leftExtreme||point.getX()==m_rightExtreme||point.getY()==m_topExtreme||point.getY()==m_bottomExtreme){
			recalculateExtrema();
		}
		assert Debugger.addSnapNode("New point list",m_points);
		resetIntersectionMap();
		assert Debugger.closeNode();
	}
	public void setPointList(List<Point>pointList){
		assert Debugger.openNode("Setting this discrete region's point list to a provided one");
		assert Debugger.addSnapNode("Old point list",m_points);
		m_points=pointList;
		recalculateExtrema();
		resetIntersectionMap();
		assert Debugger.addSnapNode("New point list",m_points);
		assert Debugger.closeNode();
	}
	private void testExtrema(Point point){
		if(point.getX()<m_leftExtreme){
			assert Debugger.addNode("Extrema Settings","Setting left extrema to ("+point.getX()+") from ("+m_leftExtreme+")");
			m_leftExtreme=point.getX();
		}
		if(point.getX()>m_rightExtreme){
			assert Debugger.addNode("Extrema Settings","Setting right extrema to ("+point.getX()+") from ("+m_rightExtreme+")");
			m_rightExtreme=point.getX();
		}
		if(point.getY()<m_bottomExtreme){
			assert Debugger.addNode("Extrema Settings","Setting bottom extrema to ("+point.getY()+") from ("+m_bottomExtreme+")");
			m_bottomExtreme=point.getY();
		}
		if(point.getY()>m_topExtreme){
			assert Debugger.addNode("Extrema Settings","Setting top extrema to ("+point.getY()+") from ("+m_topExtreme+")");
			m_topExtreme=point.getY();
		}
	}
	private void resetExtrema(){
		assert Debugger.addNode("Extrema Settings","Resetting extrema.");
		m_leftExtreme=java.lang.Double.POSITIVE_INFINITY;
		m_rightExtreme=java.lang.Double.NEGATIVE_INFINITY;
		m_topExtreme=m_rightExtreme;
		m_bottomExtreme=m_leftExtreme;
	}
	private void recalculateExtrema(){
		assert Debugger.openNode("Extrema Recalculations","Recalculating extrema");
		resetExtrema();
		for(int i=0;i<m_points.size();i++){
			testExtrema((Point)m_points.get(i));
		}
		assert Debugger.closeNode();
	}
	public int getVersion(){return m_version;}
	public boolean isOptimized(){return m_isOptimized;}
	public void setOptimized(boolean optimized){m_isOptimized=optimized;}
	public void resetIntersectionMap(){
		assert Debugger.addNode("Resetting discrete region's intersection map");
		m_intersectionMap.clear();
		m_interiorPoint=m_midPoint=null;
		m_version++;
		m_isOptimized=false;
		recheckNeighbors();
	}
	public void addRegionToMap(DiscreteRegion region){
		assert Debugger.addSnapNode("Intersection Map Additions","Adding this region to intersection map",region);
		m_intersectionMap.put(region, region.getVersion());
	}
	public boolean checkClearedRegionMap(DiscreteRegion region){
		assert Debugger.openNode("Checking cleared intersection map for region");
		assert Debugger.addNode("If found, this means that the region does not intersect this region and all further testing can be omitted.");
		assert Debugger.addSnapNode("Region to find",region);
		if(m_intersectionMap.get(region)==null){
			assert Debugger.closeNode("Region not found");
			return false;
		}
		if(m_intersectionMap.get(region)==region.getVersion()){
			assert Debugger.closeNode("Region found and version is identical.");
			return true;
		}else{
			assert Debugger.closeNode("Region's version differs from our saved version, returning false.");
			return false;
		}
	}
	public void addNeighbor(DiscreteRegion region){
		if(region.equals(this)){return;}
		assert Debugger.addSnapNode("Discrete Region Neighbor Additions","Adding this region as a neighbor",region);
		m_neighbors.add(region);
	}
	public void addRegionNeighbor(DiscreteRegion region){
		if(region.equals(this)){return;}
		assert Debugger.openNode("Discrete Region Neighbor Evaluations","Checking for neighbor status");
		assert Debugger.addSnapNode("This region",this);
		assert Debugger.addSnapNode("Potential neighbor",region);
		List regionPoints = region.getPoints();
		for(int i=0;i<m_points.size();i++){
			Point pointA = (Point)m_points.get(i);
			Point pointB = (Point)m_points.get((i+1)%m_points.size());
			for(int j=0;j<regionPoints.size();j++){
				Point testPoint = (Point)regionPoints.get(j);
				Point otherTestPoint = (Point)regionPoints.get((j+1)%regionPoints.size());
				if(!RiffPolygonToolbox.testForColinearity(pointA, pointB, testPoint, otherTestPoint)){continue;}
				if(!RiffPolygonToolbox.getBoundingRectIntersection(pointA, pointB, testPoint, otherTestPoint)){continue;}
				region.addNeighbor(this);
				m_neighbors.add(region);
			}
		}
		assert Debugger.closeNode();
	}
	public void removeRegionNeighbor(DiscreteRegion region){
		m_neighbors.remove(region);
	}
	public void recheckNeighbors(){
		Set<DiscreteRegion>neighbors=new HashSet<DiscreteRegion>(m_neighbors);
		m_neighbors.clear();
		addRegionNeighbors(neighbors);
	}
	public void addRegionNeighbors(Collection<DiscreteRegion>regions){
		for(DiscreteRegion region:regions){addRegionNeighbor(region);}
	}
	public boolean equals(Object o){
		DiscreteRegion otherRegion = (DiscreteRegion)o;
		List<Point>pointList=otherRegion.getPoints();
		return(m_points.containsAll(pointList)&&pointList.containsAll(m_points));
	}
	public int hashCode(){
		return m_points.hashCode();
	}
	// ScriptConvertible implementation
	public Object convert(){
		FauxTemplate_DiscreteRegion region=new FauxTemplate_DiscreteRegion(getEnvironment(),getEnvironment().getTemplate(FauxTemplate_DiscreteRegion.DISCRETEREGIONSTRING).getType());
		region.setRegion(this);
		return region;
	}
	// Nodeable implementation
	public boolean nodificate(){
		assert Debugger.openNode("Discrete Region ("+m_points.size()+" point(s))");
		assert Debugger.addSnapNode("Points ("+m_points.size()+" point(s))",m_points);
		if(m_properties.size()==1){
			assert Debugger.addSnapNode("Property Map (1 property)",m_properties);
		}else{
			assert Debugger.addSnapNode("Property Map ("+m_properties.size()+" properties)",m_properties);
		}
		assert Debugger.openNode("Details");
		assert Debugger.addNode("Script environment reference: "+m_environment);
		assert Debugger.openNode("Bounds");
		assert Debugger.addNode("Left bound: " + m_leftExtreme);
		assert Debugger.addNode("Right bound: " + m_rightExtreme);
		assert Debugger.addNode("Top bound: " + m_topExtreme);
		assert Debugger.addNode("Bottom bound: " + m_bottomExtreme);
		assert Debugger.closeNode();
		assert Debugger.addNode("Version: "+m_version);
		assert Debugger.addNode("Optimized: "+m_isOptimized);
		if(m_neighbors!=null&&m_neighbors.size()>0){
			assert Debugger.openNode("Neighbors ("+m_neighbors.size()+ " neighbor(s))");
			for(DiscreteRegion region:m_neighbors){
				assert Debugger.openNode("Discrete Region ("+region.getPoints().size()+" point(s))");
				assert Debugger.addSnapNode("Points ("+region.getPoints().size()+" point(s))",m_points);
				if(m_properties.size()==1){
					assert Debugger.addSnapNode("Property Map (1 property)",region.getProperties());
				}else{
					assert Debugger.addSnapNode("Property Map ("+region.getProperties().size()+" properties)",region.getProperties());
				}
				assert Debugger.closeNode();
			}
			assert Debugger.closeNode();
		}
		assert Debugger.addSnapNode("Intersection Map",m_intersectionMap);
		assert Debugger.addNode("Mid-point: "+m_midPoint);
		assert Debugger.addNode("Interior point: "+m_interiorPoint);
		assert Debugger.closeNode();
		assert Debugger.closeNode();
		return true;
	}
}
