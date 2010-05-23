package com.dafrito.logging;

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

import com.dafrito.ide.legacy.CodeEnvironment;
import com.dafrito.logging.tree.LogViewTreePanel;

class TreePathStruct {
    private boolean isExpanding;
    private TreePath path;

    public TreePathStruct(TreePath path, boolean isExpanding) {
        this.path = path;
        this.isExpanding = isExpanding;
    }

    public TreePath getPath() {
        return this.path;
    }

    public boolean isExpanding() {
        return this.isExpanding;
    }
}

public class LogView extends JPanel implements ActionListener {
    // Core stuff
    private CodeEnvironment debugger;
    // GUI stuff
    protected JButton createListener, sendToFilter, refresh, jump;
    protected JCheckBox isSynchronized, isCapturing, defaultFilter;
    private JSplitPane treeAndHotspotSplitPane;
    
    // Debug components and filter-parents and children
    private LogViewTreePanel treePanel;
    private LogHotspotPanel hotspotPanel;
    private LogView source;
    private java.util.List<LogView> childOutputs = new LinkedList<LogView>();
    protected boolean isListening = false;
    
    private String threadName;

    public LogView(String threadName, CodeEnvironment debugger, LogView source, String name) {
        this.setName(name);
        this.threadName = threadName;
        this.treePanel = new LogViewTreePanel(new LogFilter(this));
        this.debugger = debugger;
        this.source = source;
        setLayout(new BorderLayout());
        // Set up top-of-the-window buttons
        JPanel buttons = new JPanel();
        add(buttons, BorderLayout.NORTH);
        buttons.setLayout(new FlowLayout(FlowLayout.LEFT));
        buttons.add(this.createListener = new JButton("Create Listener"));
        buttons.add(this.sendToFilter = new JButton("Send to Filter"));
        buttons.add(this.refresh = new JButton("Refresh"));
        buttons.add(this.isSynchronized = new JCheckBox("Synchronized"));
        buttons.add(this.isCapturing = new JCheckBox("Capturing"));
        buttons.add(this.defaultFilter = new JCheckBox("Default Filter"));
        buttons.add(this.jump = new JButton("Jump to Parent"));
        // Set up split-pane
        this.treeAndHotspotSplitPane = new JSplitPane();
        this.treeAndHotspotSplitPane.setResizeWeight(1);
        add(this.treeAndHotspotSplitPane);
        this.treeAndHotspotSplitPane.setLeftComponent(new JScrollPane(this.treePanel));
        JTabbedPane hotspotAndFiltersPanel = new JTabbedPane();
        this.treeAndHotspotSplitPane.setRightComponent(hotspotAndFiltersPanel);
        // Set up hotspot-panel
        hotspotAndFiltersPanel.add("Hotspots", this.hotspotPanel = new LogHotspotPanel(this.treePanel));
        hotspotAndFiltersPanel.add("Filters", this.treePanel.getFilter());
        if(isUnfiltered()) {
            this.jump.setEnabled(false);
            this.isSynchronized.setEnabled(false);
            this.refresh.setEnabled(false);
            this.isCapturing.setEnabled(false);
            this.defaultFilter.setEnabled(false);
            hotspotAndFiltersPanel.setEnabledAt(1, false);
        }
        // Listeners!
        this.sendToFilter.addActionListener(this);
        this.defaultFilter.addActionListener(this);
        this.createListener.addActionListener(this);
        this.refresh.addActionListener(this);
        this.jump.addActionListener(this);
        this.isSynchronized.addActionListener(this);
        this.isCapturing.addActionListener(this);
        setVisible(true);
    }
    
    public String getThreadName() {
        return this.threadName;
    }

    public CodeEnvironment getDebugger() {
        return this.debugger;
    }

    public LogHotspotPanel getHotspotPanel() {
        return this.hotspotPanel;
    }

    public LogViewTreePanel getTreePanel() {
        return this.treePanel;
    }

    public boolean isSynchronizing() {
        return this.isSynchronized.isSelected();
    }

    public boolean isFiltering() {
        return this.defaultFilter.isSelected();
    }

    public boolean isCapturing() {
        return this.isCapturing.isSelected();
    }

    public boolean isUnfiltered() {
        return this.source == null;
    }

    public LogView getSource() {
        return this.source;
    }

    // Setters
    public void setSynchronized(boolean isSynchronized) {
        this.isSynchronized.setSelected(isSynchronized);
    }

