import java.lang.Math;
import java.util.*;
public class Gradient_Radial implements Gradient{
	private static final int m_polygonVertices=4;
	private Krumflex m_krumflex;
	private double m_exponent,m_radius;
	private Point m_focus;
	public Gradient_Radial(Point focus,double radius,double exponent){
		m_focus=focus;
		m_radius=radius;
		m_exponent=exponent;
	}
	public Point getFocus(){return m_focus;}
	public double getRadius(){return m_radius;}
	public double getExponent(){return m_exponent;}
	// Gradient implementation
	public void setKrumflex(Krumflex krumflex){m_krumflex=krumflex;}
	public Krumflex getKrumflex(){return m_krumflex;}
	public Krumflex getKrumflexAt(Point point){
		double distance=RiffToolbox.getDistance(getFocus(),point);
		if(Math.abs(distance)>getRadius()||getExponent()==0){return getKrumflex().getKrumflexFromIntensity(0.0d);}
		return getKrumflex().getKrumflexFromIntensity(Math.abs(Math.pow(distance /getRadius(),getExponent())-1.0d));
	}
	public List<DiscreteRegion>getRegions(double precision){
		List<DiscreteRegion>list=new LinkedList<DiscreteRegion>();
		double radius=0;
		DiscreteRegion lastRegion=null;
		for(double i=1.0d;i>0.0d;i-=precision){
			assert Debugger.addNode("Entering sequence. i is at: " + i);
			radius+=precision*getRadius();
			DiscreteRegion newRegion=new DiscreteRegion();
			newRegion.setProperty(getKrumflex().getName(),getKrumflex().getKrumflexFromIntensity(i));
			for(int j=0;j<Gradient_Radial.m_polygonVertices;j++){
				double radianOffset = ((Math.PI*2)/m_polygonVertices)*j;
				double longOffset = Math.cos(radianOffset)*radius;
				double latOffset = Math.sin(radianOffset)*radius;
				newRegion.addPoint(Point.createPoint(getFocus(),getFocus().getX()+longOffset,getFocus().getY()+latOffset,0.0d));				
			}
			if(lastRegion==null){
				list.add(newRegion);
				lastRegion=newRegion;
			}else{
				DiscreteRegion fullRegion=new DiscreteRegion();
				for(int q=0;q<=lastRegion.getPoints().size()/2;q++){
					fullRegion.getPoints().add(lastRegion.getPoints().get(q));
				}
				for(int q=0;q<=newRegion.getPoints().size()/2;q++){
					fullRegion.getPoints().add(newRegion.getPoints().get((newRegion.getPoints().size()/2)- q));
				}
				lastRegion=newRegion;
				list.add(fullRegion);
			}
		}
		return list;
	}
	public double getLeftExtreme(){return getFocus().getX()-getRadius();}
	public double getRightExtreme(){return getFocus().getX()+getRadius();}
	public double getTopExtreme(){return getFocus().getY()+getRadius();}
	public double getBottomExtreme(){return getFocus().getY()-getRadius();}
}
