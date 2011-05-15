public class Point_Euclidean extends Point implements ScriptConvertible {
	private double x, y, z;
	private static int pointNum = 0;

	public Point_Euclidean(ScriptEnvironment env, double x, double y, double z) {
		this(env, null, x, y, z);
	}

	public Point_Euclidean(ScriptEnvironment env, String name, double x, double y, double z) {
		super(env, name);
		pointNum++;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void addX(double x) {
		this.x += x;
	}

	public void addY(double y) {
		this.y += y;
	}

	public void addZ(double z) {
		this.z += z;
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
		return (RiffToolbox.areEqual(Point.System.EUCLIDEAN, this.x, testPoint.getX()) && RiffToolbox.areEqual(Point.System.EUCLIDEAN, this.y, testPoint.getY()) && RiffToolbox.areEqual(Point.System.EUCLIDEAN, this.z, testPoint.getZ()));
	}

	@Override
	public Point.System getSystem() {
		return Point.System.EUCLIDEAN;
	}

	// Point implementation
	@Override
	public double getX() {
		return this.x;
	}

	@Override
	public double getY() {
		return this.y;
	}

	@Override
	public double getZ() {
		return this.z;
	}

	@Override
	public void setX(double x) {
		this.x = x;
	}

	@Override
	public void setY(double y) {
		this.y = y;
	}

	@Override
	public void setZ(double z) {
		this.z = z;
	}

	@Override
	public String toString() {
		String string = new String();
		string += "(" + this.x;
		string += ", " + this.y;
		string += ", " + this.z + ")";
		return string;
	}
}
