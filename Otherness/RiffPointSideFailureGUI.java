import javax.swing.*; // For JPanel, etc.
import java.awt.*;           // For Graphics, etc.
import java.awt.geom.*;      // For Ellipse2D, etc.
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.*;

public class RiffPointSideFailureGUI extends JPanel{
	private RiffAbsolutePoint m_pointA, m_pointB;
	private DiscreteRegion m_region;
	public RiffPointSideFailureGUI(){
		RiffToolbox.toggleDebugSpew();
		java.util.Set regions = RiffToolbox.getDiscreteRegionsFromFile("pointSideDump.txt");
		Iterator iter = regions.iterator();
		m_region = (DiscreteRegion)iter.next();
		java.util.List list = ((DiscreteRegion)iter.next()).getPoints();
		m_pointA = (RiffAbsolutePoint)list.get(0);
		m_pointB = (RiffAbsolutePoint)list.get(1);
		System.out.println(RiffPolygonToolbox.getPointSideList(m_region, m_pointA, m_pointB));
	}
	public void paintComponent(Graphics g){
		clear(g);
		Graphics2D g2d = (Graphics2D)g;
		g2d.setColor(Color.BLACK);
		g2d.drawLine((int)m_pointA.getX(), getHeight()-(int)m_pointA.getY(), (int)m_pointB.getX(), getHeight()-(int)m_pointB.getY());
		java.util.List pointList = m_region.getPoints();
		for(int j=0;j<pointList.size();j++){
			if(((RiffAbsolutePoint)pointList.get(j)).getName()!=null){
				g2d.drawString(((RiffAbsolutePoint)pointList.get(j)).getName(),(int)((RiffAbsolutePoint)pointList.get(j)).getX(),getHeight()-(int)((RiffAbsolutePoint)pointList.get(j)).getY());
			}
		}
		g2d.setColor(new Color(.25f, .25f + RiffToolbox.getRandom().nextFloat()%.75f, .25f + RiffToolbox.getRandom().nextFloat()%.75f, .25f + RiffToolbox.getRandom().nextFloat()%.75f));
		g2d.fillPolygon(RiffGraphicsToolbox.getPolygonFromDiscreteRegion(this, m_region));
	}
	protected void clear(Graphics g){super.paintComponent(g);}
	public static void main(String[] args) {
	 	JFrame frame = new JFrame("RiffPointSideFailureGUI");
	 	frame.setSize(500, 500);
		frame.setContentPane(new RiffPointSideFailureGUI());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
