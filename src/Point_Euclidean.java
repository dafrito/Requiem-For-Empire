public class Point_Euclidean extends Point implements ScriptConvertible {
	private double m_x, m_y, m_z;
	private static int m_pointNum = 0;

	public Point_Euclidean(ScriptEnvironment env, double x, double y, double z) {
		this(env, null, x, y, z);
	}

	public Point_Euclidean(ScriptEnvironment env, String name, double x, double y, double z) {
		super(env, name);
		m_pointNum++;
		this.m_x = x;
		this.m_y = y;
		this.m_z = z;
	}

	public void addX(double x) {
		this.m_x += x;
	}

	public void addY(double y) {
		this.m_y += y;
	}

	public void addZ(double z) {
		this.m_z += z;
	}

	// ScriptConvertible implementation
	@Override
	public Object convert() {
		FauxTemplate_Point point = new FauxTemplate_Point(this.getEnvironment());
		point.setPoint(this);
		return point;
	}

	// Object overloading
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Point_Euclidean)) {
			return false;
		}
		Point_Euclidean testPoint = (Point_Euclidean) o;
		return (RiffToolbox.areEqual(Point.System.EUCLIDEAN, this.m_x, testPoint.getX()) && RiffToolbox.areEqual(Point.System.EUCLIDEAN, this.m_y, testPoint.getY()) && RiffToolbox.areEqual(Point.System.EUCLIDEAN, this.m_z, testPoint.getZ()));
	}

	@Override
	public Point.System getSystem() {
		return Point.System.EUCLIDEAN;
	}

	// Point implementation
	@Override
	public double getX() {
		return this.m_x;
	}

	@Override
	public double getY() {
		return this.m_y;
	}

	@Override
	public double getZ() {
		return this.m_z;
	}

	@Override
	public void setX(double x) {
		this.m_x = x;
	}

	@Override
	public void setY(double y) {
		this.m_y = y;
	}

	@Override
	public void setZ(double z) {
		this.m_z = z;
	}

	@Override
	public String toString() {
		String string = new String();
		string += "(" + this.m_x;
		string += ", " + this.m_y;
		string += ", " + this.m_z + ")";
		return string;
	}
}
