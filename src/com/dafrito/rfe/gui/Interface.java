package com.dafrito.rfe.gui;

// Requiem for Empire
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import com.dafrito.rfe.Debugger;
import com.dafrito.rfe.Exception_InternalError;
import com.dafrito.rfe.MouseButton;
import com.dafrito.rfe.ScriptEnvironment;
import com.dafrito.rfe.gui.event.KeyEvent_KeyDown;
import com.dafrito.rfe.gui.event.KeyEvent_KeyUp;
import com.dafrito.rfe.gui.event.RiffInterface_ClickEvent;
import com.dafrito.rfe.gui.event.RiffInterface_DragEvent;
import com.dafrito.rfe.gui.event.RiffInterface_Event;
import com.dafrito.rfe.gui.event.RiffInterface_MouseDownEvent;
import com.dafrito.rfe.gui.event.RiffInterface_MouseUpEvent;
import com.dafrito.rfe.points.Points;

public class Interface extends JPanel implements MouseListener, MouseMotionListener, WindowListener, ComponentListener, KeyListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8097618780874029519L;
	private static final int HEIGHT = 600;
	private static final int WIDTH = 800;
	private ScriptEnvironment environment;
	private InterfaceElement_Root rootElement;
	private int lastX, lastY;
	private java.util.List<RiffInterface_Event> queuedEvents = new LinkedList<RiffInterface_Event>();
	private MouseButton lastButton;
	private boolean ignoreMemoryWarning;
	private BufferedImage buffer, backBuffer;
	private long secondBegin;
	private int lastIteration;
	private int iterations;
	private boolean emergencyStop;
	private JFrame frame;

	private ScheduledExecutorService painting;

	public Interface(ScriptEnvironment env) {
		this.frame = new JFrame("Requiem for Empire");
		this.frame.setSize(WIDTH, HEIGHT);
		this.frame.setContentPane(this);
		this.frame.addWindowListener(this);
		this.frame.addComponentListener(this);
		this.frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.frame.setVisible(true);
		this.frame.addKeyListener(this);
		this.environment = env;
		this.rootElement = new InterfaceElement_Root(env, this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.buffer = (BufferedImage) this.createImage(this.frame.getContentPane().getWidth(), this.frame.getContentPane().getHeight());
		this.backBuffer = (BufferedImage) this.createImage(this.frame.getContentPane().getWidth(), this.frame.getContentPane().getHeight());
		this.painting = Executors.newScheduledThreadPool(4);
		this.painting.scheduleAtFixedRate(new PaintInterface(this), 0, 1000 / 60, TimeUnit.MILLISECONDS);
	}

	protected void clear(Graphics g) {
		this.paint(g);
	}

	@Override
	public void componentHidden(ComponentEvent e) {
	}

	@Override
	public void componentMoved(ComponentEvent e) {
	}

	@Override
	public void componentResized(ComponentEvent e) {
		if (this.buffer == null) {
			return;
		}
		if (this.buffer.getWidth() != this.getWidth() || this.buffer.getHeight() != this.getHeight()) {
			this.buffer = (BufferedImage) this.createImage(this.getWidth(), this.getHeight());
			this.backBuffer = (BufferedImage) this.createImage(this.getWidth(), this.getHeight());
		}
	}

	@Override
	public void componentShown(ComponentEvent e) {
	}

	public synchronized void flushQueue() {
		assert Debugger.addNode("Flushing Event Queue (" + this.queuedEvents.size() + " event(s))");
		List<RiffInterface_Event> flushQueue = new LinkedList<RiffInterface_Event>();
		flushQueue.addAll(this.queuedEvents);
		this.queuedEvents.removeAll(flushQueue);
		for (int i = 0; i < flushQueue.size(); i++) {
			this.rootElement.dispatchEvent(flushQueue.get(i));
		}
	}

	public BufferedImage backBuffer() {
		return this.backBuffer;
	}

	public ScriptEnvironment getEnvironment() {
		return this.environment;
	}

	public JFrame getFrame() {
		return this.frame;
	}

	public BufferedImage getFrontBuffer() {
		return this.buffer;
	}

	public InterfaceElement_Root getRoot() {
		return this.rootElement;
	}

	@Override
	public synchronized void keyPressed(KeyEvent e) {
		KeyEvent_KeyDown event = new KeyEvent_KeyDown(e.getKeyCode());
		assert Debugger.addNode(event);
		this.queuedEvents.add(event);
	}

	@Override
	public synchronized void keyReleased(KeyEvent e) {
		KeyEvent_KeyUp event = new KeyEvent_KeyUp(e.getKeyCode());
		assert Debugger.addNode(event);
		this.queuedEvents.add(event);
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public synchronized void mouseClicked(MouseEvent e) {
		RiffInterface_ClickEvent event = new RiffInterface_ClickEvent(e.getX(), e.getY(), MouseButton.getButton(e.getButton()), e.getClickCount());
		assert Debugger.addNode(event);
		this.queuedEvents.add(event);
		this.lastX = e.getX();
		this.lastY = e.getY();
		this.lastButton = MouseButton.getButton(e.getButton());
	}

	@Override
	public synchronized void mouseDragged(MouseEvent e) {
		double distance = Points.getDistance(this.lastX, this.lastY, e.getX(), e.getY());
		if (e.getX() - this.lastX < 0) {
			distance *= -1;
		}
		if (e.getX() == this.lastX && e.getY() - this.lastY < 0 && distance > 0) {
			distance *= -1;
		}
		RiffInterface_DragEvent event = new RiffInterface_DragEvent(e.getX(), e.getY(), this.lastButton, e.getX() - this.lastX, e.getY() - this.lastY, distance);
		assert Debugger.addNode(event);
		this.queuedEvents.add(event);
		this.lastX = e.getX();
		this.lastY = e.getY();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public synchronized void mousePressed(MouseEvent e) {
		RiffInterface_MouseDownEvent event = new RiffInterface_MouseDownEvent(e.getX(), e.getY(), MouseButton.getButton(e.getButton()));
		assert Debugger.addNode(event);
		this.queuedEvents.add(event);
		this.lastX = e.getX();
		this.lastY = e.getY();
		this.lastButton = MouseButton.getButton(e.getButton());
	}

	@Override
	public synchronized void mouseReleased(MouseEvent e) {
		RiffInterface_MouseUpEvent event = new RiffInterface_MouseUpEvent(e.getX(), e.getY(), MouseButton.getButton(e.getButton()));
		assert Debugger.addNode(event);
		this.queuedEvents.add(event);
		this.lastX = -1;
		this.lastY = -1;
	}

	@Override
	public void paint(Graphics g) {
		g.drawImage(this.buffer, 0, 0, null);
		g.dispose();
	}

	@Override
	public void update(Graphics g) {
		this.paint(g);
	}

	public synchronized void updateBufferedImage() {
		if (this.emergencyStop) {
			return;
		}
		if (this.secondBegin == 0) {
			this.secondBegin = System.currentTimeMillis();
		}
		if (System.currentTimeMillis() > this.secondBegin + 1000) {
			this.secondBegin = System.currentTimeMillis();
			this.lastIteration = this.iterations;
			this.iterations = 0;
		}
		this.iterations++;
		if (this.backBuffer == null) {
			return;
		}
		Graphics g = this.backBuffer.getGraphics();
		try {
			if (Debugger.isResetting()) {
				return;
			}
			if (Debugger.atFullAllocation() && Debugger.getFreePercentage() > 50) {
				Debugger.getDebugger().setExceptionsMode(true);
			}
			if (!this.ignoreMemoryWarning && Debugger.atFullAllocation() && Debugger.getFreePercentage() < 20) {
				System.gc();
				if (Debugger.getFreePercentage() > 20) {
					return;
				}
				Debugger.getDebugger().report();
				this.emergencyStop = true;
				int option = JOptionPane.showConfirmDialog(null, "Memory usage exceeds 80% of full allocation. Reset debug tree?", "Memory Warning (" + Debugger.getFreePercentage() + "% free)", JOptionPane.YES_NO_OPTION);
				if (option == JOptionPane.YES_OPTION) {
					Debugger.getDebugger().reset();
					this.emergencyStop = false;
				} else {
					this.ignoreMemoryWarning = true;
					this.emergencyStop = false;
				}
			}
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			this.rootElement.paint(g2d);
			g2d.setColor(Color.WHITE);
			g2d.drawString("" + this.lastIteration + " fps", this.getWidth() / 2, 20);
			g2d.dispose();
		} catch (Exception ex) {
			throw new Exception_InternalError(this.rootElement.getEnvironment(), ex);
		}
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		this.painting.shutdown();
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}
}
