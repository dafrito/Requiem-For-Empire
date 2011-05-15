import java.util.LinkedList;
import java.util.List;

public class Gradient_Radial implements Gradient {
	private static final int m_polygonVertices = 4;
	private Krumflex m_krumflex;
	private double m_exponent, m_radius;
	private Point m_focus;

	public Gradient_Radial(Point focus, double radius, double exponent) {
		this.m_focus = focus;
		this.m_radius = radius;
		this.m_exponent = exponent;
	}

	@Override
	public double getBottomExtreme() {
		return this.getFocus().getY() - this.getRadius();
	}

	public double getExponent() {
		return this.m_exponent;
	}

	public Point getFocus() {
		return this.m_focus;
	}

	@Override
	public Krumflex getKrumflex() {
		return this.m_krumflex;
	}

	@Override
	public Krumflex getKrumflexAt(Point point) {
		double distance = RiffToolbox.getDistance(this.getFocus(), point);
		if (Math.abs(distance) > this.getRadius() || this.getExponent() == 0) {
			return this.getKrumflex().getKrumflexFromIntensity(0.0d);
		}
		return this.getKrumflex().getKrumflexFromIntensity(Math.abs(Math.pow(distance / this.getRadius(), this.getExponent()) - 1.0d));
	}

	@Override
	public double getLeftExtreme() {
		return this.getFocus().getX() - this.getRadius();
	}

	public double getRadius() {
		return this.m_radius;
	}

	@Override
	public List<DiscreteRegion> getRegions(double precision) {
		List<DiscreteRegion> list = new LinkedList<DiscreteRegion>();
		double radius = 0;
		DiscreteRegion lastRegion = null;
		for (double i = 1.0d; i > 0.0d; i -= precision) {
			assert Debugger.addNode("Entering sequence. i is at: " + i);
			radius += precision * this.getRadius();
			DiscreteRegion newRegion = new DiscreteRegion();
			newRegion.setProperty(this.getKrumflex().getName(), this.getKrumflex().getKrumflexFromIntensity(i));
			for (int j = 0; j < Gradient_Radial.m_polygonVertices; j++) {
				double radianOffset = ((Math.PI * 2) / m_polygonVertices) * j;
				double longOffset = Math.cos(radianOffset) * radius;
				double latOffset = Math.sin(radianOffset) * radius;
				newRegion.addPoint(Point.createPoint(this.getFocus(), this.getFocus().getX() + longOffset, this.getFocus().getY() + latOffset, 0.0d));
			}
			if (lastRegion == null) {
				list.add(newRegion);
				lastRegion = newRegion;
			} else {
				DiscreteRegion fullRegion = new DiscreteRegion();
				for (int q = 0; q <= lastRegion.getPoints().size() / 2; q++) {
					fullRegion.getPoints().add(lastRegion.getPoints().get(q));
				}
				for (int q = 0; q <= newRegion.getPoints().size() / 2; q++) {
					fullRegion.getPoints().add(newRegion.getPoints().get((newRegion.getPoints().size() / 2) - q));
				}
				lastRegion = newRegion;
				list.add(fullRegion);
			}
		}
		return list;
	}

	@Override
	public double getRightExtreme() {
		return this.getFocus().getX() + this.getRadius();
	}

	@Override
	public double getTopExtreme() {
		return this.getFocus().getY() + this.getRadius();
	}

	// Gradient implementation
	@Override
	public void setKrumflex(Krumflex krumflex) {
		this.m_krumflex = krumflex;
	}
}
