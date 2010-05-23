package com.dafrito.geom;

import com.dafrito.debug.Exception_InternalError;
import com.dafrito.script.ScriptEnvironment;

public abstract class Point{
	private String m_name;
	private ScriptEnvironment m_environment;
	public enum System{EUCLIDEAN,SPHERICAL}
	public Point(ScriptEnvironment env,String name){
		this.m_environment=env;
		this.m_name=name;
	}
	public ScriptEnvironment getEnvironment(){return this.m_environment;}
	public String getName(){return this.m_name;}
	public void setName(String name){this.m_name=name;}
	public void translate(double x, double y, double z){
		setX(getX()+x);
		setY(getY()+y);
		setZ(getZ()+z);
	}
	public void setPosition(Point point){
		setX(point.getX());
		setY(point.getY());
		setZ(point.getZ());
	}
	public void setPosition(double x, double y, double z){
		setX(x);
		setY(y);
		setZ(z);
	}
	public static Point createPoint(Point reference,double x,double y,double z){
		switch(reference.getSystem()){
		case EUCLIDEAN:return new Point_Euclidean(reference.getEnvironment(),x,y,z);
		case SPHERICAL:return new Point_Spherical(reference.getEnvironment(),x,y,z);
		}
		throw new Exception_InternalError("Invalid default");
	}
	public abstract double getX();
	public abstract double getY();
	public abstract double getZ();
	public abstract void setX(double x);
	public abstract void setY(double y);
	public abstract void setZ(double z);
	public abstract Point.System getSystem();
}
