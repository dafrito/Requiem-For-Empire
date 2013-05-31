package com.dafrito.rfe.gui.debug;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.tree.TreePath;

public class LogPanel extends JPanel {
	private DebugEnvironment debugger;

	private JCheckBox isSynchronized, isCapturing, defaultFilter;
	private JButton sendToFilter, createListener, refresh, jump;

	private Debug_Tree treePanel;
	private HotspotsPanel hotspotPanel;
	private LogPanel source;
	private java.util.List<LogPanel> childOutputs = new LinkedList<LogPanel>();
	protected boolean isListening = false;
	private String threadName;

	public LogPanel(String threadName, DebugEnvironment debugger, LogPanel source, String name) {
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

		createListener = new JButton("Create Listener");
		createListener.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createListener();
			}
		});
		buttons.add(createListener);

		sendToFilter = new JButton("Send to Filter");
		sendToFilter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendToFilter();
			}
		});
		buttons.add(sendToFilter);

		refresh = new JButton("Refresh");
		refresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getTreePanel().refresh();
			}
		});
		buttons.add(refresh);

		isSynchronized = new JCheckBox("Synchronized");
		buttons.add(isSynchronized);
		isSynchronized.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setSynchronized(isSynchronized.isSelected());
			}
		});

		isCapturing = new JCheckBox("Capturing");
		isCapturing.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setCapturing(isCapturing.isSelected());
			}
		});
		buttons.add(isCapturing);

		defaultFilter = new JCheckBox("Default Filter");
		defaultFilter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setFiltering(defaultFilter.isSelected());
			}
		});
		buttons.add(defaultFilter);

		jump = new JButton("Jump to Parent");
		jump.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jump();
			}
		});
		buttons.add(jump);

		if (this.isUnfiltered()) {
			jump.setEnabled(false);
			isSynchronized.setEnabled(false);
			refresh.setEnabled(false);
			isCapturing.setEnabled(false);
			defaultFilter.setEnabled(false);
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
		if (this.debugger.getFilteringOutput() != null && this.debugger.getFilteringOutput().equals(this)) {
			this.debugger.setFilteringOutput(null);
		}
	}

	public void sendToFilter() {
		if (this.debugger.getFilteringOutput() == null) {
			LogPanel listener;
			listener = this.createListener();
			if (listener == null) {
				JOptionPane.showMessageDialog(null, "No filtering output defined.", "Undefined Filter", JOptionPane.WARNING_MESSAGE);
			} else {
				listener.setFiltering(true);
			}
			return;
		}
		Object[] array = null;
		Object value = null;
		Debug_TreeNode selected = this.getTreePanel().getSelectedNode();
		if (selected != null) {
			value = selected.getData().toString();
			if (selected.getGroup() != null) {
				value = selected.getGroup();
				array = new Object[2];
				array[0] = selected.getGroup();
				array[1] = selected.getData();
			}
		}
		Object obj = JOptionPane.showInputDialog(this, "Insert New Filter", "Adding Filter", JOptionPane.QUESTION_MESSAGE, null, array, value);
		if (obj != null) {
			this.debugger.getFilteringOutput().getTreePanel().getFilter().addFilter(obj);
		}
	}

	public void clear() {
		this.hotspotPanel.clear();
		this.treePanel.reset();
	}

	public void setCapturing(boolean isCapturing) {
		this.isCapturing.setSelected(isCapturing);
		if (this.isCapturing.isSelected()) {
			this.refresh.setEnabled(false);
			this.isSynchronized.setEnabled(false);
			this.jump.setEnabled(false);
		} else {
			if (!this.defaultFilter.isSelected()) {
				this.refresh.setEnabled(true);
				this.isSynchronized.setEnabled(true);
			}
			this.jump.setEnabled(true);
		}
	}

	public void setFiltering(boolean isFiltering) {
		this.defaultFilter.setSelected(isFiltering);
		if (this.defaultFilter.isSelected()) {
			if (this.debugger.getFilteringOutput() != null) {
				this.debugger.getFilteringOutput().setFiltering(false);
			}
			this.isSynchronized.setSelected(true);
			this.isSynchronized.setEnabled(false);
			this.debugger.setFilteringOutput(this);
		} else {
			this.debugger.setFilteringOutput(null);
			if (!this.isCapturing.isSelected()) {
				this.isSynchronized.setEnabled(true);
			}
		}
	}

	public void setSource(LogPanel source) {
		this.source = source;
	}

	public boolean isSynchronizing() {
		return this.isSynchronized.isSelected();
	}

	public void setSynchronized(boolean isSynchronized) {
		this.isSynchronized.setSelected(isSynchronized);
	}

	public DebugEnvironment getDebugger() {
		return this.debugger;
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

	public boolean isCapturing() {
		return this.isCapturing.isSelected();
	}

	public boolean isFiltering() {
		return this.defaultFilter.isSelected();
	}

	public boolean isUnfiltered() {
		return this.source == null;
	}

	private static final long serialVersionUID = -6965306788286409632L;
}