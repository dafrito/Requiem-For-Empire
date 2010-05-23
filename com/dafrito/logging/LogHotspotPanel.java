package com.dafrito.logging;

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

import com.dafrito.logging.tree.LogViewTreePanel;
import com.dafrito.logging.tree.LogViewTreeNode;
import com.dafrito.logging.tree.NamedTreePath;

public class LogHotspotPanel extends JPanel implements ActionListener, MouseListener {

    private LogViewTreePanel m_treePanel;
    private DefaultListModel m_treePaths;
    private JList m_hotspots;
    private JButton m_createHotspot, m_showHotspot, m_removeHotspot;

    public LogHotspotPanel(LogViewTreePanel tree) {
        this.m_treePanel = tree;
        setLayout(new BorderLayout());
        add(new JScrollPane(this.m_hotspots = new JList(this.m_treePaths = new DefaultListModel())));
        // Set up hotspot-buttons
        JPanel hotspotButtons = new JPanel();
        add(hotspotButtons, BorderLayout.NORTH);
        hotspotButtons.setLayout(new GridLayout(3, 0));
        hotspotButtons.add(this.m_showHotspot = new JButton("Show Hotspot"));
        hotspotButtons.add(this.m_createHotspot = new JButton("Create Hotspot"));
        hotspotButtons.add(this.m_removeHotspot = new JButton("Remove Hotspot"));
        this.m_hotspots.addMouseListener(this);
        this.m_showHotspot.addActionListener(this);
        this.m_createHotspot.addActionListener(this);
        this.m_removeHotspot.addActionListener(this);
    }

    public LogViewTreePanel getTreePanel() {
        return this.m_treePanel;
    }

    public void reset() {
        this.m_treePaths.clear();
    }

    public void createHotspot(LogViewTreeNode node, String name) {
        createHotspot(node.getTreePath(name));
    }

    public void createHotspot(TreePath path) {
        this.m_treePaths.addElement(path);
    }

    public void actionPerformed(ActionEvent event) {
        if(event.getSource().equals(this.m_showHotspot)) {
            if(this.m_hotspots.getSelectedValue() != null) {
                getTreePanel().showTreePath((TreePath)this.m_hotspots.getSelectedValue());
            }
        } else if(event.getSource().equals(this.m_createHotspot)) {
            String string;
            if(getTreePanel().getSelectionPath() == null) {
                return;
            }
            do {
                string = JOptionPane.showInputDialog(
                    null,
                    "Enter hotspot name",
                    ((LogViewTreeNode)getTreePanel().getSelectionPath().getLastPathComponent()).getData());
                if(string == null) {
                    return;
                }
                if("".equals(string)) {
                    JOptionPane.showMessageDialog(this, "Please insert a name.");
                    string = null;
                }
            } while (string == null);
            this.m_treePaths.addElement(new NamedTreePath(string, getTreePanel().getSelectionPath()));
        } else if(event.getSource().equals(this.m_removeHotspot)) {
            if(this.m_hotspots.getSelectedValue() != null) {
                this.m_treePaths.remove(this.m_hotspots.getSelectedIndex());
            }
        }
    }

    // TODO Debug_Hotspots should use adapters

    public void mouseEntered(MouseEvent e) {
        // Ignore this event
    }

    public void mouseExited(MouseEvent e) {
        // Ignore this event
    }

    public void mouseClicked(MouseEvent e) {
        if(e.getClickCount() == 2) {
            if(this.m_hotspots.getSelectedValue() != null) {
                getTreePanel().showTreePath((TreePath)this.m_hotspots.getSelectedValue());
            }
        }
    }

    public void mousePressed(MouseEvent e) {
        // Ignore this event
    }

    public void mouseReleased(MouseEvent e) {
        // Ignore this event
    }
}
