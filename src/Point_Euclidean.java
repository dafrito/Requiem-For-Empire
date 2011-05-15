public class Point_Euclidean extends Point implements ScriptConvertible{
	private double m_x, m_y, m_z;
	private static int m_pointNum=0;
	public Point_Euclidean(ScriptEnvironment env,double x, double y, double z){this(env,null,x,y,z);}
	public Point_Euclidean(ScriptEnvironment env,String name,double x, double y, double z){
		super(env,name);
		m_pointNum++;
		m_x=x;
		m_y=y;
		m_z=z;
	}
	public void addX(double x){m_x+=x;}
	public void addY(double y){m_y+=y;}
	public void addZ(double z){m_z+=z;}
	// Point implementation
	public double getX(){return m_x;}
	public double getY(){return m_y;}
	public double getZ(){return m_z;}
	public void setX(double x){m_x=x;}
	public void setY(double y){m_y=y;}
	public void setZ(double z){m_z=z;}
	public Point.System getSystem(){return Point.System.EUCLIDEAN;}
	// ScriptConvertible implementation
	public Object convert(){
		FauxTemplate_Point point=new FauxTemplate_Point(getEnvironment());
		point.setPoint(this);
		return point;
	}
	// Object overloading
	public boolean equals(Object o){
		if(!(o instanceof Point_Euclidean)){return false;}
		Point_Euclidean testPoint=(Point_Euclidean)o;
		return(RiffToolbox.areEqual(Point.System.EUCLIDEAN, m_x,testPoint.getX())&&RiffToolbox.areEqual(Point.System.EUCLIDEAN, m_y,testPoint.getY())&&RiffToolbox.areEqual(Point.System.EUCLIDEAN, m_z,testPoint.getZ()));
	}
	public String toString(){
		String string = new String();
		string += "(" + m_x;
		string += ", " + m_y;
		string += ", " + m_z + ")";
		return string;
	}
}
