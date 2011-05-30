/**
 * 
 */
package com.dafrito.rfe.gui;

/**
 * A {@link Runnable} that paints the interface.
 * 
 * @author Aaron Faanes
 */
public class PaintInterface implements Runnable {
	private Interface riffInterface;

	public PaintInterface(Interface riffInterface) {
		this.riffInterface = riffInterface;
	}

	@Override
	public void run() {
		this.riffInterface.getFrontBuffer().getGraphics().drawImage(this.riffInterface.backBuffer(), 0, 0, null);
		this.riffInterface.flushQueue();
		this.riffInterface.updateBufferedImage();
		this.riffInterface.repaint();
	}
}
