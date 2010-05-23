package com.dafrito.logging;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.dafrito.logging.tree.LogViewTreeNode;

public class LogFilter extends JPanel implements ActionListener {

    private JButton addFilter, removeFilter;
    private DefaultListModel filters = new DefaultListModel();
    private JList filterList;
    private boolean isListening;
    private LogView listener;

    public LogFilter(LogView listener) {
        this.listener = listener;
        setLayout(new BorderLayout());
        JPanel filterButtons = new JPanel();
        add(filterButtons, BorderLayout.NORTH);
        filterButtons.setLayout(new GridLayout(2, 0));
        filterButtons.add(this.addFilter = new JButton("Add Filter"));
        filterButtons.add(this.removeFilter = new JButton("Remove Filter"));
        add(new JScrollPane(this.filterList = new JList(this.filters)));
        this.addFilter.addActionListener(this);
        this.removeFilter.addActionListener(this);
    }

    public DefaultListModel getFilters() {
        return this.filters;
    }

    public LogView getListener() {
        return this.listener;
    }

    public void setListening(boolean listening) {
        this.isListening = listening;
    }

    public boolean isListening() {
        if(getListener().isUnfiltered()) {
            return true;
        }
        if(getListener().isCapturing() || getListener().isSynchronizing()) {
            return this.isListening;
        }
        return false;
    }

    /**
     * Sniffs a node. The method returns true if all filters in this filter
     * allow the given node.
     * <p>
     * This method has the dubious side-effect of activating this filter if the
     * node is allowed.
     * 
     * @param node The node to 'sniff'
     * @return True if this filter is interested in the node; false otherwise
     */
    public boolean sniffNode(LogViewTreeNode node) {
        if(this.isListening == true)
            return true;
        for(int i = 0; i < this.filters.size(); i++) {
            this.isListening = node.getData().equals(this.filters.get(i))
                || (node.getGroup() != null && node.getGroup().equals(this.filters.get(i)));
        }
        return this.isListening;
    }

    public boolean addFilter(Object filter) {
        if(filter == null
            || getListener().getDebugger().isFilterUsed(filter.toString(), this.listener.getThreadName()) != null) {
            return false;
        }
        this.filters.add(this.filters.size(), filter);
        return true;
    }

    public boolean isFilterUsed(Object test) {
        for(int i = 0; i < this.filters.size(); i++) {
            if(this.filters.get(i).equals(test)) {
                return true;
            }
        }
        return false;
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(this.addFilter)) {
            Object data;
            if(getListener().getTreePanel().getSelectedNode() != null) {
                data = JOptionPane.showInputDialog(
                    null,
                    "Insert the name of the filter you wish to add",
                    "Filter Additions",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    getListener().getTreePanel().getFirstAvailableFilter());
            } else {
                data = getListener().getTreePanel().getFirstAvailableFilter();
            }
            if(data != null) {
                addFilter(data);
            }
        } else if(e.getSource().equals(this.removeFilter)) {
            this.filters.remove(this.filters.indexOf(this.filterList.getSelectedValue()));
        }
    }
}
