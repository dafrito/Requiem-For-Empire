package com.dafrito.rfe.gui.logging;

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

public class Debug_Filter extends JPanel implements ActionListener {
	private JButton addFilter, removeFilter;
	private DefaultListModel<Object> filters = new DefaultListModel<Object>();
	private JList<Object> filterList;
	private LogPanel listener;

	public Debug_Filter(LogPanel listener) {
		this.listener = listener;
		this.setLayout(new BorderLayout());
		JPanel filterButtons = new JPanel();
		this.add(filterButtons, BorderLayout.NORTH);
		filterButtons.setLayout(new GridLayout(filterButtons.getComponentCount(), 0));
		filterButtons.add(this.addFilter = new JButton("Add Filter"));
		filterButtons.add(this.removeFilter = new JButton("Remove Filter"));
		this.add(new JScrollPane(this.filterList = new JList<Object>(this.filters)));
		this.addFilter.addActionListener(this);
		this.removeFilter.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(this.addFilter)) {
			Object data;
			if (this.getListener().getTreePanel().getSelectedNode() != null) {
				data = JOptionPane.showInputDialog(null, "Insert the name of the filter you wish to add", "Filter Additions", JOptionPane.PLAIN_MESSAGE, null, null, this.getListener().getTreePanel().getFirstAvailableFilter());
			} else {
				data = this.getListener().getTreePanel().getFirstAvailableFilter();
			}
			if (data != null) {
				this.addFilter(data);
			}
		} else if (e.getSource().equals(this.removeFilter)) {
			this.filters.remove(this.filters.indexOf(this.filterList.getSelectedValue()));
		}
	}

	public void addFilter(Object filter) {
		this.filters.add(this.filters.size(), filter);
	}

	public DefaultListModel<Object> getFilters() {
		return this.filters;
	}

	public LogPanel getListener() {
		return this.listener;
	}

	public boolean isFilterUsed(Object test) {
		for (int i = 0; i < this.filters.size(); i++) {
			if (this.filters.get(i).equals(test)) {
				return true;
			}
		}
		return false;
	}

	private static final long serialVersionUID = -3002302090734581411L;
}
