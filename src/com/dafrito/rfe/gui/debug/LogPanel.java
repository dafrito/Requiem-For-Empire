package com.dafrito.rfe.gui.debug;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.tree.TreePath;

public class LogPanel extends JPanel {
	private LogViewer debugger;

	private JButton createFilter, jump;

	private Debug_Tree treePanel;
	private HotspotsPanel hotspotPanel;
	private LogPanel source;

	private List<LogPanel> childOutputs = new LinkedList<LogPanel>();

	private String threadName;

	public LogPanel(String threadName, LogViewer debugger, LogPanel source) {
		this.threadName = threadName;
		this.treePanel = new Debug_Tree(new Debug_Filter(this));
		this.debugger = debugger;
		this.source = source;

		setLayout(new BorderLayout());

		JPanel buttons = new JPanel();
		buttons.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.add(buttons, BorderLayout.NORTH);

		JSplitPane mainPanel = new JSplitPane();
		mainPanel.setResizeWeight(1d);
		mainPanel.setLeftComponent(new JScrollPane(this.treePanel));
		add(mainPanel);

		JTabbedPane hotspotAndFiltersPanel = new JTabbedPane();
		hotspotAndFiltersPanel.add("Hotspots", this.hotspotPanel = new HotspotsPanel(this.treePanel));
		hotspotAndFiltersPanel.add("Filters", this.treePanel.getFilter());
		mainPanel.setRightComponent(hotspotAndFiltersPanel);

		createFilter = new JButton("Create filter");
		createFilter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createListener();
			}
		});
		buttons.add(createFilter);

		jump = new JButton("Show in parent");
		jump.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jump();
			}
		});
		buttons.add(jump);

		if (this.isUnfiltered()) {
			jump.setEnabled(false);
			hotspotAndFiltersPanel.setEnabledAt(1, false);
		}

		this.setVisible(true);
	}

	public void addChildOutput(LogPanel output) {
		this.childOutputs.add(output);
	}

	public LogPanel createListener() {
		return this.debugger.addOutputListener(this, this.treePanel.getFirstAvailableFilter());
	}

	public void jump() {
		TreePath path = this.getTreePanel().getSelectedRelativeTreePath();
		this.debugger.focusOnOutput(this.source);
		this.source.getTreePanel().showTreePath(path);
	}

	public LogPanel promptCreateListener() {
		Object filter = this.treePanel.getFirstAvailableFilter();
		if (this.treePanel.getSelectedNode() != null) {
			filter = JOptionPane.showInputDialog(this, "Insert Listener Name", "Listener Creation", JOptionPane.PLAIN_MESSAGE, null, null, filter);
		}
		return this.debugger.addOutputListener(this, filter);
	}

	public void removeTab() {
		for (LogPanel output : this.childOutputs) {
			output.setSource(this.source);
			output.clear();
		}
	}

	public void clear() {
		this.hotspotPanel.clear();
		this.treePanel.reset();
	}

	public void setSource(LogPanel source) {
		this.source = source;
	}

	public HotspotsPanel getHotspotPanel() {
		return this.hotspotPanel;
	}

	public LogPanel getSource() {
		return this.source;
	}

	public String getThreadName() {
		return this.threadName;
	}

	public Debug_Tree getTreePanel() {
		return this.treePanel;
	}

	public boolean isUnfiltered() {
		return this.source == null;
	}

	private static final long serialVersionUID = -6965306788286409632L;
}