import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.List;
public class GraphicalElement_Line extends InterfaceElement implements ScriptConvertible,Nodeable{
	private Point m_pointA,m_pointB;
	private ScriptEnvironment m_environment;
	public GraphicalElement_Line(ScriptEnvironment env,Point pointA, Point pointB){
		super(env,null,null);
		m_pointA=pointA;
		m_pointB=pointB;
	}
	public void setPointA(Point point){m_pointA=point;}
	public void setPointB(Point point){m_pointB=point;}
	public Rectangle getDrawingBounds(){
		return new Rectangle(getXAnchor()+getLeftMarginMagnitude()+getLeftBorderMagnitude(),getYAnchor()+getTopMarginMagnitude()+getTopBorderMagnitude(),getInternalWidth(),getInternalHeight());
	}
	public boolean isFocusable(){return false;}
	public void paint(Graphics2D g2d){
		assert Debugger.openNode("Line Painting Operations","Painting Line Element");
		assert Debugger.addNode(this);
		Point offset;
		if(getParent()instanceof InterfaceElement_Panel){
			offset=((InterfaceElement_Panel)getParent()).getOffset();
		}else{
			offset=new Point_Euclidean(getEnvironment(),getXAnchor(),getYAnchor(),0.0d);
		}
		// Offset translation
		assert m_pointA!=null:"Point A is null in LineElement";
		assert m_pointB!=null:"Point B is null in LineElement";
		assert offset!=null:"Offset point is null in LineElement";
		double ax=m_pointA.getX()-offset.getX();
		double ay=m_pointA.getY()-offset.getY();
		double bx=m_pointB.getX()-offset.getX();
		double by=m_pointB.getY()-offset.getY();
		// Orthographic zoom
		ax=ax*Math.pow(2,offset.getZ());
		ay=ay*Math.pow(2,offset.getZ());
		bx=bx*Math.pow(2,offset.getZ());
		by=by*Math.pow(2,offset.getZ());
		// Converstion to screen coordinates
		assert getParent()!=null;
		assert getParent().getContainerElement()!=null;
		assert getParent().getContainerElement().getDrawingBounds()!=null;
		double width=(getParent().getContainerElement().getDrawingBounds().getWidth()-getParent().getContainerElement().getDrawingBounds().getX())/2;
		double height=(getParent().getContainerElement().getDrawingBounds().getHeight()-getParent().getContainerElement().getDrawingBounds().getY())/2;
		// Draw transformed line
		Point translatedPointA=new Point_Euclidean(getEnvironment(),ax+getParent().getContainerElement().getDrawingBounds().getX()+width,ay+getParent().getContainerElement().getDrawingBounds().getY()+height,0.0d);
		Point translatedPointB=new Point_Euclidean(getEnvironment(),bx+getParent().getContainerElement().getDrawingBounds().getX()+width,by+getParent().getContainerElement().getDrawingBounds().getY()+height,0.0d);
		DiscreteRegion region=RiffJavaToolbox.convertToRegion(getEnvironment(),getParent().getContainerElement().getDrawingBounds());
		List<RiffIntersectionPoint>intersections=RiffPolygonToolbox.getIntersections(translatedPointA,translatedPointB,region);
		if(intersections.size()==0&&!RiffPolygonToolbox.getBoundingRectIntersection(translatedPointA,translatedPointA,region)&&!RiffPolygonToolbox.getBoundingRectIntersection(translatedPointB,translatedPointB,region)){return;}
		if(intersections.size()==2){
			translatedPointA=intersections.get(0).getIntersection();
			translatedPointB=intersections.get(1).getIntersection();
		}else if(intersections.size()==1){
			if(RiffPolygonToolbox.getBoundingRectIntersection(translatedPointA,translatedPointA,region)){
				translatedPointB=intersections.get(0).getIntersection();
			}else{
				translatedPointA=intersections.get(0).getIntersection();
			}
		}
		assert Debugger.addNode("Translated first point: " + translatedPointA);
		assert Debugger.addNode("Translated second point: " + translatedPointB);
		g2d.draw(new java.awt.geom.Line2D.Double(translatedPointA.getX(),translatedPointA.getY(),translatedPointB.getX(),translatedPointB.getY()));
		assert Debugger.closeNode();
	}
	// ScriptConvertible and Nodeable implementations
	public Object convert(){
		FauxTemplate_Line line=new FauxTemplate_Line(getEnvironment());
		line.setPointA(m_pointA);
		line.setPointB(m_pointB);
		return line;
	}
	public boolean nodificate(){
		assert Debugger.openNode("Line Graphical Element");
		assert Debugger.addNode("First point: "+m_pointA);
		assert Debugger.addNode("Second point: "+m_pointB);
		assert Debugger.closeNode();
		return true;
	}
}
