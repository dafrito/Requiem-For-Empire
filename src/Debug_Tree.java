import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;

import javax.swing.DefaultListModel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

public class Debug_Tree extends JPanel implements ActionListener, MouseListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8224462858092721477L;
	private Debug_Filter m_filter;
	private boolean m_isReloading = false;
	private JTree m_tree;
	private JPopupMenu m_popup;
	private JMenuItem m_copySelectedNodeGroup, m_copySelectedNodeData;
	private Debug_TreeNode m_currentNode, m_lastChild, m_root;

	// Constructor
	public Debug_Tree(Debug_Filter filter) {
		m_filter = filter;
		setLayout(new GridLayout(1, 0));
		add(m_tree = new JTree(new DefaultTreeModel(m_root = m_currentNode = new Debug_TreeNode(DebugString.OUTPUTTREE))));
		m_popup = new JPopupMenu();
		m_popup.add(m_copySelectedNodeData = new JMenuItem("Copy Selected"));
		m_popup.add(m_copySelectedNodeGroup = new JMenuItem("Copy Selected's Group"));
		m_copySelectedNodeGroup.addActionListener(this);
		m_copySelectedNodeData.addActionListener(this);
		m_tree.addMouseListener(this);
	}

	// ActionListener implementation
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(m_copySelectedNodeGroup)) {
			if (getSelectedNode() != null && getSelectedNode().getGroup() != null) {
				StringSelection ss = new StringSelection(getSelectedNode().getGroup().toString());
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
			}
		} else if (e.getSource().equals(m_copySelectedNodeData)) {
			if (getSelectedNode() != null) {
				StringSelection ss = new StringSelection(getSelectedNode().getData().toString());
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
			}
		}
	}

	// Node shiz
	public void addNode(Debug_TreeNode node) {
		m_currentNode.addChild(node);
		m_lastChild = node;
		int[] array = new int[1];
		array[0] = m_currentNode.getChildCount() - 1;
		((DefaultTreeModel) m_tree.getModel()).nodesWereInserted(m_currentNode, array);
	}

	public void closeNode() {
		m_currentNode = (Debug_TreeNode) m_currentNode.getPracticalParent();
		assert m_currentNode != null : "CurrentNode is null after closeNode - potentially a redundant closenode has caused us to leave the tree.";
		if (m_currentNode.isRoot()) {
			getFilter().setListening(false);
		}
	}

	public Object[] getAvailableFilterArray() {
		if (getSelectedNode() == null || getSelectedNode().getGroup() == null) {
			return null;
		}
		Object[] array = new Object[2];
		array[0] = getSelectedNode().getGroup();
		array[1] = getSelectedNode().getData();
		return array;
	}

	public Debug_TreeNode getCurrentNode() {
		return m_currentNode;
	}

	public Debug_TreeNode getDataByFilter(DefaultListModel data) {
		return m_currentNode.filterByData(data);
	}

	// Quick retrievals
	public Debug_Filter getFilter() {
		return m_filter;
	}

	public Object getFirstAvailableFilter() {
		if (getSelectedNode() == null) {
			return JOptionPane.showInputDialog(null, "Insert Filter Name", "Debug Filter", JOptionPane.INFORMATION_MESSAGE);
		}
		if (getSelectedNode().getGroup() != null) {
			return getSelectedNode().getGroup();
		}
		return getSelectedNode().getData();
	}

	public Debug_TreeNode getLastNodeAdded() {
		return m_lastChild;
	}

	public Debug_TreeNode getSelectedNode() {
		if (m_tree.getSelectionPath() != null) {
			return (Debug_TreeNode) m_tree.getSelectionPath().getLastPathComponent();
		}
		return null;
	}

	// Seminode shiz
	public TreePath getSelectedRelativeTreePath() {
		if (getSelectedNode() == null) {
			return null;
		}
		TreePath path = ((Debug_TreeNode) m_tree.getSelectionPath().getLastPathComponent()).getRelativeTreePath();
		if (((Debug_TreeNode) path.getPath()[0]).getData().equals((getCurrentNode().getRoot().getData()))) {
			JOptionPane.showMessageDialog(null, "This node, or one of its parents, was created during a capture operation and thus has no real parent.", "Invalid Jump", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		return path;
	}

	public TreePath getSelectionPath() {
		return m_tree.getSelectionPath();
	}

	public boolean isReloading() {
		return m_isReloading;
	}

	private void maybeShowPopup(MouseEvent e) {
		if (!e.isPopupTrigger()) {
			return;
		}
		m_popup.show(e.getComponent(), e.getX(), e.getY());
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	// MouseListener implementation
	@Override
	public void mousePressed(MouseEvent e) {
		TreePath path = m_tree.getPathForLocation(e.getX(), e.getY());
		if (path != null) {
			m_tree.setSelectionPath(path);
		}
		maybeShowPopup(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		maybeShowPopup(e);
	}

	public void refresh() {
		((DefaultTreeModel) m_tree.getModel()).setRoot(m_currentNode = getFilter().getListener().getSource().getTreePanel().getDataByFilter(getFilter().getFilters()));
	}

	public void reset() {
		((DefaultTreeModel) m_tree.getModel()).setRoot(m_currentNode = new Debug_TreeNode(DebugString.OUTPUTTREE));
	}

	public void setAsCurrent(Debug_TreeNode node) {
		m_currentNode = node;
		assert m_currentNode != null;
	}

	public void setSelectionPath(TreePath path) {
		m_tree.setSelectionPath(path);
	}

	public void showTreePath(TreePath path) {
		java.util.List<Debug_TreeNode> pathList = new LinkedList<Debug_TreeNode>();
		for (int i = 0; i < path.getPath().length; i++) {
			pathList.add((Debug_TreeNode) path.getPath()[i]);
		}
		Debug_TreeNode node = m_currentNode.getRoot();
		if (!node.equals(pathList.get(0))) {
			assert false : "Invalid path (Roots do not match) Root: " + node.getData() + " OtherRoot: " + pathList.get(0);
		}
		for (int i = 1; i < pathList.size(); i++) {
			Debug_TreeNode pathNode = pathList.get(i); // first child
			if (node.getNode(pathNode) != null) {
				node = node.getNode(pathNode);
				continue;
			}
			if (pathNode.getGroup() == null) {
				assert false : "Invalid Path (PathNode has no group)";
			}
			java.util.List<Debug_TreeNode> groupNodes = node.getNodesByData(pathNode.getGroup());
			boolean flag = false;
			for (Debug_TreeNode groupNode : groupNodes) {
				if (groupNode.getNode(pathNode) != null) {
					pathList.add(i, groupNode);
					i++;
					node = groupNode.getNode(pathNode);
					flag = true;
					break;
				}
			}
			if (!flag) {
				assert false : "Invalid path (GroupNode could not be found)";
			}
		}
		if (path instanceof NamedTreePath) {
			path = new NamedTreePath(((NamedTreePath) path).getName(), pathList.toArray());
		} else {
			path = new TreePath(pathList.toArray());
		}
		m_tree.setSelectionPath(path);
		m_tree.makeVisible(path);
	}
}
