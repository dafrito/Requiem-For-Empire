package com.dafrito.rfe.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import com.dafrito.rfe.geom.points.Points;
import com.dafrito.rfe.gui.event.KeyEvent_KeyDown;
import com.dafrito.rfe.gui.event.KeyEvent_KeyUp;
import com.dafrito.rfe.gui.event.RiffInterface_ClickEvent;
import com.dafrito.rfe.gui.event.RiffInterface_DragEvent;
import com.dafrito.rfe.gui.event.RiffInterface_Event;
import com.dafrito.rfe.gui.event.RiffInterface_MouseDownEvent;
import com.dafrito.rfe.gui.event.RiffInterface_MouseUpEvent;
import com.dafrito.rfe.logging.Logs;
import com.dafrito.rfe.script.ScriptEnvironment;

/**
 * Displays the game world and dispatches input events.
 * 
 * @author Aaron Faanes
 */
public class Interface extends JPanel {
	private static final long serialVersionUID = 8097618780874029519L;
	private static final int HEIGHT = 600;
	private static final int WIDTH = 800;
	private final InterfaceElement_Root rootElement;
	private int lastX, lastY;
	private final Deque<RiffInterface_Event> queuedEvents = new ArrayDeque<RiffInterface_Event>();
	private MouseButton lastButton;
	private BufferedImage frontBuffer, backBuffer;
	private long secondBegin;
	private int lastIteration;
	private int iterations;
	private boolean emergencyStop;
	private final JFrame frame = new JFrame("Requiem for Empire");

	/**
	 * The {@link Executor} that manages updating the buffered images of this
	 * interface panel.
	 */
	private final ScheduledExecutorService painting = Executors.newScheduledThreadPool(4);

	public Interface(ScriptEnvironment env) {
		// TODO I'd like to move this code outside of this class, since I don't like
		// panels controlling their own parent frames.
		this.frame.setSize(WIDTH, HEIGHT);
		this.frame.setContentPane(this);
		this.frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.frame.setVisible(true);

		this.rootElement = new InterfaceElement_Root(env, this);
		recreateBufferedImages();

		this.painting.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				frontBuffer.getGraphics().drawImage(backBuffer, 0, 0, null);
				flushQueue();
				updateBufferedImage();
				repaint();
			}
		}, 0, 1000 / 60, TimeUnit.MILLISECONDS);

		// Stop painting this window when we close
		this.frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				painting.shutdown();
			}
		});

		// Resize the back buffer when we're resized
		this.frame.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				if (frontBuffer == null) {
					return;
				}
				if (frontBuffer.getWidth() != getWidth() || frontBuffer.getHeight() != getHeight()) {
					recreateBufferedImages();
				}
			}
		});

		this.frame.addKeyListener(new KeyAdapter() {
			@Override
			public synchronized void keyPressed(KeyEvent e) {
				KeyEvent_KeyDown event = new KeyEvent_KeyDown(e.getKeyCode());
				assert Logs.addNode(event);
				queuedEvents.add(event);
			}

			@Override
			public synchronized void keyReleased(KeyEvent e) {
				KeyEvent_KeyUp event = new KeyEvent_KeyUp(e.getKeyCode());
				assert Logs.addNode(event);
				queuedEvents.add(event);
			}
		});

		this.addMouseListener(new MouseAdapter() {

			@Override
			public synchronized void mouseClicked(MouseEvent e) {
				RiffInterface_ClickEvent event = new RiffInterface_ClickEvent(e.getX(), e.getY(), MouseButton.getButton(e.getButton()), e.getClickCount());
				assert Logs.addNode(event);
				queuedEvents.add(event);
				lastX = e.getX();
				lastY = e.getY();
				lastButton = MouseButton.getButton(e.getButton());
			}

			@Override
			public synchronized void mousePressed(MouseEvent e) {
				RiffInterface_MouseDownEvent event = new RiffInterface_MouseDownEvent(e.getX(), e.getY(), MouseButton.getButton(e.getButton()));
				assert Logs.addNode(event);
				queuedEvents.add(event);
				lastX = e.getX();
				lastY = e.getY();
				lastButton = MouseButton.getButton(e.getButton());
			}

			@Override
			public synchronized void mouseReleased(MouseEvent e) {
				RiffInterface_MouseUpEvent event = new RiffInterface_MouseUpEvent(e.getX(), e.getY(), MouseButton.getButton(e.getButton()));
				assert Logs.addNode(event);
				queuedEvents.add(event);
				lastX = -1;
				lastY = -1;
			}
		});

		this.addMouseMotionListener(new MouseAdapter() {

			@Override
			public synchronized void mouseDragged(MouseEvent e) {
				double distance = Points.getDistance(lastX, lastY, e.getX(), e.getY());
				if (e.getX() - lastX < 0) {
					distance *= -1;
				}
				if (e.getX() == lastX && e.getY() - lastY < 0 && distance > 0) {
					distance *= -1;
				}
				RiffInterface_DragEvent event = new RiffInterface_DragEvent(e.getX(), e.getY(), lastButton, e.getX() - lastX, e.getY() - lastY, distance);
				assert Logs.addNode(event);
				queuedEvents.add(event);
				lastX = e.getX();
				lastY = e.getY();
			}

		});
	}

	private void recreateBufferedImages() {
		this.frontBuffer = (BufferedImage) this.createImage(getWidth(), getHeight());
		this.backBuffer = (BufferedImage) this.createImage(getWidth(), getHeight());
	}

	private void flushQueue() {
		assert Logs.addNode("Flushing Event Queue (" + this.queuedEvents.size() + " event(s))");
		while (!this.queuedEvents.isEmpty()) {
			this.rootElement.dispatchEvent(this.queuedEvents.pop());
		}
	}

	public InterfaceElement_Root getRoot() {
		return this.rootElement;
	}

	@Override
	public void paint(Graphics g) {
		g.drawImage(this.frontBuffer, 0, 0, null);
		g.dispose();
	}

	@Override
	public void update(Graphics g) {
		this.paint(g);
	}

	private void updateBufferedImage() {
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
		Graphics2D g2d = (Graphics2D) this.backBuffer.getGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		this.rootElement.paint(g2d);
		g2d.setColor(Color.WHITE);
		g2d.drawString("" + this.lastIteration + " fps", this.getWidth() / 2, 20);
		g2d.dispose();
	}

}
