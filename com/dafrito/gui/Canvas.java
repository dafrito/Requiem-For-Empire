package com.dafrito.gui;

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

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Exception_InternalError;
import com.dafrito.script.ScriptEnvironment;
import com.dafrito.util.RiffJavaToolbox;
import com.dafrito.util.RiffToolbox;

class PainterThread extends Thread {
    private Canvas canvas;
    private static int threadNum = 0;
    private volatile boolean shouldDraw = true;
    private int delay;

    public PainterThread(Canvas canvas, int delay) {
        super("Painter " + threadNum++);
        this.canvas = canvas;
        this.delay = delay;
    }

    @Override
    public void run() {
        try {
            while (this.shouldDraw) {
                // Debugger.hitStopWatch(Thread.currentThread().getName());
                this.canvas.getFrontBuffer().getGraphics().drawImage(this.canvas.getBufferedImage(), 0, 0, null);
                this.canvas.flushQueue();
                this.canvas.updateBufferedImage();
                this.canvas.repaint();
                Thread.sleep(this.delay);
            }
        }  catch(Exception_InternalError e) {
            System.out.println(e);
        } catch(RuntimeException ex) {
            stopLoop();
            this.canvas.getFrame().dispose();
            throw ex;
        } catch(InterruptedException ex) {
            // Do nothing.
        } finally {
            // Debugger.hitStopWatch(Thread.currentThread().getName());
            System.out.println("PainterThread has ended.");
        }
    }

    public void stopLoop() {
        this.shouldDraw = false;
        this.canvas.getEnvironment().stopExecution();
    }
}

