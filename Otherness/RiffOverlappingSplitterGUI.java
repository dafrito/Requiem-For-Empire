import javax.swing.*; // For JPanel, etc.
import java.awt.*;           // For Graphics, etc.
import java.awt.geom.*;      // For Ellipse2D, etc.
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import javax.swing.JOptionPane;
import java.lang.Math;
import java.io.*;

//public Brand(String name, Commodity commodity, double quality)
//public Lot(Brand brand, double quality, double quantity)
//public AssetPoint(RiffDataPoint point, Planet planet, Asset asset)
//public RiffSpherePoint(Location referenceLocation, double longitude, double latitude)
//public Terrain(double brushDensity, double elevation, double temperature, double cohesion, double waterDepth)
//public LinearGradient(RiffDataPoint focus, double radius, double exponent) throws ZeroRadiusException

public class RiffOverlappingSplitterGUI extends JPanel implements KeyListener, MouseListener{
	private java.util.List m_oldRegionList = new LinkedList();
	private java.util.List m_unsplitRegions = new LinkedList();
	private int m_polygonCycler;
	private boolean m_cyclePolygons, m_showLabels, m_drawMode,m_drawNeighborList;
	private DiscreteRegion m_drawingRegion;
	private SplitterThread m_splittingThread;
	private String m_statusText;
	private DiscreteRegionBSPNode m_root;
	private java.util.List m_pathList;
	private RiffAbsolutePoint m_sourcePoint;
	public RiffOverlappingSplitterGUI(){
		Set regions = RiffToolbox.getDiscreteRegionsFromFile("dump.txt");
		if(regions!=null&&!regions.isEmpty()){
			m_unsplitRegions.addAll(regions);
			m_splittingThread = new SplitterThread(this, m_root, regions, true);
			System.out.println("Now splitting polygons...");
			m_splittingThread.run();
		}
		m_statusText = "Awaiting drawing instructions.";
		m_showLabels=true;
		m_drawMode=true;
		setFocusable(true);
		addKeyListener(this);
		addMouseListener(this);
	}
	public void paintComponent(Graphics g){
		clear(g);
		if(m_splittingThread !=null){
			m_statusText = m_splittingThread.getStatusText();
			if(!m_splittingThread.isAlive()){
				m_root=m_splittingThread.getRoot();
				m_splittingThread=null;
			}
		}
	//	if(m_splittingThread!=null&&m_splittingThread.isAlive()){return;}
		Graphics2D g2d = (Graphics2D)g;
		if(m_splittingThread!=null){
			m_statusText = m_splittingThread.getStatusText();
			g2d.drawString(m_statusText , 5, getHeight()-5);
		}else{
			if(m_root!=null){g2d.drawString(m_statusText + "(Finished Region List Size: " + m_root.getPolyList().size() + ")", 5, getHeight()-5);}
		}
		if(m_root!=null){
			Set regions;
			if(m_drawNeighborList){
				regions = new HashSet();
				Iterator iter = m_root.getPolyList().iterator();
				while(iter.hasNext()){
					regions.addAll(((DiscreteRegion)iter.next()).getNeighbors());
				}
			}else{
				regions = new HashSet(m_root.getPolyList());
			}
			Iterator iter=regions.iterator();
			while(iter.hasNext()){
				DiscreteRegion region = (DiscreteRegion)iter.next();
				if(region.getName()==null){region.setName("R" + region.getNextNum());}
				java.util.List pointList = region.getPoints();
				if(m_showLabels){
					for(int j=0;j<pointList.size();j++){
						g2d.setColor(Color.BLACK);
						if(((RiffAbsolutePoint)pointList.get(j)).getName()!=null){
							g2d.drawString(((RiffAbsolutePoint)pointList.get(j)).getName(),(int)((RiffAbsolutePoint)pointList.get(j)).getX(),getHeight() - (int)((RiffAbsolutePoint)pointList.get(j)).getY());
						}
					}
				}
				if(m_drawMode){
					region.setColor(new Color(.25f, .25f + RiffToolbox.getRandom().nextFloat()%.75f, .25f + RiffToolbox.getRandom().nextFloat()%.75f, .25f + RiffToolbox.getRandom().nextFloat()%.75f));
				}
				//g2d.setColor(RiffGraphicsToolbox.getDiscreteRegionColor(region));
				g2d.setColor(region.getColor());
				g2d.fillPolygon(RiffGraphicsToolbox.getPolygonFromDiscreteRegion(this, region));
				g2d.setColor(Color.BLACK);
				g2d.drawString(region.getName(),(int)region.getBoundingRectMidPoint().getX(),getHeight() - (int)region.getBoundingRectMidPoint().getY());
				if(m_cyclePolygons){break;}
			}
			if(m_root.getTempList()!=null){
				Iterator errorPolygon = m_root.getTempList().iterator();
				g2d.setColor(Color.RED);
				while(errorPolygon.hasNext()){
					g2d.fillPolygon(RiffGraphicsToolbox.getPolygonFromDiscreteRegion(this, (DiscreteRegion)errorPolygon.next()));
				}
			}
		}
		
		g2d.setColor(Color.BLACK);
		if(m_drawingRegion!=null){
			g2d.drawPolygon(RiffGraphicsToolbox.getPolygonFromDiscreteRegion(this, m_drawingRegion));
		}
		if(m_pathList!=null){
			for(int i=0;i<m_pathList.size()-1;i++){
				RiffAbsolutePoint pointA=(RiffAbsolutePoint)m_pathList.get(i);
				RiffAbsolutePoint pointB=(RiffAbsolutePoint)m_pathList.get((i+1)%m_pathList.size());
				g2d.drawLine((int)pointA.getX(), getHeight()-(int)pointA.getY(), (int)pointB.getX(), getHeight()-(int)pointB.getY());
			}
		}
	}
	private double m_brushDensityModifier=0.0d;
	public void mouseClicked(MouseEvent e){
		if(!m_drawMode){
			assert RiffToolbox.printDebug("GUI/input", "Finding polygons...");
			Set polyList = m_root.findPolygons(e.getX(), getHeight() - e.getY());
			assert RiffToolbox.printDebug("GUI/input/data", "Polygon list: " + RiffToolbox.displayList(polyList));
			Iterator iter = polyList.iterator();
			if(iter.hasNext()){
				DiscreteRegion region=(DiscreteRegion)iter.next();
				region.setColor(Color.YELLOW);
				java.util.Set neighbors = region.getNeighbors();
				assert RiffToolbox.printDebug("GUI/input/data", "Neighbors: " + RiffToolbox.displayList(neighbors));
				Iterator polyIter=neighbors.iterator();
				while(polyIter.hasNext()){
					((DiscreteRegion)polyIter.next()).setColor(Color.BLUE);
				}
			}
			repaint();
			return;
		}
		/*if(!m_drawMode){
			assert RiffToolbox.printDebug("GUI/input", "Finding polygons...");
			if(m_sourcePoint==null){
				m_sourcePoint = new RiffEuclideanPoint(e.getX(), getHeight() - e.getY(), 0.0d);
				return;
			}
			Pathfinder pathfinder=new Pathfinder(m_root, m_sourcePoint, new RiffEuclideanPoint(e.getX(), getHeight()-e.getY(), 0.0d));
			m_pathList = pathfinder.getRoute();
			repaint();
			return;
		}*/
		if(m_drawingRegion==null){
			m_drawingRegion=new DiscreteRegion();
			//public Terrain(double brushDensity, double elevation, double temperature, double cohesion, double waterDepth){
			m_drawingRegion.addAsset(new Terrain(m_brushDensityModifier, .5d, .5d, .85d, 0.0d));
		}
		m_drawingRegion.addPoint(new RiffEuclideanPoint("Point " + (m_drawingRegion.getPoints().size()+1), null, e.getX(), getHeight()-e.getY(),0));
		repaint();
	}
	public void mousePressed(MouseEvent e){}
	public void mouseReleased(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void keyTyped(KeyEvent keyEvent){}
	public void keyReleased(KeyEvent keyEvent){}
	public void keyPressed(KeyEvent keyEvent){
		switch(keyEvent.getKeyCode()){
			case KeyEvent.VK_M:
			m_brushDensityModifier+=.1;
			System.out.println("Modifier: " + m_brushDensityModifier);
			if(m_brushDensityModifier>1.0d){m_brushDensityModifier=1.0d;}
			break;
			case KeyEvent.VK_N:
			m_brushDensityModifier-=.1;
			System.out.println("Modifier: " + m_brushDensityModifier);
			if(m_brushDensityModifier<0.0d){m_brushDensityModifier=0.0d;}
			break;
			case KeyEvent.VK_LEFT:
			m_polygonCycler--;
			break;
			case KeyEvent.VK_RIGHT:
			m_polygonCycler++;
			break;
			case KeyEvent.VK_SPACE:
			assert RiffToolbox.printDebug("GUI/input", "Toggling cyclePolygons.");
			m_cyclePolygons = !m_cyclePolygons;
			break;
			case KeyEvent.VK_CONTROL:
			assert RiffToolbox.printDebug("GUI/input", "Toggling the showing of labels.");
			m_showLabels = !m_showLabels;
			break;
			case KeyEvent.VK_ALT:
			assert RiffToolbox.printDebug("GUI/input", "Toggling draw-mode with point-finding mode.");
			m_drawMode = !m_drawMode;
			
			break;
			case KeyEvent.VK_Q:
			System.out.println(RiffToolbox.displayList(m_root.getPolyList()));
			break;
			case KeyEvent.VK_T:
			assert RiffToolbox.printDebug("GUI/input", "Toggling debug spew.");
			RiffToolbox.toggleDebugSpew();
			break;
			case KeyEvent.VK_U:
			System.out.println("TREEEE!!\n\n\n\n\n\n" + m_root);
			break;
			case KeyEvent.VK_Y:
			RiffToolbox.displayList(m_root.getPolyList());
			break;
			case KeyEvent.VK_CAPS_LOCK:
			assert RiffToolbox.printDebug("GUI/input", "Dumping unsplit regions into text file.");
			try{
				String string = RiffToolbox.getParseableDiscreteRegions(m_unsplitRegions);
				FileWriter writer = new FileWriter("dump.txt");
				System.out.println(string);
				writer.write(string);
				writer.close();
			}catch(IOException ex){
				System.out.println(ex);
			}
			assert RiffToolbox.printDebug("GUI/input", "Dumping complete.");
			break;
			case KeyEvent.VK_ENTER:
			if(m_splittingThread!=null&&m_splittingThread.isAlive()==false){
				m_statusText = "Splitting thread is not complete - cannot begin a new splitting process";
				break;
			}
			if(m_drawingRegion==null){break;}
			assert RiffToolbox.printDebug("GUI/input", "Duplicating drawing region and adding it to unsplit region list for debug dumping purposes.");
			m_unsplitRegions.add(new DiscreteRegion(m_drawingRegion));
			assert RiffToolbox.printDebug("GUI/input", "Creating new splitting thread.");
			m_splittingThread = new SplitterThread(this, m_root, m_drawingRegion, true);
			m_splittingThread.start();
			m_drawingRegion=null;
			break;
		}
		repaint();
	}
	protected void clear(Graphics g){super.paintComponent(g);}
  	public static void main(String[] args) {
	 	JFrame frame = new JFrame("RiffOverlappingSplitterGUI");
	 	frame.setSize(500, 500);
		frame.setContentPane(new RiffOverlappingSplitterGUI());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
