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
	/**
	 * 
	 */
	private static final long serialVersionUID = -3002302090734581411L;
	private JButton m_addFilter, m_removeFilter;
	private DefaultListModel m_filters = new DefaultListModel();
	private JList m_filterList;
	private boolean m_isListening;
	private Debug_Listener m_listener;

	public Debug_Filter(Debug_Listener listener) {
		this.m_listener = listener;
		this.setLayout(new BorderLayout());
		JPanel filterButtons = new JPanel();
		this.add(filterButtons, BorderLayout.NORTH);
		filterButtons.setLayout(new GridLayout(2, 0));
		filterButtons.add(this.m_addFilter = new JButton("Add Filter"));
		filterButtons.add(this.m_removeFilter = new JButton("Remove Filter"));
		this.add(new JScrollPane(this.m_filterList = new JList(this.m_filters)));
		this.m_addFilter.addActionListener(this);
		this.m_removeFilter.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(this.m_addFilter)) {
			Object data;
			if (this.getListener().getTreePanel().getSelectedNode() != null) {
				data = JOptionPane.showInputDialog(null, "Insert the name of the filter you wish to add", "Filter Additions", JOptionPane.PLAIN_MESSAGE, null, null, this.getListener().getTreePanel().getFirstAvailableFilter());
			} else {
				data = this.getListener().getTreePanel().getFirstAvailableFilter();
			}
			if (data != null) {
				this.addFilter(data);
			}
		} else if (e.getSource().equals(this.m_removeFilter)) {
			this.m_filters.remove(this.m_filters.indexOf(this.m_filterList.getSelectedValue()));
		}
	}

	public boolean addFilter(Object filter) {
		if (filter == null || this.getListener().getDebugger().isFilterUsed(filter.toString(), this.m_listener.getThreadName()) != null) {
			return false;
		}
		this.m_filters.add(this.m_filters.size(), filter);
		return true;
	}

	public DefaultListModel getFilters() {
		return this.m_filters;
	}

	public Debug_Listener getListener() {
		return this.m_listener;
	}

	public boolean isFilterUsed(Object test) {
		for (int i = 0; i < this.m_filters.size(); i++) {
			if (this.m_filters.get(i).equals(test)) {
				return true;
			}
		}
		return false;
	}

	public boolean isListening() {
		if (this.getListener().isUnfiltered()) {
			return true;
		}
		if (this.getListener().isCapturing() || this.getListener().isSynchronizing()) {
			return this.m_isListening;
		}
		return false;
	}

	public void setListening(boolean listening) {
		this.m_isListening = listening;
	}

	public void sniffNode(Debug_TreeNode node) {
		for (int i = 0; i < this.m_filters.size(); i++) {
			if (this.m_isListening == true) {
				return;
			}
			this.m_isListening = node.getData().equals(this.m_filters.get(i)) || (node.getGroup() != null && node.getGroup().equals(this.m_filters.get(i)));
		}
	}
}
