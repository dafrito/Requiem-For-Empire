public abstract class Point {
	public enum System {
		EUCLIDEAN, SPHERICAL
	}

	public static Point createPoint(Point reference, double x, double y, double z) {
		switch (reference.getSystem()) {
		case EUCLIDEAN:
			return new Point_Euclidean(reference.getEnvironment(), x, y, z);
		case SPHERICAL:
			return new Point_Spherical(reference.getEnvironment(), x, y, z);
		}
		throw new Exception_InternalError("Invalid default");
	}

	private String m_name;

	private ScriptEnvironment m_environment;

	public Point(ScriptEnvironment env, String name) {
		m_environment = env;
		m_name = name;
	}

	public ScriptEnvironment getEnvironment() {
		return m_environment;
	}

	public String getName() {
		return m_name;
	}

	public abstract Point.System getSystem();

	public abstract double getX();

	public abstract double getY();

	public abstract double getZ();

	public void setName(String name) {
		m_name = name;
	}

	public void setPosition(double x, double y, double z) {
		setX(x);
		setY(y);
		setZ(z);
	}

	public void setPosition(Point point) {
		setX(point.getX());
		setY(point.getY());
		setZ(point.getZ());
	}

	public abstract void setX(double x);

	public abstract void setY(double y);

	public abstract void setZ(double z);

	public void translate(double x, double y, double z) {
		setX(getX() + x);
		setY(getY() + y);
		setZ(getZ() + z);
	}
}
