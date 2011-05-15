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

public class RiffGUI extends JPanel implements KeyListener{
	private Planet m_planet;
	public RiffGUI() throws InvalidScenarioDateRangeException{
		try{
			GregorianCalendar startDate = new GregorianCalendar(2100, Calendar.MARCH, 1, 5, 25, 00);
			GregorianCalendar endDate = new GregorianCalendar(2200, Calendar.DECEMBER, 25, 12, 00, 00);
			Scenario scenario = new Scenario("Roman Campaign", new Planet("Earth", "Terran", null, null, 6372.795), ((365/4)*3600*24)/(3600*24), startDate, endDate);
			m_planet = (Planet)scenario.getRootLocation();
			
			RiffCommodities.addCommodity(new Commodity("Widgets"));
			RiffCommodities.addCommodity(new Commodity("Widget Fluff"));
			RiffCommodities.getCommodity("Widgets").addPrerequisiteLevel("Widget Fluff", 30);
			RiffBrands.addBrand(new Brand("Generic Fluff", "Widget Fluff", .98));
			TappableAsset component = new TappableAsset(new Brand("Power Widgets", "Widgets", .85));
			new AssetPoint(new RiffSpherePoint(m_planet, 0,0), m_planet, component);
			new TerrainPoint(new LinearGradient(new RiffSpherePoint(m_planet, 30,30), 30, 1), m_planet, new Terrain(.5d, 38, 25, 1.0, 0.0));
			component.addAsset(new Lot("Generic Fluff", .75, 90));
			setFocusable(true);
			addKeyListener(this);
		}catch(OverwriteException ex){
			System.out.println(ex);
		}catch(CommodityMapException ex){
			System.out.println(ex);
		}catch(ZeroRadiusException ex){
			System.out.println(ex);
		}
	}
	public void paintComponent(Graphics g){
		clear(g);
		Graphics2D g2d = (Graphics2D)g;
		java.util.List list = m_planet.getTerrainPoints();
		for(int i=0;i<list.size();i++){
			java.util.List polyList = ((TerrainPoint)list.get(i)).getPolygons(.25);
			System.out.println(RiffPolygonToolbox.isPolygonConvex((DiscreteRegion)polyList.get(Math.abs(m_fullPolygonIterator % polyList.size()))));
			System.out.println("Splitting polygon:\n" + (DiscreteRegion)polyList.get(Math.abs(m_fullPolygonIterator % polyList.size())));
			polyList = RiffPolygonToolbox.convertPolyToConvex((DiscreteRegion)polyList.get(Math.abs(m_fullPolygonIterator % polyList.size())));
			int j=Math.abs(m_innerPolygonIterator % polyList.size());
			Polygon poly = new Polygon();
			System.out.println("Current polygon:\n " + polyList.get(j));
			java.util.List pointList = ((DiscreteRegion)polyList.get(j)).getPoints();
			for(int k=0;k<pointList.size();k++){
				poly.addPoint((int)((RiffSpherePoint)((RiffDataPoint)pointList.get(k)).getAbsolutePosition()).getLongitudeDegrees(), (int)((RiffSpherePoint)((RiffDataPoint)pointList.get(k)).getAbsolutePosition()).getLatitudeDegrees());
			}
			g2d.drawPolygon(poly);
		}
	}
	private int m_innerPolygonIterator=0;
	private int m_fullPolygonIterator=0;
	public void keyTyped(KeyEvent keyEvent){}
	public void keyReleased(KeyEvent keyEvent){}
	public void keyPressed(KeyEvent keyEvent){
		switch(keyEvent.getKeyCode()){
		case KeyEvent.VK_W:
			m_innerPolygonIterator++;
			break;
		case KeyEvent.VK_S:
			m_innerPolygonIterator--;
			break;
		case KeyEvent.VK_D:
			m_fullPolygonIterator++;
			m_innerPolygonIterator=0;
			break;
		case KeyEvent.VK_A:
			m_fullPolygonIterator--;
			m_innerPolygonIterator=0;
			break;
		}
		repaint();
	}
	protected void clear(Graphics g){super.paintComponent(g);}
	
  	public static void main(String[] args) {
	 	JFrame frame = new JFrame("RiffGUI");
	 	frame.setSize(720, 360);
		try{
			frame.setContentPane(new RiffGUI());
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
		}catch(InvalidScenarioDateRangeException ex){
			System.out.println(ex);
		}
		
	}
}
