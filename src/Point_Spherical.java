import java.lang.Math;

public class Point_Spherical extends Point{
	public static final double LATITUDEMAXIMUM=180;
	public static final double LONGITUDEMAXIMUM=360;
	private double m_latitude;
	private double m_longitude;
	private double m_magnitude;
	public Point_Spherical(ScriptEnvironment env,double longitude, double latitude, double magnitude){
		this(env,null,longitude,latitude,magnitude);
	}
	public Point_Spherical(ScriptEnvironment env,String name, double longitude, double latitude, double magnitude){
		super(env,name);
		m_longitude=longitude%LONGITUDEMAXIMUM;
		m_latitude=latitude%LATITUDEMAXIMUM;
		m_magnitude=magnitude;
	}
	// Degrees
	public double getLongitudeDegrees(){return m_longitude;}
	public void setLongitudeDegrees(double longitude){m_longitude = longitude%LONGITUDEMAXIMUM;}
	public double getLatitudeDegrees(){return m_latitude;}
	public void setLatitudeDegrees(double latitude){m_latitude = latitude%LATITUDEMAXIMUM;}
	// Radians
	public double getLongitudeRadians(){return Math.toRadians(m_longitude);}
	public void setLongitudeRadians(double longitude){m_longitude = Math.toDegrees(longitude);}
	public double getLatitudeRadians(){return Math.toRadians(m_latitude);}
	public void setLatitudeRadians(double latitude){m_latitude = Math.toDegrees(latitude);;}
	// Point implementation
	public double getX(){return getLongitudeDegrees();}
	public double getY(){return getLatitudeDegrees();}
	public double getZ(){return m_magnitude;}
	public void setX(double x){setLongitudeDegrees(x);}
	public void setY(double y){setLatitudeDegrees(y);}
	public void setZ(double z){m_magnitude=z;}
	public Point.System getSystem(){return Point.System.EUCLIDEAN;}
	// Object overloading
	public boolean equals(Object o){
		if(!(o instanceof Point_Spherical)){return false;}
		Point_Spherical point=((Point_Spherical)o);
		if(RiffToolbox.areEqual(Point.System.EUCLIDEAN,getY(),90.0d)&&RiffToolbox.areEqual(Point.System.EUCLIDEAN,point.getY(),90.0d)){return true;}
		if(RiffToolbox.areEqual(Point.System.EUCLIDEAN,getY(),-90.0d)&&RiffToolbox.areEqual(Point.System.EUCLIDEAN,point.getY(),-90.0d)){return true;}
		return RiffToolbox.areEqual(Point.System.SPHERICAL,getX(),point.getX())&&RiffToolbox.areEqual(Point.System.SPHERICAL,getY(),point.getY());
	}
	public String toString(){
		String string = new String();
		if(getName()!=null){string += getName();}
		string += "(" + m_longitude + " degrees longitude, " + m_latitude + " degrees latitude, " + m_magnitude + ")";
		return string;
	}
}
