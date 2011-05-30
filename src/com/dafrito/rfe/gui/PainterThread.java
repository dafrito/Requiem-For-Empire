/**
 * 
 */
package com.dafrito.rfe.gui;

import com.dafrito.rfe.Debugger;

class PainterThread extends Thread {
	private Interface riffInterface;
	private static int threadNum = 0;
	private volatile boolean shouldDraw = true;
	private int delay;

	public PainterThread(Interface riffInterface, int delay) {
		super("Painter " + threadNum++);
		this.riffInterface = riffInterface;
		this.delay = delay;
	}

	@Override
	public void run() {
		try {
			while (this.shouldDraw) {
				long start = System.currentTimeMillis();
				this.riffInterface.getFrontBuffer().getGraphics().drawImage(this.riffInterface.backBuffer(), 0, 0, null);
				this.riffInterface.flushQueue();
				this.riffInterface.updateBufferedImage();
				this.riffInterface.repaint();
				long delay = Math.max(0l, this.delay - (System.currentTimeMillis() - start));
				if (!Debugger.getDebugger().isIgnoringThisThread()) {
					delay = 1000;
				}
				Thread.sleep(delay);
			}
		} catch (RuntimeException ex) {
			this.stopLoop();
			this.riffInterface.getFrame().dispose();
			throw ex;
		} catch (InterruptedException ex) {
		}
	}

	public void stopLoop() {
		this.shouldDraw = false;
		this.riffInterface.getEnvironment().stopExecution();
	}
}