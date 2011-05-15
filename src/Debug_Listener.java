import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.tree.TreePath;

public class Debug_Listener extends JPanel implements ActionListener, ComponentListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6965306788286409632L;
	// Core stuff
	private Debug_Environment m_debugger;
	private int m_width;
	// GUI stuff
	private String m_name;
	protected JButton m_createListener, m_sendToFilter, m_refresh, m_jump;
	protected JCheckBox m_isSynchronized, m_isCapturing, m_defaultFilter;
	private JSplitPane m_treeAndHotspotSplitPane;
	// Debug components and filter-parents and children
	private Debug_Tree m_treePanel;
	private Debug_Hotspots m_hotspotPanel;
	private Debug_Listener m_source;
	private java.util.List<Debug_Listener> m_childOutputs = new LinkedList<Debug_Listener>();
	protected boolean m_isListening = false;
	// CONSTRUCTOR!!
	private String m_threadName;

	public Debug_Listener(String threadName, Debug_Environment debugger, Debug_Listener source, String name) {
		this.m_threadName = threadName;
		this.m_treePanel = new Debug_Tree(new Debug_Filter(this));
		this.m_debugger = debugger;
		this.m_width = this.m_debugger.getWidth();
		this.m_source = source;
		this.m_name = name;
		this.setLayout(new BorderLayout());
		// Set up top-of-the-window buttons
		JPanel buttons = new JPanel();
		this.add(buttons, BorderLayout.NORTH);
		buttons.setLayout(new FlowLayout(FlowLayout.LEFT));
		buttons.add(this.m_createListener = new JButton("Create Listener"));
		buttons.add(this.m_sendToFilter = new JButton("Send to Filter"));
		buttons.add(this.m_refresh = new JButton("Refresh"));
		buttons.add(this.m_isSynchronized = new JCheckBox("Synchronized"));
		buttons.add(this.m_isCapturing = new JCheckBox("Capturing"));
		buttons.add(this.m_defaultFilter = new JCheckBox("Default Filter"));
		buttons.add(this.m_jump = new JButton("Jump to Parent"));
		// Set up split-pane
		this.m_treeAndHotspotSplitPane = new JSplitPane();
		this.add(this.m_treeAndHotspotSplitPane);
		this.m_treeAndHotspotSplitPane.setLeftComponent(new JScrollPane(this.m_treePanel));
		JTabbedPane hotspotAndFiltersPanel = new JTabbedPane();
		this.m_treeAndHotspotSplitPane.setRightComponent(hotspotAndFiltersPanel);
		// Set up hotspot-panel
		hotspotAndFiltersPanel.add("Hotspots", this.m_hotspotPanel = new Debug_Hotspots(this.m_treePanel));
		hotspotAndFiltersPanel.add("Filters", this.m_treePanel.getFilter());
		if (this.isUnfiltered()) {
			this.m_jump.setEnabled(false);
			this.m_isSynchronized.setEnabled(false);
			this.m_refresh.setEnabled(false);
			this.m_isCapturing.setEnabled(false);
			this.m_defaultFilter.setEnabled(false);
			hotspotAndFiltersPanel.setEnabledAt(1, false);
		}
		// Listeners!
		this.addComponentListener(this);
		this.m_sendToFilter.addActionListener(this);
		this.m_defaultFilter.addActionListener(this);
		this.m_createListener.addActionListener(this);
		this.m_refresh.addActionListener(this);
		this.m_jump.addActionListener(this);
		this.m_isSynchronized.addActionListener(this);
		this.m_isCapturing.addActionListener(this);
		this.setVisible(true);
		this.m_treeAndHotspotSplitPane.setDividerLocation((int) (this.m_width * .68));
	}

	// ActionListener implementation
	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource().equals(this.m_defaultFilter)) {
			this.setFiltering(this.m_defaultFilter.isSelected());
		} else if (event.getSource().equals(this.m_isSynchronized)) {
			this.setSynchronized(this.m_isSynchronized.isSelected());
		} else if (event.getSource().equals(this.m_isCapturing)) {
			this.setCapturing(this.m_isCapturing.isSelected());
		} else if (event.getSource().equals(this.m_createListener)) {
			this.createListener();
		} else if (event.getSource().equals(this.m_sendToFilter)) {
			this.sendToFilter();
		} else if (event.getSource().equals(this.m_refresh)) {
			this.getTreePanel().refresh();
		} else if (event.getSource().equals(this.m_jump)) {
			this.jump();
		}
	}

	public void addChildOutput(Debug_Listener output) {
		this.m_childOutputs.add(output);
	}

	// Command functions
	public void clearTab() {
		this.m_hotspotPanel.reset();
		this.m_treePanel.reset();
		System.gc();
	}

	@Override
	public void componentHidden(ComponentEvent x) {
	}

	@Override
	public void componentMoved(ComponentEvent x) {
	}

	// ComponentListener implementation
	@Override
	public void componentResized(ComponentEvent x) {
		double location = ((double) this.m_treeAndHotspotSplitPane.getDividerLocation()) / (double) this.m_width;
		if (location > 1) {
			location = 1;
		}
		this.m_treeAndHotspotSplitPane.setDividerLocation((int) (this.getWidth() * location));
		this.m_width = this.getWidth();
	}

	@Override
	public void componentShown(ComponentEvent x) {
	}

	public Debug_Listener createListener() {
		return this.m_debugger.addOutputListener(this, this.m_treePanel.getFirstAvailableFilter());
	}

	// Quick retrievals
	public Debug_Environment getDebugger() {
		return this.m_debugger;
	}

	public Debug_Hotspots getHotspotPanel() {
		return this.m_hotspotPanel;
	}

	public Debug_Listener getSource() {
		return this.m_source;
	}

	public String getThreadName() {
		return this.m_threadName;
	}

	public Debug_Tree getTreePanel() {
		return this.m_treePanel;
	}

	public boolean isCapturing() {
		return this.m_isCapturing.isSelected();
	}

	public boolean isFiltering() {
		return this.m_defaultFilter.isSelected();
	}

	public boolean isSynchronizing() {
		return this.m_isSynchronized.isSelected();
	}

	public boolean isUnfiltered() {
		return this.m_source == null;
	}

	public void jump() {
		TreePath path = this.getTreePanel().getSelectedRelativeTreePath();
		this.m_debugger.focusOnOutput(this.m_source);
		this.m_source.getTreePanel().showTreePath(path);
	}

	public Debug_Listener promptCreateListener() {
		Object filter = this.m_treePanel.getFirstAvailableFilter();
		if (this.m_treePanel.getSelectedNode() != null) {
			filter = JOptionPane.showInputDialog(this, "Insert Listener Name", "Listener Creation", JOptionPane.PLAIN_MESSAGE, null, null, filter);
		}
		return this.m_debugger.addOutputListener(this, filter);
	}

	public void removeTab() {
		for (Debug_Listener output : this.m_childOutputs) {
			output.setSource(this.m_source);
			output.clearTab();
		}
		if (this.m_debugger.getFilteringOutput() != null && this.m_debugger.getFilteringOutput().equals(this)) {
			this.m_debugger.setFilteringOutput(null);
		}
	}

	public void sendToFilter() {
		if (this.m_debugger.getFilteringOutput() == null) {
			Debug_Listener listener;
			listener = this.createListener();
			if (listener == null) {
				JOptionPane.showMessageDialog(null, "No filtering output defined.", "Undefined Filter", JOptionPane.WARNING_MESSAGE);
			}
			listener.setFiltering(true);
			return;
		}
		Object[] array = null;
		Object value = null;
		if (this.getTreePanel().getSelectedNode() != null) {
			value = this.getTreePanel().getSelectedNode().getData().toString();
			if (this.getTreePanel().getSelectedNode().getGroup() != null) {
				value = this.getTreePanel().getSelectedNode().getGroup();
				array = new Object[2];
				array[0] = this.getTreePanel().getSelectedNode().getGroup();
				array[1] = this.getTreePanel().getSelectedNode().getData();
			}
		}
		Object obj = JOptionPane.showInputDialog(this, "Insert New Filter", "Adding Filter", JOptionPane.QUESTION_MESSAGE, null, array, value);
		if (obj != null) {
			this.m_debugger.getFilteringOutput().getTreePanel().getFilter().addFilter(obj);
		}
	}

	public void setCapturing(boolean isCapturing) {
		this.m_isCapturing.setSelected(isCapturing);
		if (this.m_isCapturing.isSelected()) {
			this.m_refresh.setEnabled(false);
			this.m_isSynchronized.setEnabled(false);
			this.m_jump.setEnabled(false);
		} else {
			if (!this.m_defaultFilter.isSelected()) {
				this.m_refresh.setEnabled(true);
				this.m_isSynchronized.setEnabled(true);
			}
			this.m_jump.setEnabled(true);
		}
	}

	public void setFiltering(boolean isFiltering) {
		this.m_defaultFilter.setSelected(isFiltering);
		if (this.m_defaultFilter.isSelected()) {
			if (this.m_debugger.getFilteringOutput() != null) {
				this.m_debugger.getFilteringOutput().setFiltering(false);
			}
			this.m_isSynchronized.setSelected(true);
			this.m_isSynchronized.setEnabled(false);
			this.m_debugger.setFilteringOutput(this);
		} else {
			this.m_debugger.setFilteringOutput(null);
			if (!this.m_isCapturing.isSelected()) {
				this.m_isSynchronized.setEnabled(true);
			}
		}
	}

	public void setSource(Debug_Listener source) {
		this.m_source = source;
	}

	// Setters
	public void setSynchronized(boolean isSynchronized) {
		this.m_isSynchronized.setSelected(isSynchronized);
	}
}

class TreePathStruct {
	private boolean m_isExpanding;
	private TreePath m_path;

	public TreePathStruct(TreePath path, boolean isExpanding) {
		this.m_path = path;
		this.m_isExpanding = isExpanding;
	}

	public TreePath getPath() {
		return this.m_path;
	}

	public boolean isExpanding() {
		return this.m_isExpanding;
	}
}
