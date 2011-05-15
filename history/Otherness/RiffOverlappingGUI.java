import javax.swing.*; // For JPanel, etc.
import java.awt.*;           // For Graphics, etc.
import java.awt.geom.*;      // For Ellipse2D, etc.
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.*;

public class RiffOverlappingGUI extends JPanel implements KeyListener{
	private int m_polygonCycler;
	private boolean m_showLabels;
	private int m_viewMode=1;
	private String m_statusText;
	private java.util.Set m_regions = new HashSet();
	private SplitterThread m_thread;
	private DiscreteRegionBSPNode m_root;
	private static final int VIEW_ALL_POLYGONS=0;
	private static final int VIEW_TEMP_POLYGONS=1;
	private static final int VIEW_TREE_POLYGONS=2;
	private static final int CYCLE_POLYGONS=3;
	
	public RiffOverlappingGUI(){
		m_regions = RiffToolbox.getDiscreteRegionsFromFile("dump.txt");
		m_statusText=new String();
		m_showLabels=true;
		setFocusable(true);
		addKeyListener(this);
	}
	public void paintComponent(Graphics g){
		clear(g);
		Graphics2D g2d = (Graphics2D)g;
		if(m_thread!=null&&!m_thread.isAlive()){
			m_root = m_thread.getRoot();
			if(m_root!=null){
				m_regions = m_root.getTempList();
			}
		}
		java.util.Set allRegions = new HashSet();
		if(m_viewMode==RiffOverlappingGUI.VIEW_ALL_POLYGONS||m_viewMode==RiffOverlappingGUI.CYCLE_POLYGONS){
			if(m_root!=null){
				allRegions.addAll(m_root.getPolyList());
				allRegions.addAll(m_root.getTempList());
			}else{
				allRegions=m_regions;
			}
		}else if(m_viewMode==RiffOverlappingGUI.VIEW_TREE_POLYGONS){
			if(m_root!=null){
				allRegions.addAll(m_root.getPolyList());
			}
		}else if(m_viewMode==RiffOverlappingGUI.VIEW_TEMP_POLYGONS){
			if(m_root!=null){
				allRegions.addAll(m_root.getTempList());
			}else{
				allRegions=m_regions;
			}
		}
		Iterator iter = allRegions.iterator();
		while(iter.hasNext()){
			if(m_viewMode==RiffOverlappingGUI.CYCLE_POLYGONS){
				if(m_polygonCycler<0){m_polygonCycler=allRegions.size()-1;}
				int i=m_polygonCycler%allRegions.size();
				for(int cycle=0;cycle<i;i++){
					iter.next();
				}
				m_polygonCycler=i;
				m_statusText = "(Now on polygon " + i + ")";
			}
			DiscreteRegion region = (DiscreteRegion)iter.next();
			java.util.List pointList = region.getPoints();
			if(m_showLabels){
				for(int j=0;j<pointList.size();j++){
					g2d.setColor(Color.BLACK);
					if(((RiffAbsolutePoint)pointList.get(j)).getName()!=null){
						g2d.drawString(((RiffAbsolutePoint)pointList.get(j)).getName(),(int)((RiffAbsolutePoint)pointList.get(j)).getX(),getHeight()-(int)((RiffAbsolutePoint)pointList.get(j)).getY());
					}
				}
			}
			g2d.setColor(new Color(.25f, .25f + RiffToolbox.getRandom().nextFloat()%.75f, .25f + RiffToolbox.getRandom().nextFloat()%.75f, .25f + RiffToolbox.getRandom().nextFloat()%.75f));
			g2d.fillPolygon(RiffGraphicsToolbox.getPolygonFromDiscreteRegion(this, region));
			if(m_viewMode==RiffOverlappingGUI.CYCLE_POLYGONS){break;}
		}
		g2d.setColor(Color.BLACK);
		g2d.drawString(m_statusText, 5, getHeight()-5);
	}
	public void keyTyped(KeyEvent keyEvent){}
	public void keyReleased(KeyEvent keyEvent){}
	public synchronized void keyPressed(KeyEvent keyEvent){
		switch(keyEvent.getKeyCode()){
			case KeyEvent.VK_LEFT:
			m_polygonCycler--;
			break;
			case KeyEvent.VK_RIGHT:
			m_polygonCycler++;
			break;
			case KeyEvent.VK_CAPS_LOCK:
			try{
				String string = RiffToolbox.getParseableDiscreteRegions(m_regions);
				FileWriter writer = new FileWriter("quickDump.txt");
				writer.write(string);
				writer.close();
			}catch(IOException ex){
				System.out.println(ex);
			}
			break;
			case KeyEvent.VK_T:
			RiffToolbox.toggleDebugSpew();
			break;
			case KeyEvent.VK_SPACE:
			m_viewMode=(m_viewMode+1)%4;
			switch(m_viewMode){
				case RiffOverlappingGUI.VIEW_ALL_POLYGONS:
				m_statusText = "Now viewing all polygons.";
				break;
				case RiffOverlappingGUI.VIEW_TREE_POLYGONS:
				m_statusText = "Now viewing all tree polygons.";
				break;
				case RiffOverlappingGUI.VIEW_TEMP_POLYGONS:
				m_statusText = "Now viewing all to-be-added polygons.";
				break;
				case RiffOverlappingGUI.CYCLE_POLYGONS:
				m_statusText = "Now cycling through polygons.";
				break;
			}
			break;
			case KeyEvent.VK_CONTROL:
			m_showLabels = !m_showLabels;
			break;
			case KeyEvent.VK_ENTER:
			m_thread = new SplitterThread(this, m_root, m_regions,false);
			m_thread.start();
			break;
			case KeyEvent.VK_BACK_QUOTE:
			System.out.println(RiffToolbox.displayList(m_regions));
			break;
		}
		repaint();
	}
	protected void clear(Graphics g){super.paintComponent(g);}
	public static void main(String[] args) {
	 	JFrame frame = new JFrame("RiffOverlappingGUI");
	 	frame.setSize(500, 500);
		frame.setContentPane(new RiffOverlappingGUI());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
