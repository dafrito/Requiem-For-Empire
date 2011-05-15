import javax.swing.*; // For JPanel, etc.
import java.awt.*;           // For Graphics, etc.
import java.awt.geom.*;      // For Ellipse2D, etc.
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import javax.swing.JOptionPane;
import java.lang.Math;

//public Brand(String name, Commodity commodity, double quality)
//public Lot(Brand brand, double quality, double quantity)
//public AssetPoint(RiffDataPoint point, Planet planet, Asset asset)
//public RiffSpherePoint(Location referenceLocation, double longitude, double latitude)
//public Terrain(double brushDensity, double elevation, double temperature, double cohesion, double waterDepth)
//public LinearGradient(RiffDataPoint focus, double radius, double exponent) throws ZeroRadiusException

public class RiffPolygonTesterGUI extends JPanel implements KeyListener, MouseListener{
	private DiscreteRegion m_region;
	public RiffPolygonTesterGUI(){
			RiffToolbox.createRegion(m_region, 4);
			System.out.println(m_region.getPoints().size());
			addMouseListener(this);
			addKeyListener(this);
	}
	public void paintComponent(Graphics g){
		clear(g);
		Graphics2D g2d = (Graphics2D)g;
		System.out.println("********new iteration********");
		for(int i=0;i<m_region.getPoints().size();i++){
			g2d.drawString(((RiffAbsolutePoint)m_region.getPoints().get(i)).getName(),(int)((RiffAbsolutePoint)m_region.getPoints().get(i)).getX(),(int)((RiffAbsolutePoint)m_region.getPoints().get(i)).getY());	
		}
		java.util.List lineList = RiffGraphicsToolbox.getLineListFromDiscreteRegion(m_region);
		for(int i=0;i<lineList.size();i++){
			if(i%2 == 0){g2d.setColor(Color.RED);}
			if(i%2 == 1){g2d.setColor(Color.BLUE);}
			g2d.drawLine((int)((Line2D.Double)lineList.get(i)).getX1(),(int)((Line2D.Double)lineList.get(i)).getY1(),(int)((Line2D.Double)lineList.get(i)).getX2(),(int)((Line2D.Double)lineList.get(i)).getY2());
		}
		java.util.List polyList = RiffPolygonToolbox.convertPolyToConvex(m_region);
		polyList = RiffPolygonToolbox.convertPolyToConvex(m_region);
		polyList = RiffPolygonToolbox.joinPolygons(polyList);
		System.out.println(polyList.size());
		for(int q=0;q<polyList.size();q++){
			lineList = RiffGraphicsToolbox.getLineListFromDiscreteRegion((DiscreteRegion)polyList.get(q));
			for(int i=0;i<((DiscreteRegion)polyList.get(q)).getPoints().size();i++){
				g2d.drawString(((RiffAbsolutePoint)((DiscreteRegion)polyList.get(q)).getPoints().get(i)).getName(),(int)((RiffAbsolutePoint)((DiscreteRegion)polyList.get(q)).getPoints().get(i)).getX()+300,(int)((RiffAbsolutePoint)((DiscreteRegion)polyList.get(q)).getPoints().get(i)).getY());	
			}
			for(int i=0;i<lineList.size();i++){
				if(i%2 == 0){g2d.setColor(Color.RED);}
				if(i%2 == 1){g2d.setColor(Color.BLUE);}
				g2d.drawLine((int)((Line2D.Double)lineList.get(i)).getX1()+300,(int)((Line2D.Double)lineList.get(i)).getY1(),(int)((Line2D.Double)lineList.get(i)).getX2()+300,(int)((Line2D.Double)lineList.get(i)).getY2());
			}
		}
	}
	private int m_innerPolygonIterator=0;
	private int m_fullPolygonIterator=0;
	public void keyTyped(KeyEvent keyEvent){}
	public void keyReleased(KeyEvent keyEvent){}
	public void keyPressed(KeyEvent keyEvent){}
	public void mouseClicked(MouseEvent e){System.out.println(e.getPoint());}
	public void mousePressed(MouseEvent e){}
	public void mouseReleased(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	protected void clear(Graphics g){super.paintComponent(g);}
  	public static void main(String[] args) {
	 	JFrame frame = new JFrame("RiffPolygonTesterGUI");
	 	frame.setSize(1400, 360);
		frame.setContentPane(new RiffPolygonTesterGUI());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
