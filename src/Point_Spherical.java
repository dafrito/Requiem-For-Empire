
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
		m_longitude = longitude % LONGITUDEMAXIMUM;
		m_latitude = latitude % LATITUDEMAXIMUM;
		m_magnitude = magnitude;
	}

	// Object overloading
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Point_Spherical)) {
			return false;
		}
		Point_Spherical point = ((Point_Spherical) o);
		if (RiffToolbox.areEqual(Point.System.EUCLIDEAN, getY(), 90.0d) && RiffToolbox.areEqual(Point.System.EUCLIDEAN, point.getY(), 90.0d)) {
			return true;
		}
		if (RiffToolbox.areEqual(Point.System.EUCLIDEAN, getY(), -90.0d) && RiffToolbox.areEqual(Point.System.EUCLIDEAN, point.getY(), -90.0d)) {
			return true;
		}
		return RiffToolbox.areEqual(Point.System.SPHERICAL, getX(), point.getX()) && RiffToolbox.areEqual(Point.System.SPHERICAL, getY(), point.getY());
	}

	public double getLatitudeDegrees() {
		return m_latitude;
	}

	public double getLatitudeRadians() {
		return Math.toRadians(m_latitude);
	}

	// Degrees
	public double getLongitudeDegrees() {
		return m_longitude;
	}

	// Radians
	public double getLongitudeRadians() {
		return Math.toRadians(m_longitude);
	}

	@Override
	public Point.System getSystem() {
		return Point.System.EUCLIDEAN;
	}

	// Point implementation
	@Override
	public double getX() {
		return getLongitudeDegrees();
	}

	@Override
	public double getY() {
		return getLatitudeDegrees();
	}

	@Override
	public double getZ() {
		return m_magnitude;
	}

	public void setLatitudeDegrees(double latitude) {
		m_latitude = latitude % LATITUDEMAXIMUM;
	}

	public void setLatitudeRadians(double latitude) {
		m_latitude = Math.toDegrees(latitude);
		;
	}

	public void setLongitudeDegrees(double longitude) {
		m_longitude = longitude % LONGITUDEMAXIMUM;
	}

	public void setLongitudeRadians(double longitude) {
		m_longitude = Math.toDegrees(longitude);
	}

	@Override
	public void setX(double x) {
		setLongitudeDegrees(x);
	}

	@Override
	public void setY(double y) {
		setLatitudeDegrees(y);
	}

	@Override
	public void setZ(double z) {
		m_magnitude = z;
	}

	@Override
	public String toString() {
		String string = new String();
		if (getName() != null) {
			string += getName();
		}
		string += "(" + m_longitude + " degrees longitude, " + m_latitude + " degrees latitude, " + m_magnitude + ")";
		return string;
	}
}
