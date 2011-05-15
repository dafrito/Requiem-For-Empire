import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.TreePath;

public class Debug_Hotspots extends JPanel implements ActionListener, MouseListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6430758815840883100L;
	private Debug_Tree m_treePanel;
	private DefaultListModel m_treePaths;
	private JList m_hotspots;
	private JButton m_createHotspot, m_showHotspot, m_removeHotspot;

	public Debug_Hotspots(Debug_Tree tree) {
		m_treePanel = tree;
		setLayout(new BorderLayout());
		add(new JScrollPane(m_hotspots = new JList(m_treePaths = new DefaultListModel())));
		// Set up hotspot-buttons
		JPanel hotspotButtons = new JPanel();
		add(hotspotButtons, BorderLayout.NORTH);
		hotspotButtons.setLayout(new GridLayout(3, 0));
		hotspotButtons.add(m_showHotspot = new JButton("Show Hotspot"));
		hotspotButtons.add(m_createHotspot = new JButton("Create Hotspot"));
		hotspotButtons.add(m_removeHotspot = new JButton("Remove Hotspot"));
		m_hotspots.addMouseListener(this);
		m_showHotspot.addActionListener(this);
		m_createHotspot.addActionListener(this);
		m_removeHotspot.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource().equals(m_showHotspot)) {
			if (m_hotspots.getSelectedValue() != null) {
				getTreePanel().showTreePath((TreePath) m_hotspots.getSelectedValue());
			}
		} else if (event.getSource().equals(m_createHotspot)) {
			String string;
			if (getTreePanel().getSelectionPath() == null) {
				return;
			}
			do {
				string = JOptionPane.showInputDialog(null, "Enter hotspot name", ((Debug_TreeNode) getTreePanel().getSelectionPath().getLastPathComponent()).getData());
				if (string == null) {
					return;
				}
				if ("".equals(string)) {
					JOptionPane.showMessageDialog(this, "Please insert a name.");
					string = null;
				}
			} while (string == null);
			m_treePaths.addElement(new NamedTreePath(string, getTreePanel().getSelectionPath()));
		} else if (event.getSource().equals(m_removeHotspot)) {
			if (m_hotspots.getSelectedValue() != null) {
				m_treePaths.remove(m_hotspots.getSelectedIndex());
			}
		}
	}

	public void createHotspot(Debug_TreeNode node, String name) {
		createHotspot(node.getTreePath(name));
	}

	public void createHotspot(TreePath path) {
		m_treePaths.addElement(path);
	}

	public Debug_Tree getTreePanel() {
		return m_treePanel;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			if (m_hotspots.getSelectedValue() != null) {
				getTreePanel().showTreePath((TreePath) m_hotspots.getSelectedValue());
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	public void reset() {
		m_treePaths.clear();
	}
}