public class Canvas extends JPanel implements MouseListener, MouseMotionListener, WindowListener, ComponentListener,
        KeyListener {
    private static final int CANVAS_HEIGHT = 600;
    private static final int CANVAS_WIDTH = 800;
    private ScriptEnvironment environment;
    private InterfaceElement_Root rootElement;
    private int lastX, lastY;
    private java.util.List<RiffInterface_Event> queuedEvents = new LinkedList<RiffInterface_Event>();
    private RiffInterface_MouseListener.MouseButton lastButton;
    private BufferedImage buffer, backBuffer;
    private long secondBegin;
    private int lastIteration;
    private int iterations;
    private boolean emergencyStop;
    private JFrame frame;
    private PainterThread painter;

    public int getFramerate() {
        // TODO: If we're in debug mode, return 200. Otherwise return 1000 / 60
        return 1000 / 60;
    }

    public Canvas(ScriptEnvironment env) {
        this.frame = new JFrame("Requiem for Empire");
        this.frame.setSize(CANVAS_WIDTH, CANVAS_HEIGHT);
        this.frame.setContentPane(this);
        this.frame.addWindowListener(this);
        this.frame.addComponentListener(this);
        this.frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.frame.setVisible(true);
        this.frame.addKeyListener(this);
        this.environment = env;
        this.rootElement = new InterfaceElement_Root(env, this);
        addMouseListener(this);
        addMouseMotionListener(this);
        this.buffer = (BufferedImage)createImage(
            this.frame.getContentPane().getWidth(),
            this.frame.getContentPane().getHeight());
        this.backBuffer = (BufferedImage)createImage(
            this.frame.getContentPane().getWidth(),
            this.frame.getContentPane().getHeight());
        
        this.painter = new PainterThread(this, this.getFramerate());
        
        this.painter.start();
    }

    public BufferedImage getFrontBuffer() {
        return this.buffer;
    }

    public BufferedImage getBufferedImage() {
        return this.backBuffer;
    }

    public JFrame getFrame() {
        return this.frame;
    }

    public InterfaceElement_Root getRoot() {
        return this.rootElement;
    }

    public ScriptEnvironment getEnvironment() {
        return this.environment;
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

    protected void clear(Graphics g) {
        paint(g);
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(this.buffer, 0, 0, null);
        g.dispose();
    }

    public synchronized void updateBufferedImage() {
        if(this.emergencyStop) {
            return;
        }
        if(this.secondBegin == 0) {
            this.secondBegin = System.currentTimeMillis();
        }
        if(System.currentTimeMillis() > this.secondBegin + 1000) {
            this.secondBegin = System.currentTimeMillis();
            this.lastIteration = this.iterations;
            this.iterations = 0;
        }
        this.iterations++;
        if(this.backBuffer == null) {
            return;
        }
        Graphics g = this.backBuffer.getGraphics();
        try {
            /*if(Debugger.isResetting()) {
                return;
            }
            if(Debugger.atFullAllocation() && Debugger.getFreePercentage() > 50) {
                Debugger.setExceptionsMode(true);
            }
            if(!this.ignoreMemoryWarning && Debugger.atFullAllocation() && Debugger.getFreePercentage() < 20) {
                if(Debugger.getFreePercentage() > 20) {
                    return;
                }
                this.emergencyStop = true;
                int option = JOptionPane.showConfirmDialog(
                    null,
                    "Memory usage exceeds 80% of full allocation. Reset debug tree?",
                    "Memory Warning (" + Debugger.getFreePercentage() + "% free)",
                    JOptionPane.YES_NO_OPTION);
                if(option == JOptionPane.YES_OPTION) {
                    Debugger.reset();
                    this.emergencyStop = false;
                } else {
                    this.ignoreMemoryWarning = true;
                    this.emergencyStop = false;
                }
            }*/
            Graphics2D g2d = (Graphics2D)g;
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            this.rootElement.paint(g2d);
            g2d.setColor(Color.WHITE);
            g2d.drawString("" + this.lastIteration + " fps", getWidth() / 2, 20);
            g2d.dispose();
        } catch(Exception ex) {
            throw new Exception_InternalError(this.rootElement.getEnvironment(), ex);
        }
    }

    public synchronized void flushQueue() {
        assert LegacyDebugger.addNode("Flushing Event Queue (" + this.queuedEvents.size() + " event(s))");
        List<RiffInterface_Event> flushQueue = new LinkedList<RiffInterface_Event>();
        flushQueue.addAll(this.queuedEvents);
        this.queuedEvents.removeAll(flushQueue);
        for(int i = 0; i < flushQueue.size(); i++) {
            this.rootElement.dispatchEvent(flushQueue.get(i));
        }
    }

    public synchronized void mousePressed(MouseEvent e) {
        RiffInterface_MouseDownEvent event = new RiffInterface_MouseDownEvent(e.getX(), e.getY(), RiffJavaToolbox.getRiffButton(e.getButton()));
        assert LegacyDebugger.addNode(event);
        this.queuedEvents.add(event);
        this.lastX = e.getX();
        this.lastY = e.getY();
        this.lastButton = RiffJavaToolbox.getRiffButton(e.getButton());
    }

    public synchronized void mouseClicked(MouseEvent e) {
        RiffInterface_ClickEvent event = new RiffInterface_ClickEvent(e.getX(), e.getY(), RiffJavaToolbox.getRiffButton(e.getButton()), e.getClickCount());
        assert LegacyDebugger.addNode(event);
        this.queuedEvents.add(event);
        this.lastX = e.getX();
        this.lastY = e.getY();
        this.lastButton = RiffJavaToolbox.getRiffButton(e.getButton());
    }

    public synchronized void mouseReleased(MouseEvent e) {
        RiffInterface_MouseUpEvent event = new RiffInterface_MouseUpEvent(e.getX(), e.getY(), RiffJavaToolbox.getRiffButton(e.getButton()));
        assert LegacyDebugger.addNode(event);
        this.queuedEvents.add(event);
        this.lastX = -1;
        this.lastY = -1;
    }

    public synchronized void mouseDragged(MouseEvent e) {
        double distance = RiffToolbox.getDistance(this.lastX, this.lastY, e.getX(), e.getY());
        if(e.getX() - this.lastX < 0) {
            distance *= -1;
        }
        if(e.getX() == this.lastX && e.getY() - this.lastY < 0 && distance > 0) {
            distance *= -1;
        }
        RiffInterface_DragEvent event = new RiffInterface_DragEvent(e.getX(), e.getY(), this.lastButton, e.getX()
            - this.lastX, e.getY() - this.lastY, distance);
        assert LegacyDebugger.addNode(event);
        this.queuedEvents.add(event);
        this.lastX = e.getX();
        this.lastY = e.getY();
    }

    public synchronized void keyPressed(KeyEvent e) {
        KeyEvent_KeyDown event = new KeyEvent_KeyDown(e.getKeyCode());
        assert LegacyDebugger.addNode(event);
        this.queuedEvents.add(event);
    }

    public synchronized void keyReleased(KeyEvent e) {
        KeyEvent_KeyUp event = new KeyEvent_KeyUp(e.getKeyCode());
        assert LegacyDebugger.addNode(event);
        this.queuedEvents.add(event);
    }

    // TODO: Canvas should use adapters

    public void mouseEntered(MouseEvent e) {
        // Do nothing
    }

    public void mouseExited(MouseEvent e) {
        // Do nothing
    }

    public void mouseMoved(MouseEvent e) {
        // Do nothing
    }

    public void keyTyped(KeyEvent e) {
        // Do nothing
    }

    public void windowOpened(WindowEvent e) {
        // Do nothing
    }

    public void windowClosed(WindowEvent e) {
        // Do nothing
    }

    public void windowClosing(WindowEvent e) {
        this.painter.stopLoop();
    }

    public void windowIconified(WindowEvent e) {
        // Do nothing
    }

    public void windowDeiconified(WindowEvent e) {
        // Do nothing
    }

    public void windowActivated(WindowEvent e) {
        // Do nothing
    }

    public void windowDeactivated(WindowEvent e) {
        // Do nothing
    }

    public void componentResized(ComponentEvent e) {
        if(this.buffer == null) {
            System.out.println("Buffer is null!");
            return;
        }
        if(this.buffer.getWidth() != getWidth() || this.buffer.getHeight() != getHeight()) {
            this.buffer = (BufferedImage)createImage(getWidth(), getHeight());
            this.backBuffer = (BufferedImage)createImage(getWidth(), getHeight());
        }
    }

    public void componentMoved(ComponentEvent e) {
        // Do nothing
    }

    public void componentShown(ComponentEvent e) {
        // Do nothing
    }

    public void componentHidden(ComponentEvent e) {
        // Do nothing
    }
}
