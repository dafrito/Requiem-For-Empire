package com.dafrito.logging.tree;

import java.awt.BorderLayout;
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

import com.dafrito.logging.DebugString;
import com.dafrito.logging.LogFilter;

public class LogViewTreePanel extends JPanel implements ActionListener, MouseListener {
    private LogFilter filter;
    private boolean isReloading = false;
    private JTree tree;
    private JPopupMenu popup;
    private JMenuItem copySelectedNodeGroup, copySelectedNodeData;
    private LogViewTreeNode currentNode, lastChild, root;

    // Constructor
    public LogViewTreePanel(LogFilter filter) {
        this.filter = filter;
        setLayout(new BorderLayout());

        this.currentNode = new LogViewTreeNode(DebugString.OUTPUTTREE);
        this.root = this.currentNode;

        this.tree = new JTree(new DefaultTreeModel(this.root));

        add(this.tree, BorderLayout.CENTER);

        this.popup = new JPopupMenu();
        this.popup.add(this.copySelectedNodeData = new JMenuItem("Copy Selected"));
        this.popup.add(this.copySelectedNodeGroup = new JMenuItem("Copy Selected's Group"));
        this.copySelectedNodeGroup.addActionListener(this);
        this.copySelectedNodeData.addActionListener(this);
        this.tree.addMouseListener(this);
    }

    // Quick retrievals
    public LogFilter getFilter() {
        return this.filter;
    }

    public LogViewTreeNode getCurrentNode() {
        return this.currentNode;
    }

    public LogViewTreeNode getLastNodeAdded() {
        return this.lastChild;
    }

    public boolean isReloading() {
        return this.isReloading;
    }

    public LogViewTreeNode getSelectedNode() {
        if(this.tree.getSelectionPath() != null) {
            return (LogViewTreeNode)this.tree.getSelectionPath().getLastPathComponent();
        }
        return null;
    }

    public Object[] getAvailableFilterArray() {
        if(getSelectedNode() == null || getSelectedNode().getGroup() == null) {
            return null;
        }
        Object[] array = new Object[2];
        array[0] = getSelectedNode().getGroup();
        array[1] = getSelectedNode().getData();
        return array;
    }

    public Object getFirstAvailableFilter() {
        if(getSelectedNode() == null) {
            return JOptionPane.showInputDialog(
                null,
                "Insert Filter Name",
                "Debug Filter",
                JOptionPane.INFORMATION_MESSAGE);
        }
        if(getSelectedNode().getGroup() != null) {
            return getSelectedNode().getGroup();
        }
        return getSelectedNode().getData();
    }

    // Node shiz
    public void addNode(LogViewTreeNode node) {
        this.currentNode.addChild(node);
        this.lastChild = node;
        int[] array = new int[1];
        array[0] = this.currentNode.getChildCount() - 1;
        ((DefaultTreeModel)this.tree.getModel()).nodesWereInserted(this.currentNode, array);
    }

    public void closeNode() {
        this.currentNode = (LogViewTreeNode)this.currentNode.getPracticalParent();
        assert this.currentNode != null : "CurrentNode is null after closeNode - potentially a redundant closenode has caused us to leave the tree.";
        if(this.currentNode.isRoot()) {
            getFilter().setListening(false);
        }
    }

    public void setAsCurrent(LogViewTreeNode node) {
        this.currentNode = node;
        assert this.currentNode != null;
    }

    // Seminode shiz
    public TreePath getSelectedRelativeTreePath() {
        if(getSelectedNode() == null) {
            return null;
        }
        TreePath path = ((LogViewTreeNode)this.tree.getSelectionPath().getLastPathComponent()).getRelativeTreePath();
        if(((LogViewTreeNode)path.getPath()[0]).getData().equals((getCurrentNode().getRoot().getData()))) {
            JOptionPane.showMessageDialog(
                null,
                "This node, or one of its parents, was created during a capture operation and thus has no real parent.",
                "Invalid Jump",
                JOptionPane.ERROR_MESSAGE);
            return null;
        }
        return path;
    }

