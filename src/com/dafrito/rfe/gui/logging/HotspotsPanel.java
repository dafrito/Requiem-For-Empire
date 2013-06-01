package com.dafrito.rfe.gui.logging;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.TreePath;

import com.dafrito.rfe.strings.NamedTreePath;

/**
 * A list of hotspots. Hotspots are locations that, when clicked, will focus a
 * given debug tree on that location. While manual hotspots can be created, it's
 * more common for them to be automatically created to signal important events
 * (like compilation errors in a compiler.)
 * 
 * @author Aaron Faanes
 * 
 */
public class HotspotsPanel extends JPanel {
	private Debug_Tree treePanel;

	private DefaultListModel<TreePath> hotspotPaths = new DefaultListModel<TreePath>();
	private JList<TreePath> hotspots = new JList<TreePath>(this.hotspotPaths);

	public HotspotsPanel(Debug_Tree tree) {
		this.treePanel = tree;

		setLayout(new BorderLayout());

		this.add(new JScrollPane(this.hotspots));

		JPanel hotspotButtons = new JPanel();
		this.add(hotspotButtons, BorderLayout.NORTH);

		this.hotspots.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					if (hotspots.getSelectedValue() != null) {
						getTreePanel().showTreePath(hotspots.getSelectedValue());
					}
				}
			}
		});

		JButton createHotspot = new JButton("New hotspot...");
		createHotspot.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (getTreePanel().getSelectionPath() == null) {
					return;
				}
				String hotspotName;
				do {
					hotspotName = JOptionPane.showInputDialog(null, "Enter hotspot name", ((Debug_TreeNode) getTreePanel().getSelectionPath().getLastPathComponent()).getData());
					if (hotspotName == null) {
						return;
					}
					if ("".equals(hotspotName)) {
						JOptionPane.showMessageDialog(HotspotsPanel.this, "Please insert a name.");
						hotspotName = null;
					}
				} while (hotspotName == null);
				hotspotPaths.addElement(new NamedTreePath(hotspotName, getTreePanel().getSelectionPath()));
			}
		});
		hotspotButtons.add(createHotspot);

		JButton removeHotspot = new JButton("Remove hotspot");
		removeHotspot.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (hotspots.getSelectedValue() != null) {
					hotspotPaths.remove(hotspots.getSelectedIndex());
				}
			}
		});
		hotspotButtons.add(removeHotspot);

		hotspotButtons.setLayout(new GridLayout(hotspotButtons.getComponentCount(), 0));
	}

	public void addHotspot(Debug_TreeNode node, String name) {
		this.addHotspot(node.getTreePath(name));
	}

	public void addHotspot(TreePath path) {
		this.hotspotPaths.addElement(path);
	}

	public Debug_Tree getTreePanel() {
		return this.treePanel;
	}

	public void clear() {
		this.hotspotPaths.clear();
	}

	private static final long serialVersionUID = -6430758815840883100L;
}
