package com.dafrito.geom;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Nodeable;

public class IntersectionPoint implements Nodeable{
	private Point m_point;
	private boolean m_isTangent;
	public IntersectionPoint(Point point, boolean isTangent){
		this.m_isTangent=isTangent;
		this.m_point=point;
	}
	public Point getPoint(){return this.m_point;}
	public boolean isTangent(){return this.m_isTangent;}
	public boolean nodificate(){
		assert LegacyDebugger.open("Intersection Point");
		assert LegacyDebugger.addNode("Point: "+this.m_point);
		assert LegacyDebugger.addNode("Tangent: "+this.m_isTangent);
		assert LegacyDebugger.close();
		return true;
	}
	@Override
    public boolean equals(Object o){
		if(!(o instanceof IntersectionPoint)){return false;}
		return getPoint().equals(((IntersectionPoint)o).getPoint());
	}
}
