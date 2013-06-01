package com.dafrito.rfe;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.bluespot.swing.Components;
import com.dafrito.rfe.gui.debug.DebugEnvironment;

/**
 * Create a new editing environment.
 * 
 * @author Aaron Faanes
 * 
 */
public class EditorRunner implements Runnable {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new EditorRunner());
	}

	@Override
	public void run() {
		if (!SwingUtilities.isEventDispatchThread()) {
			throw new IllegalStateException("Runner must be run from EDT");
		}
		Components.LookAndFeel.GTK.activate();
		JFrame frame = new DebugEnvironment();
		frame.setSize(800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Components.center(frame);
		frame.setVisible(true);
	}
}
