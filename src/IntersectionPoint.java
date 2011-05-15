public class IntersectionPoint implements Nodeable {
	private Point m_point;
	private boolean m_isTangent;

	public IntersectionPoint(Point point, boolean isTangent) {
		this.m_isTangent = isTangent;
		this.m_point = point;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof IntersectionPoint)) {
			return false;
		}
		return this.getPoint().equals(((IntersectionPoint) o).getPoint());
	}

	public Point getPoint() {
		return this.m_point;
	}

	public boolean isTangent() {
		return this.m_isTangent;
	}

	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Intersection Point");
		assert Debugger.addNode("Point: " + this.m_point);
		assert Debugger.addNode("Tangent: " + this.m_isTangent);
		assert Debugger.closeNode();
		return true;
	}
}
