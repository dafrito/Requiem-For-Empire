import javax.swing.*; // For JPanel, etc.
import java.awt.*;           // For Graphics, etc.
import java.awt.geom.*;      // For Ellipse2D, etc.
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import javax.swing.JOptionPane;
import java.lang.Math;

public class RiffFullPolygonTesterGUI extends JPanel implements KeyListener, MouseListener{
	private DiscreteRegionBinaryTree m_regionTree;
	private DiscreteRegion m_drawingRegion;
	private java.util.List m_polygonThreads;
	private boolean m_displayNames;
	private String m_infoString;
	public RiffFullPolygonTesterGUI(){
		m_drawingRegion = new DiscreteRegion();
		m_regionTree= new DiscreteRegionBinaryTree();
		m_polygonThreads=new Vector();
		m_displayNames=true;
		m_infoString = new String("Awaiting drawing instructions...");
		addMouseListener(this);
		addKeyListener(this);
		setFocusable(true);
	}
	public void setInfoString(String string){m_infoString=string;repaint();}
	public String getInfoString(){return m_infoString;}
	public void paintComponent(Graphics g){
		clear(g);
		Graphics2D g2d = (Graphics2D)g;
		g2d.setColor(Color.BLACK);
		g2d.drawString(getInfoString(), 5, getHeight()-5);
		Iterator iter = m_polygonThreads.iterator();
		int verticalOffset=12;
		java.util.List workingRegionsList = new LinkedList();
		java.util.List deleteList = new LinkedList();
		while(iter.hasNext()){
			PolygonPipeline pipeline = (PolygonPipeline)iter.next();
			if(pipeline.isComplete()){
				deleteList.add(pipeline);
				continue;
			}else{
				workingRegionsList.add(pipeline.getRegion());
			}
			g2d.drawString(pipeline.getInfoString(), 5, getHeight()-5-verticalOffset);
			verticalOffset += 12;
		}
		m_polygonThreads.removeAll(deleteList);
		if(m_drawingRegion != null){
			java.util.List lineList = RiffGraphicsToolbox.getLineListFromDiscreteRegion(m_drawingRegion);
			for(int i=0;i<lineList.size();i++){
				if(i%2 == 0){g2d.setColor(Color.RED);}
				if(i%2 == 1){g2d.setColor(Color.BLUE);}
				g2d.drawLine((int)((Line2D.Double)lineList.get(i)).getX1(),(int)((Line2D.Double)lineList.get(i)).getY1(),(int)((Line2D.Double)lineList.get(i)).getX2(),(int)((Line2D.Double)lineList.get(i)).getY2());
			}
		}
		java.util.List m_polygons = new LinkedList(m_regionTree.getFullList());
		m_polygons.addAll(workingRegionsList);
		for(int q=0;q<m_polygons.size();q++){
			DiscreteRegion currentRegion = (DiscreteRegion)m_polygons.get(q);
			java.util.List lineList = RiffGraphicsToolbox.getLineListFromDiscreteRegion(currentRegion);
			if(m_displayNames){
				for(int i=0;i<(currentRegion).getPoints().size();i++){
					g2d.setColor(Color.BLACK);
					g2d.drawString(((RiffAbsolutePoint)currentRegion.getPoints().get(i)).getName(),(int)((RiffAbsolutePoint)currentRegion.getPoints().get(i)).getX(),(int)((RiffAbsolutePoint)currentRegion.getPoints().get(i)).getY());	
				}
			}
			for(int i=0;i<lineList.size();i++){
				if(i%2 == 0){g2d.setColor(Color.RED);}
				if(i%2 == 1){g2d.setColor(Color.BLUE);}
				g2d.drawLine((int)((Line2D.Double)lineList.get(i)).getX1(),(int)((Line2D.Double)lineList.get(i)).getY1(),(int)((Line2D.Double)lineList.get(i)).getX2(),(int)((Line2D.Double)lineList.get(i)).getY2());
			}
			if(m_boundingRectTestClick){
				boundingRect.reset();
				g2d.setColor(Color.BLACK);
				g2d.drawString(Integer.toString((int)currentRegion.getLeftExtreme()) + ", " + Integer.toString((int)currentRegion.getBottomExtreme()), (int)currentRegion.getLeftExtreme(), (int)currentRegion.getBottomExtreme());
				g2d.drawString(Integer.toString((int)currentRegion.getRightExtreme()) + ", " + Integer.toString((int)currentRegion.getTopExtreme()), (int)currentRegion.getRightExtreme(), (int)currentRegion.getTopExtreme());
				boundingRect.addPoint((int)currentRegion.getLeftExtreme(), (int)currentRegion.getTopExtreme());
				boundingRect.addPoint((int)currentRegion.getRightExtreme(), (int)currentRegion.getTopExtreme());
				boundingRect.addPoint((int)currentRegion.getRightExtreme(), (int)currentRegion.getBottomExtreme());
				boundingRect.addPoint((int)currentRegion.getLeftExtreme(), (int)currentRegion.getBottomExtreme());
				g2d.drawPolygon(boundingRect);
			}
		}
	}
	private Polygon boundingRect= new Polygon();
	private boolean m_boundingRectTestClick=false;
	public void keyTyped(KeyEvent keyEvent){}
	public void keyReleased(KeyEvent keyEvent){}
	public void keyPressed(KeyEvent keyEvent){
		switch(keyEvent.getKeyCode()){
			case KeyEvent.VK_CONTROL:
			m_boundingRectTestClick = !m_boundingRectTestClick;
			if(m_boundingRectTestClick){setInfoString("Now in testpoint mode...");
			}else{setInfoString("Now in draw mode...");}
			break;
			case KeyEvent.VK_ENTER:
			m_polygonThreads.add(new PolygonPipeline(this, m_regionTree, m_polygonThreads.size(), m_drawingRegion));
			m_drawingRegion=new DiscreteRegion();
			((Thread)m_polygonThreads.get(m_polygonThreads.size()-1)).start();
			break; 
			case KeyEvent.VK_SPACE:
			m_displayNames = !m_displayNames;
			if(m_displayNames){setInfoString("Now displaying names...");
			}else{setInfoString("Now not displaying names...");}
			break;
		}
	}
	public void mouseClicked(MouseEvent e){
		if(m_boundingRectTestClick){
			System.out.println("Region Tree: " + m_regionTree);
			System.out.println("Testing point: " + e.getPoint());
			Set polygons = m_regionTree.testPoint(RiffGraphicsToolbox.convertPointToEuclidean(e.getPoint()));
			setInfoString("Bounding-rect collisions with clicked point: " + polygons.size());
			System.out.println("\n" + RiffToolbox.displayList(polygons, "polygon that potentially collides with this point based on the bounding-rect test", "polygons that potentially collide with this point based on the bounding-rect test"));  
			return;
		}
		if(m_drawingRegion==null){m_drawingRegion=new DiscreteRegion();}
		m_drawingRegion.addPoint(new RiffEuclideanPoint("Point " + (m_drawingRegion.getPoints().size()+1), null, e.getX(),e.getY(),0));
		System.out.println(e.getPoint());
		repaint();
	}
	public void mousePressed(MouseEvent e){}
	public void mouseReleased(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	protected void clear(Graphics g){super.paintComponent(g);}
  	public static void main(String[] args) {
	 	JFrame frame = new JFrame("RiffFullPolygonTesterGUI");
	 	frame.setSize(720, 360);
		frame.setContentPane(new RiffFullPolygonTesterGUI());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
