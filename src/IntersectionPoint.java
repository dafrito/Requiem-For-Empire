public class IntersectionPoint implements Nodeable {
	private Point point;
	private boolean isTangent;

	public IntersectionPoint(Point point, boolean isTangent) {
		this.isTangent = isTangent;
		this.point = point;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof IntersectionPoint)) {
			return false;
		}
		return this.getPoint().equals(((IntersectionPoint) o).getPoint());
	}

	public Point getPoint() {
		return this.point;
	}

	public boolean isTangent() {
		return this.isTangent;
	}

	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Intersection Point");
		assert Debugger.addNode("Point: " + this.point);
		assert Debugger.addNode("Tangent: " + this.isTangent);
		assert Debugger.closeNode();
		return true;
	}
}
