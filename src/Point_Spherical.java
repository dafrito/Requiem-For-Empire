public class Point_Spherical extends Point {
	public static final double LATITUDEMAXIMUM = 180;
	public static final double LONGITUDEMAXIMUM = 360;
	private double m_latitude;
	private double m_longitude;
	private double m_magnitude;

	public Point_Spherical(ScriptEnvironment env, double longitude, double latitude, double magnitude) {
		this(env, null, longitude, latitude, magnitude);
	}

	public Point_Spherical(ScriptEnvironment env, String name, double longitude, double latitude, double magnitude) {
		super(env, name);
		this.m_longitude = longitude % LONGITUDEMAXIMUM;
		this.m_latitude = latitude % LATITUDEMAXIMUM;
		this.m_magnitude = magnitude;
	}

	// Object overloading
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Point_Spherical)) {
			return false;
		}
		Point_Spherical point = ((Point_Spherical) o);
		if (RiffToolbox.areEqual(Point.System.EUCLIDEAN, this.getY(), 90.0d) && RiffToolbox.areEqual(Point.System.EUCLIDEAN, point.getY(), 90.0d)) {
			return true;
		}
		if (RiffToolbox.areEqual(Point.System.EUCLIDEAN, this.getY(), -90.0d) && RiffToolbox.areEqual(Point.System.EUCLIDEAN, point.getY(), -90.0d)) {
			return true;
		}
		return RiffToolbox.areEqual(Point.System.SPHERICAL, this.getX(), point.getX()) && RiffToolbox.areEqual(Point.System.SPHERICAL, this.getY(), point.getY());
	}

	public double getLatitudeDegrees() {
		return this.m_latitude;
	}

	public double getLatitudeRadians() {
		return Math.toRadians(this.m_latitude);
	}

	// Degrees
	public double getLongitudeDegrees() {
		return this.m_longitude;
	}

	// Radians
	public double getLongitudeRadians() {
		return Math.toRadians(this.m_longitude);
	}

	@Override
	public Point.System getSystem() {
		return Point.System.EUCLIDEAN;
	}

	// Point implementation
	@Override
	public double getX() {
		return this.getLongitudeDegrees();
	}

	@Override
	public double getY() {
		return this.getLatitudeDegrees();
	}

	@Override
	public double getZ() {
		return this.m_magnitude;
	}

	public void setLatitudeDegrees(double latitude) {
		this.m_latitude = latitude % LATITUDEMAXIMUM;
	}

	public void setLatitudeRadians(double latitude) {
		this.m_latitude = Math.toDegrees(latitude);
		;
	}

	public void setLongitudeDegrees(double longitude) {
		this.m_longitude = longitude % LONGITUDEMAXIMUM;
	}

	public void setLongitudeRadians(double longitude) {
		this.m_longitude = Math.toDegrees(longitude);
	}

	@Override
	public void setX(double x) {
		this.setLongitudeDegrees(x);
	}

	@Override
	public void setY(double y) {
		this.setLatitudeDegrees(y);
	}

	@Override
	public void setZ(double z) {
		this.m_magnitude = z;
	}

	@Override
	public String toString() {
		String string = new String();
		if (this.getName() != null) {
			string += this.getName();
		}
		string += "(" + this.m_longitude + " degrees longitude, " + this.m_latitude + " degrees latitude, " + this.m_magnitude + ")";
		return string;
	}
}
