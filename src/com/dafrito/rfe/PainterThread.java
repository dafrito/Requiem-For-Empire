/**
 * 
 */
package com.dafrito.rfe;

class PainterThread extends Thread {
	private Interface riffInterface;
	private static int threadNum = 0;
	private volatile boolean shouldDraw = true;
	private static int delay;

	public PainterThread(Interface riffInterface, int delay) {
		super("Painter " + threadNum++);
		this.riffInterface = riffInterface;
		PainterThread.delay = delay;
	}

	@Override
	public void run() {
		try {
			while (this.shouldDraw) {
				//Debugger.hitStopWatch(Thread.currentThread().getName());
				this.riffInterface.getFrontBuffer().getGraphics().drawImage(this.riffInterface.backBuffer(), 0, 0, null);
				this.riffInterface.flushQueue();
				this.riffInterface.updateBufferedImage();
				this.riffInterface.repaint();
				Thread.sleep(delay);
			}
		} catch (RuntimeException ex) {
			this.stopLoop();
			this.riffInterface.getFrame().dispose();
			throw ex;
		} catch (InterruptedException ex) {
		} finally {
			//Debugger.hitStopWatch(Thread.currentThread().getName());
		}
	}

	public void stopLoop() {
		this.shouldDraw = false;
		this.riffInterface.getEnvironment().stopExecution();
	}
}