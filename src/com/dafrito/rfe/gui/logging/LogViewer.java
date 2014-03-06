/**
 * Copyright (c) 2013 Aaron Faanes
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.dafrito.rfe.gui.logging;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

import com.dafrito.rfe.logging.BufferedTreeLog;

/**
 * @author Aaron Faanes
 * @param <Message>
 *            the type of log message
 * 
 */
public class LogViewer<Message> extends JFrame {

	/**
	 * The list of {@link LogPanel}s that are shown by this viewer.
	 */
	private final JTabbedPane logPanelTabs = new JTabbedPane();

	private final JMenuBar menuBar = new JMenuBar();

	final Map<String, List<LogPanel<Message>>> filteredOutputMap = new HashMap<String, List<LogPanel<Message>>>();

	public LogViewer() {
		super("RFE Log Viewer");

		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(this.logPanelTabs);

		this.setJMenuBar(this.menuBar);

		JMenu listenerMenu = new JMenu("Listener");
		this.menuBar.add(listenerMenu);
		listenerMenu.setMnemonic('L');

		JMenuItem renameTab = new JMenuItem("Rename Tab...", 'N');
		renameTab.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Object text = JOptionPane.showInputDialog(
						null,
						"Insert new output name",
						"Rename Output",
						JOptionPane.QUESTION_MESSAGE,
						null,
						null,
						logPanelTabs.getTitleAt(logPanelTabs.getSelectedIndex())
						);
				if (text != null) {
					logPanelTabs.setTitleAt(logPanelTabs.getSelectedIndex(), text.toString());
				}
			}
		});
		listenerMenu.add(renameTab);

		JMenuItem removeTab = new JMenuItem("Remove Tab", 'R');
		removeTab.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
		removeTab.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeLogPanel(getSelectedLogPanel());
			}
		});
		listenerMenu.add(removeTab);
	}

	@SuppressWarnings("unchecked")
	public LogPanel<? extends Message> getSelectedLogPanel() {
		return (LogPanel<? extends Message>) logPanelTabs.getSelectedComponent();
	}

	public void setSelectedLogPanel(LogPanel<? extends Message> panel) {
		logPanelTabs.setSelectedComponent(panel);
	}

	public void addLogPanel(BufferedTreeLog<? extends Message> log, String name) {
		addLogPanel(new LogPanel<Message>(this, log, name));
	}

	/**
	 * @param panel
	 *            the panel to add
	 * @param name
	 *            the name of the new panel
	 */
	public void addLogPanel(LogPanel<Message> panel) {
		if (panel == null) {
			throw new NullPointerException("Panel must not be null");
		}
		logPanelTabs.add(panel);
	}

	public void removeLogPanel(LogPanel<? extends Message> panel) {
		if (panel == null) {
			return;
		}
		panel.prepareToRemove();
		logPanelTabs.remove(panel);
	}

	private static final long serialVersionUID = 4926830382755122234L;
}
