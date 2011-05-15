import javax.swing.*; // For JPanel, etc.
import java.awt.*;           // For Graphics, etc.
import java.awt.geom.*;      // For Ellipse2D, etc.
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.*;

public class RiffSphereViewerGUI extends JPanel implements KeyListener{
	private String m_statusText;
	public RiffSphereViewerGUI(){
		m_statusText=new String("No time, Sir.");
		setFocusable(true);
		addKeyListener(this);
	}
	public synchronized void setStatusText(String string){m_statusText=string;repaint();}
	public void paintComponent(Graphics g){
		clear(g);
		Graphics2D g2d = (Graphics2D)g;
		if(m_statusText!=null)g2d.drawString(m_statusText, 5, getHeight()-5);
	}
	public void keyTyped(KeyEvent keyEvent){}
	public void keyReleased(KeyEvent keyEvent){}
	public void keyPressed(KeyEvent keyEvent){}
	protected void clear(Graphics g){super.paintComponent(g);}
	public static void main(String[] args) {
	 	JFrame frame = new JFrame("RiffSphereViewerGUI");
	 	frame.setSize(720, 360);
		frame.setContentPane(new RiffSphereViewerGUI());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
