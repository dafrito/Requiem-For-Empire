import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.List;

public class GraphicalElement_Line extends InterfaceElement implements ScriptConvertible, Nodeable {
	private Point pointA, pointB;
	private ScriptEnvironment environment;

	public GraphicalElement_Line(ScriptEnvironment env, Point pointA, Point pointB) {
		super(env, null, null);
		this.pointA = pointA;
		this.pointB = pointB;
	}

	// ScriptConvertible and Nodeable implementations
	@Override
	public Object convert() {
		FauxTemplate_Line line = new FauxTemplate_Line(this.getEnvironment());
		line.setPointA(this.pointA);
		line.setPointB(this.pointB);
		return line;
	}

	@Override
	public Rectangle getDrawingBounds() {
		return new Rectangle(this.getXAnchor() + this.getLeftMarginMagnitude() + this.getLeftBorderMagnitude(), this.getYAnchor() + this.getTopMarginMagnitude() + this.getTopBorderMagnitude(), this.getInternalWidth(), this.getInternalHeight());
	}

	@Override
	public boolean isFocusable() {
		return false;
	}

	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Line Graphical Element");
		assert Debugger.addNode("First point: " + this.pointA);
		assert Debugger.addNode("Second point: " + this.pointB);
		assert Debugger.closeNode();
		return true;
	}

	@Override
	public void paint(Graphics2D g2d) {
		assert Debugger.openNode("Line Painting Operations", "Painting Line Element");
		assert Debugger.addNode(this);
		Point offset;
		if (this.getParent() instanceof InterfaceElement_Panel) {
			offset = ((InterfaceElement_Panel) this.getParent()).getOffset();
		} else {
			offset = new Point_Euclidean(this.getEnvironment(), this.getXAnchor(), this.getYAnchor(), 0.0d);
		}
		// Offset translation
		assert this.pointA != null : "Point A is null in LineElement";
		assert this.pointB != null : "Point B is null in LineElement";
		assert offset != null : "Offset point is null in LineElement";
		double ax = this.pointA.getX() - offset.getX();
		double ay = this.pointA.getY() - offset.getY();
		double bx = this.pointB.getX() - offset.getX();
		double by = this.pointB.getY() - offset.getY();
		// Orthographic zoom
		ax = ax * Math.pow(2, offset.getZ());
		ay = ay * Math.pow(2, offset.getZ());
		bx = bx * Math.pow(2, offset.getZ());
		by = by * Math.pow(2, offset.getZ());
		// Converstion to screen coordinates
		assert this.getParent() != null;
		assert this.getParent().getContainerElement() != null;
		assert this.getParent().getContainerElement().getDrawingBounds() != null;
		double width = (this.getParent().getContainerElement().getDrawingBounds().getWidth() - this.getParent().getContainerElement().getDrawingBounds().getX()) / 2;
		double height = (this.getParent().getContainerElement().getDrawingBounds().getHeight() - this.getParent().getContainerElement().getDrawingBounds().getY()) / 2;
		// Draw transformed line
		Point translatedPointA = new Point_Euclidean(this.getEnvironment(), ax + this.getParent().getContainerElement().getDrawingBounds().getX() + width, ay + this.getParent().getContainerElement().getDrawingBounds().getY() + height, 0.0d);
		Point translatedPointB = new Point_Euclidean(this.getEnvironment(), bx + this.getParent().getContainerElement().getDrawingBounds().getX() + width, by + this.getParent().getContainerElement().getDrawingBounds().getY() + height, 0.0d);
		DiscreteRegion region = RiffJavaToolbox.convertToRegion(this.getEnvironment(), this.getParent().getContainerElement().getDrawingBounds());
		List<RiffIntersectionPoint> intersections = RiffPolygonToolbox.getIntersections(translatedPointA, translatedPointB, region);
		if (intersections.size() == 0 && !RiffPolygonToolbox.getBoundingRectIntersection(translatedPointA, translatedPointA, region) && !RiffPolygonToolbox.getBoundingRectIntersection(translatedPointB, translatedPointB, region)) {
			return;
		}
		if (intersections.size() == 2) {
			translatedPointA = intersections.get(0).getIntersection();
			translatedPointB = intersections.get(1).getIntersection();
		} else if (intersections.size() == 1) {
			if (RiffPolygonToolbox.getBoundingRectIntersection(translatedPointA, translatedPointA, region)) {
				translatedPointB = intersections.get(0).getIntersection();
			} else {
				translatedPointA = intersections.get(0).getIntersection();
			}
		}
		assert Debugger.addNode("Translated first point: " + translatedPointA);
		assert Debugger.addNode("Translated second point: " + translatedPointB);
		g2d.draw(new java.awt.geom.Line2D.Double(translatedPointA.getX(), translatedPointA.getY(), translatedPointB.getX(), translatedPointB.getY()));
		assert Debugger.closeNode();
	}

	public void setPointA(Point point) {
		this.pointA = point;
	}

	public void setPointB(Point point) {
		this.pointB = point;
	}
}
