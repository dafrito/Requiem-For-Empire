public class IntersectionPoint implements Nodeable {
	private Point m_point;
	private boolean m_isTangent;

	public IntersectionPoint(Point point, boolean isTangent) {
		m_isTangent = isTangent;
		m_point = point;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof IntersectionPoint)) {
			return false;
		}
		return getPoint().equals(((IntersectionPoint) o).getPoint());
	}

	public Point getPoint() {
		return m_point;
	}

	public boolean isTangent() {
		return m_isTangent;
	}

	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Intersection Point");
		assert Debugger.addNode("Point: " + m_point);
		assert Debugger.addNode("Tangent: " + m_isTangent);
		assert Debugger.closeNode();
		return true;
	}
}
