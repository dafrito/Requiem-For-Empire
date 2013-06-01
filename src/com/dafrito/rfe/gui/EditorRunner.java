package com.dafrito.rfe.gui;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.bluespot.logic.actors.Actor;
import com.bluespot.swing.Components;
import com.dafrito.rfe.gui.logging.LogViewer;
import com.dafrito.rfe.gui.script.ScriptEditor;
import com.dafrito.rfe.logging.Logs;
import com.dafrito.rfe.logging.ProxyTreeLog;

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
		final LogViewer<Object> logFrame = new LogViewer<Object>();
		logFrame.setSize(800, 600);
		logFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Components.center(logFrame);
		logFrame.setVisible(true);

		Logs.addListener(new Actor<ProxyTreeLog<Object>>() {

			@Override
			public void receive(ProxyTreeLog<Object> log) {
				logFrame.addLogPanel(log, Thread.currentThread().getName());
			}
		});

		JFrame scriptFrame = new ScriptEditor();
		scriptFrame.setSize(800, 600);
		scriptFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		scriptFrame.setLocationRelativeTo(logFrame);
		scriptFrame.setVisible(true);
	}
}