    public LogViewTreeNode getDataByFilter(DefaultListModel data) {
        return this.currentNode.filterByData(data);
    }

    public void showTreePath(TreePath path) {
        java.util.List<LogViewTreeNode> pathList = new LinkedList<LogViewTreeNode>();
        for(int i = 0; i < path.getPath().length; i++) {
            pathList.add((LogViewTreeNode)path.getPath()[i]);
        }
        LogViewTreeNode node = this.currentNode.getRoot();
        assert node.equals(pathList.get(0)) : String.format(
            "Invalid path (Roots do not match) Root: %s OtherRoot: %s",
            node.getData(),
            pathList.get(0));
        for(int i = 1; i < pathList.size(); i++) {
            LogViewTreeNode pathNode = pathList.get(i); // first child
            if(node.getNode(pathNode) != null) {
                node = node.getNode(pathNode);
                continue;
            }
            assert pathNode.getGroup() != null : "Invalid Path (PathNode has no group)";
            java.util.List<LogViewTreeNode> groupNodes = node.getNodesByData(pathNode.getGroup());
            boolean flag = false;
            for(LogViewTreeNode groupNode : groupNodes) {
                if(groupNode.getNode(pathNode) != null) {
                    pathList.add(i, groupNode);
                    i++;
                    node = groupNode.getNode(pathNode);
                    flag = true;
                    break;
                }
            }
            if(!flag) {
                assert false : "Invalid path (GroupNode could not be found)";
            }
        }
        TreePath createdPath = null;
        if(path instanceof NamedTreePath) {
            createdPath = new NamedTreePath(((NamedTreePath)path).getName(), pathList.toArray());
        } else {
            createdPath = new TreePath(pathList.toArray());
        }
        this.tree.setSelectionPath(createdPath);
        this.tree.makeVisible(createdPath);
    }

    public void setSelectionPath(TreePath path) {
        this.tree.setSelectionPath(path);
    }

    public TreePath getSelectionPath() {
        return this.tree.getSelectionPath();
    }

    public void refresh() {
        ((DefaultTreeModel)this.tree.getModel()).setRoot(this.currentNode = getFilter().getListener().getSource().getTreePanel().getDataByFilter(
            getFilter().getFilters()));
    }

    public void reset() {
        ((DefaultTreeModel)this.tree.getModel()).setRoot(this.currentNode = new LogViewTreeNode(DebugString.OUTPUTTREE));
    }

    // ActionListener implementation
    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(this.copySelectedNodeGroup)) {
            if(getSelectedNode() != null && getSelectedNode().getGroup() != null) {
                StringSelection ss = new StringSelection(getSelectedNode().getGroup().toString());
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
            }
        } else if(e.getSource().equals(this.copySelectedNodeData)) {
            if(getSelectedNode() != null) {
                StringSelection ss = new StringSelection(getSelectedNode().getData().toString());
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
            }
        }
    }

    // MouseListener implementation
    public void mousePressed(MouseEvent e) {
        TreePath path = this.tree.getPathForLocation(e.getX(), e.getY());
        if(path != null) {
            this.tree.setSelectionPath(path);
        }
        maybeShowPopup(e);
    }

    public void mouseReleased(MouseEvent e) {
        maybeShowPopup(e);
    }

    private void maybeShowPopup(MouseEvent e) {
        if(!e.isPopupTrigger()) {
            return;
        }
        this.popup.show(e.getComponent(), e.getX(), e.getY());
    }
    
    // TODO Debug_Tree should use adapters

    public void mouseClicked(MouseEvent e) {
        // Ignore this event
    }

    public void mouseEntered(MouseEvent e) {
        // Ignore this event
    }

    public void mouseExited(MouseEvent e) {
        // Ignore this event
    }
}