    public void setFiltering(boolean isFiltering) {
        this.defaultFilter.setSelected(isFiltering);
        if(this.defaultFilter.isSelected()) {
            if(this.debugger.getFilteringOutput() != null) {
                this.debugger.getFilteringOutput().setFiltering(false);
            }
            this.isSynchronized.setSelected(true);
            this.isSynchronized.setEnabled(false);
            this.debugger.setFilteringOutput(this);
        } else {
            this.debugger.setFilteringOutput(null);
            if(!this.isCapturing.isSelected()) {
                this.isSynchronized.setEnabled(true);
            }
        }
    }

    public void setCapturing(boolean isCapturing) {
        this.isCapturing.setSelected(isCapturing);
        if(this.isCapturing.isSelected()) {
            this.refresh.setEnabled(false);
            this.isSynchronized.setEnabled(false);
            this.jump.setEnabled(false);
        } else {
            if(!this.defaultFilter.isSelected()) {
                this.refresh.setEnabled(true);
                this.isSynchronized.setEnabled(true);
            }
            this.jump.setEnabled(true);
        }
    }

    public void setSource(LogView source) {
        this.source = source;
    }

    // Command functions
    public void clearTab() {
        this.hotspotPanel.reset();
        this.treePanel.reset();
    }

    public void removeTab() {
        for(LogView output : this.childOutputs) {
            output.setSource(this.source);
            output.clearTab();
        }
        if(this.debugger.getFilteringOutput() != null && this.debugger.getFilteringOutput().equals(this)) {
            this.debugger.setFilteringOutput(null);
        }
    }

    public LogView promptCreateListener() {
        Object filter = this.treePanel.getFirstAvailableFilter();
        if(this.treePanel.getSelectedNode() != null) {
            filter = JOptionPane.showInputDialog(
                this,
                "Insert Listener Name",
                "Listener Creation",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                filter);
        }
        return this.debugger.addOutputListener(this, filter);
    }

    public LogView createListener() {
        return this.debugger.addOutputListener(this, this.treePanel.getFirstAvailableFilter());
    }

    public void addChildOutput(LogView output) {
        this.childOutputs.add(output);
    }

    public void sendToFilter() {
        if(this.debugger.getFilteringOutput() == null) {
            LogView listener;
            listener = createListener();
            if(listener != null) {
                listener.setFiltering(true);
            } else {
                JOptionPane.showMessageDialog(
                    null,
                    "No filtering output defined.",
                    "Undefined Filter",
                    JOptionPane.WARNING_MESSAGE);
            }
            return;
        }
        Object[] array = null;
        Object value = null;
        if(getTreePanel().getSelectedNode() != null) {
            value = getTreePanel().getSelectedNode().getData().toString();
            if(getTreePanel().getSelectedNode().getGroup() != null) {
                value = getTreePanel().getSelectedNode().getGroup();
                array = new Object[2];
                array[0] = getTreePanel().getSelectedNode().getGroup();
                array[1] = getTreePanel().getSelectedNode().getData();
            }
        }
        Object obj = JOptionPane.showInputDialog(
            this,
            "Insert New Filter",
            "Adding Filter",
            JOptionPane.QUESTION_MESSAGE,
            null,
            array,
            value);
        if(obj != null) {
            this.debugger.getFilteringOutput().getTreePanel().getFilter().addFilter(obj);
        }
    }

    public void jump() {
        TreePath path = getTreePanel().getSelectedRelativeTreePath();
        this.debugger.focusOnOutput(this.source);
        this.source.getTreePanel().showTreePath(path);
    }

    // ActionListener implementation
    public void actionPerformed(ActionEvent event) {
        if(event.getSource().equals(this.defaultFilter)) {
            setFiltering(this.defaultFilter.isSelected());
        } else if(event.getSource().equals(this.isSynchronized)) {
            setSynchronized(this.isSynchronized.isSelected());
        } else if(event.getSource().equals(this.isCapturing)) {
            setCapturing(this.isCapturing.isSelected());
        } else if(event.getSource().equals(this.createListener)) {
            createListener();
        } else if(event.getSource().equals(this.sendToFilter)) {
            sendToFilter();
        } else if(event.getSource().equals(this.refresh)) {
            getTreePanel().refresh();
        } else if(event.getSource().equals(this.jump)) {
            jump();
        }
    }

}
