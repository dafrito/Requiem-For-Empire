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
		m_threadName = threadName;
		m_treePanel = new Debug_Tree(new Debug_Filter(this));
		m_debugger = debugger;
		m_width = m_debugger.getWidth();
		m_source = source;
		m_name = name;
		setLayout(new BorderLayout());
		// Set up top-of-the-window buttons
		JPanel buttons = new JPanel();
		add(buttons, BorderLayout.NORTH);
		buttons.setLayout(new FlowLayout(FlowLayout.LEFT));
		buttons.add(m_createListener = new JButton("Create Listener"));
		buttons.add(m_sendToFilter = new JButton("Send to Filter"));
		buttons.add(m_refresh = new JButton("Refresh"));
		buttons.add(m_isSynchronized = new JCheckBox("Synchronized"));
		buttons.add(m_isCapturing = new JCheckBox("Capturing"));
		buttons.add(m_defaultFilter = new JCheckBox("Default Filter"));
		buttons.add(m_jump = new JButton("Jump to Parent"));
		// Set up split-pane
		m_treeAndHotspotSplitPane = new JSplitPane();
		add(m_treeAndHotspotSplitPane);
		m_treeAndHotspotSplitPane.setLeftComponent(new JScrollPane(m_treePanel));
		JTabbedPane hotspotAndFiltersPanel = new JTabbedPane();
		m_treeAndHotspotSplitPane.setRightComponent(hotspotAndFiltersPanel);
		// Set up hotspot-panel
		hotspotAndFiltersPanel.add("Hotspots", m_hotspotPanel = new Debug_Hotspots(m_treePanel));
		hotspotAndFiltersPanel.add("Filters", m_treePanel.getFilter());
		if (isUnfiltered()) {
			m_jump.setEnabled(false);
			m_isSynchronized.setEnabled(false);
			m_refresh.setEnabled(false);
			m_isCapturing.setEnabled(false);
			m_defaultFilter.setEnabled(false);
			hotspotAndFiltersPanel.setEnabledAt(1, false);
		}
		// Listeners!
		addComponentListener(this);
		m_sendToFilter.addActionListener(this);
		m_defaultFilter.addActionListener(this);
		m_createListener.addActionListener(this);
		m_refresh.addActionListener(this);
		m_jump.addActionListener(this);
		m_isSynchronized.addActionListener(this);
		m_isCapturing.addActionListener(this);
		setVisible(true);
		m_treeAndHotspotSplitPane.setDividerLocation((int) (m_width * .68));
	}

	// ActionListener implementation
	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource().equals(m_defaultFilter)) {
			setFiltering(m_defaultFilter.isSelected());
		} else if (event.getSource().equals(m_isSynchronized)) {
			setSynchronized(m_isSynchronized.isSelected());
		} else if (event.getSource().equals(m_isCapturing)) {
			setCapturing(m_isCapturing.isSelected());
		} else if (event.getSource().equals(m_createListener)) {
			createListener();
		} else if (event.getSource().equals(m_sendToFilter)) {
			sendToFilter();
		} else if (event.getSource().equals(m_refresh)) {
			getTreePanel().refresh();
		} else if (event.getSource().equals(m_jump)) {
			jump();
		}
	}

	public void addChildOutput(Debug_Listener output) {
		m_childOutputs.add(output);
	}

	// Command functions
	public void clearTab() {
		m_hotspotPanel.reset();
		m_treePanel.reset();
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
		double location = ((double) m_treeAndHotspotSplitPane.getDividerLocation()) / (double) m_width;
		if (location > 1) {
			location = 1;
		}
		m_treeAndHotspotSplitPane.setDividerLocation((int) (getWidth() * location));
		m_width = getWidth();
	}

	@Override
	public void componentShown(ComponentEvent x) {
	}

	public Debug_Listener createListener() {
		return m_debugger.addOutputListener(this, m_treePanel.getFirstAvailableFilter());
	}

	// Quick retrievals
	public Debug_Environment getDebugger() {
		return m_debugger;
	}

	public Debug_Hotspots getHotspotPanel() {
		return m_hotspotPanel;
	}

	public Debug_Listener getSource() {
		return m_source;
	}

	public String getThreadName() {
		return m_threadName;
	}

	public Debug_Tree getTreePanel() {
		return m_treePanel;
	}

	public boolean isCapturing() {
		return m_isCapturing.isSelected();
	}

	public boolean isFiltering() {
		return m_defaultFilter.isSelected();
	}

	public boolean isSynchronizing() {
		return m_isSynchronized.isSelected();
	}

	public boolean isUnfiltered() {
		return m_source == null;
	}

	public void jump() {
		TreePath path = getTreePanel().getSelectedRelativeTreePath();
		m_debugger.focusOnOutput(m_source);
		m_source.getTreePanel().showTreePath(path);
	}

	public Debug_Listener promptCreateListener() {
		Object filter = m_treePanel.getFirstAvailableFilter();
		if (m_treePanel.getSelectedNode() != null) {
			filter = JOptionPane.showInputDialog(this, "Insert Listener Name", "Listener Creation", JOptionPane.PLAIN_MESSAGE, null, null, filter);
		}
		return m_debugger.addOutputListener(this, filter);
	}

	public void removeTab() {
		for (Debug_Listener output : m_childOutputs) {
			output.setSource(m_source);
			output.clearTab();
		}
		if (m_debugger.getFilteringOutput() != null && m_debugger.getFilteringOutput().equals(this)) {
			m_debugger.setFilteringOutput(null);
		}
	}

	public void sendToFilter() {
		if (m_debugger.getFilteringOutput() == null) {
			Debug_Listener listener;
			listener = createListener();
			if (listener == null) {
				JOptionPane.showMessageDialog(null, "No filtering output defined.", "Undefined Filter", JOptionPane.WARNING_MESSAGE);
			}
			listener.setFiltering(true);
			return;
		}
		Object[] array = null;
		Object value = null;
		if (getTreePanel().getSelectedNode() != null) {
			value = getTreePanel().getSelectedNode().getData().toString();
			if (getTreePanel().getSelectedNode().getGroup() != null) {
				value = getTreePanel().getSelectedNode().getGroup();
				array = new Object[2];
				array[0] = getTreePanel().getSelectedNode().getGroup();
				array[1] = getTreePanel().getSelectedNode().getData();
			}
		}
		Object obj = JOptionPane.showInputDialog(this, "Insert New Filter", "Adding Filter", JOptionPane.QUESTION_MESSAGE, null, array, value);
		if (obj != null) {
			m_debugger.getFilteringOutput().getTreePanel().getFilter().addFilter(obj);
		}
	}

	public void setCapturing(boolean isCapturing) {
		m_isCapturing.setSelected(isCapturing);
		if (m_isCapturing.isSelected()) {
			m_refresh.setEnabled(false);
			m_isSynchronized.setEnabled(false);
			m_jump.setEnabled(false);
		} else {
			if (!m_defaultFilter.isSelected()) {
				m_refresh.setEnabled(true);
				m_isSynchronized.setEnabled(true);
			}
			m_jump.setEnabled(true);
		}
	}

	public void setFiltering(boolean isFiltering) {
		m_defaultFilter.setSelected(isFiltering);
		if (m_defaultFilter.isSelected()) {
			if (m_debugger.getFilteringOutput() != null) {
				m_debugger.getFilteringOutput().setFiltering(false);
			}
			m_isSynchronized.setSelected(true);
			m_isSynchronized.setEnabled(false);
			m_debugger.setFilteringOutput(this);
		} else {
			m_debugger.setFilteringOutput(null);
			if (!m_isCapturing.isSelected()) {
				m_isSynchronized.setEnabled(true);
			}
		}
	}

	public void setSource(Debug_Listener source) {
		m_source = source;
	}

	// Setters
	public void setSynchronized(boolean isSynchronized) {
		m_isSynchronized.setSelected(isSynchronized);
	}
}

class TreePathStruct {
	private boolean m_isExpanding;
	private TreePath m_path;

	public TreePathStruct(TreePath path, boolean isExpanding) {
		m_path = path;
		m_isExpanding = isExpanding;
	}

	public TreePath getPath() {
		return m_path;
	}

	public boolean isExpanding() {
		return m_isExpanding;
	}
}
