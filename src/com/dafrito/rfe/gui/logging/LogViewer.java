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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

import com.bluespot.logic.actors.Actor;
import com.dafrito.rfe.logging.Logs;
import com.dafrito.rfe.logging.CompositeTreeLog;

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

	private final JLabel status = new JLabel();

	private final JMenuBar menuBar = new JMenuBar();

	final Map<String, List<LogPanel<Message>>> filteredOutputMap = new HashMap<String, List<LogPanel<Message>>>();

	public LogViewer() {
		super("RFE Log Viewer");

		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(this.status, BorderLayout.SOUTH);
		this.getContentPane().add(this.logPanelTabs);

		status.setText("No fucking time.");

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

		JMenuItem clearTab = new JMenuItem("Clear Tab", 'C');
		clearTab.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		clearTab.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getSelectedLogPanel().clear();
			}

		});
		listenerMenu.add(clearTab);

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
	private LogPanel<? extends Message> getSelectedLogPanel() {
		return (LogPanel<? extends Message>) logPanelTabs.getSelectedComponent();
	}

	/*
		// TODO This is disabled until we get it implemented properly
		
		public LogPanel<Message> isFilterUsed(Object filter, String threadName) {
			for (LogPanel<Message> listener : this.filteredOutputMap.get(threadName)) {
				if (!listener.isRoot() && listener.getTreePanel().getFilter().isFilterUsed(filter)) {
					return listener;
				}
			}
			return null;
		}

		public LogPanel addOutputListener(LogPanel source, Object filter) {
			if (filter == null || "".equals(filter)) {
				return null;
			}
			if (this.isFilterUsed(filter, source.getThreadName()) != null) {
				JOptionPane.showMessageDialog(this, "An output listener has an identical filter to the one provided.", "Listener Already Exists", JOptionPane.INFORMATION_MESSAGE);
				this.focusOnOutput(this.isFilterUsed(filter, source.getThreadName()));
				return null;
			}
			LogPanel output = new LogPanel(source.getThreadName(), this, source);
			output.getTreePanel().getFilter().addFilter(filter);
			output.getTreePanel().refresh();
			source.addChildOutput(output);
			this.logPanelTabs.add(filter.toString(), output);
			this.logPanelTabs.setSelectedIndex(this.logPanelTabs.getComponentCount() - 1);
			this.filteredOutputMap.get(source.getThreadName()).add(output);
			return output;
		}

		public void focusOnOutput(LogPanel output) {
			assert output != null;
			this.logPanelTabs.setSelectedIndex(this.logPanelTabs.indexOfComponent(output));
		}

		public Debug_TreeNode getUnfilteredCurrentNode() {
			return this.getUnfilteredOutput().getTreePanel().getCurrentNode();
		}

		public LogPanel getUnfilteredOutput() {
			return this.filteredOutputMap.get(Thread.currentThread().getName()).get(0);
		}*/

	public void addLogPanel(CompositeTreeLog<Message> log, String name) {
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
		logPanelTabs.remove(panel);
	}

	private static final long serialVersionUID = 4926830382755122234L;
}
