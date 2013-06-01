package com.dafrito.rfe.gui.logging;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;

import com.dafrito.rfe.logging.BufferedTreeLog;
import com.dafrito.rfe.logging.ProxyTreeLog;
import com.dafrito.rfe.logging.TreeBuildingTreeLog;
import com.dafrito.rfe.strings.NamedTreePath;

/**
 * A panel for a {@link TreeLog}.
 * <p>
 * <b>Hotspots</b> are locations that, when clicked, will focus a given debug
 * tree on that location. While manual hotspots can be created, it's more common
 * for them to be automatically created to signal important events (like
 * compilation errors in a compiler.)
 * 
 * @author Aaron Faanes
 * @param <Message>
 *            the type of message
 * 
 */
public class LogPanel<Message> extends JPanel {
	private LogViewer<Message> viewer;

	private JButton filterByMessage, jump;

	private final JTree logTree = new JTree();

	private BufferedTreeLog<Message> bufferedLog;
	private TreeBuildingTreeLog<Message> treeBuilder;

	private LogPanel<? extends Message> parent;

	private ProxyTreeLog<? extends Message> log;

	private DefaultListModel<TreePath> hotspotPaths = new DefaultListModel<TreePath>();
	private JList<TreePath> hotspots = new JList<TreePath>(this.hotspotPaths);

	public LogPanel(LogViewer<Message> viewer, ProxyTreeLog<? extends Message> log) {
		this(viewer, log, "<untitled>");
	}

	public LogPanel(LogViewer<Message> viewer, ProxyTreeLog<? extends Message> log, String name) {
		this(viewer, log, name, null);
	}

	public LogPanel(LogViewer<Message> viewer, ProxyTreeLog<? extends Message> log, String name, LogPanel<Message> parent) {
		this.viewer = viewer;
		this.parent = parent;
		this.log = log;
		setName(name);

		setLayout(new BorderLayout());

		treeBuilder = new TreeBuildingTreeLog<Message>(name);
		logTree.setModel(treeBuilder.getModel());

		bufferedLog = new BufferedTreeLog<>();
		bufferedLog.setSink(treeBuilder);
		bufferedLog.setNotifier(new Runnable() {
			@Override
			public void run() {
				SwingUtilities.invokeLater(bufferedLog);
			}
		});

		log.addListener(bufferedLog);

		JSplitPane mainPanel = new JSplitPane();
		mainPanel.setContinuousLayout(true);
		mainPanel.setResizeWeight(1d);
		mainPanel.setLeftComponent(new JScrollPane(this.logTree));

		JTabbedPane hotspotAndFiltersPanel = new JTabbedPane();
		hotspotAndFiltersPanel.add("Hotspots", buildHotspotsPanel());
		// TODO Reenable filters
		//		hotspotAndFiltersPanel.add("Filters", this.treePanel.getFilter());
		mainPanel.setRightComponent(hotspotAndFiltersPanel);

		add(buildButtons(), BorderLayout.NORTH);
		add(mainPanel, BorderLayout.CENTER);

		// TODO Disabled until we reimplement hotspots
		//			hotspotAndFiltersPanel.setEnabledAt(1, hasParent());
	}

	private JPanel buildButtons() {
		JPanel buttons = new JPanel();
		buttons.setLayout(new FlowLayout(FlowLayout.LEFT));

		jump = new JButton("Show in parent");
		jump.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jump();
			}
		});
		jump.setEnabled(hasParent());
		buttons.add(jump);

		filterByMessage = new JButton("Filter by message");
		filterByMessage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ProxyTreeLog<Message> childLog = new ProxyTreeLog<>();
				log.addListener(childLog);

				LogPanel<Message> panel = new LogPanel<>(viewer, childLog, "Child of " + treeBuilder.getName());

				viewer.addLogPanel(panel);
			}
		});
		buttons.add(filterByMessage);

		return buttons;
	}

	private JComponent buildHotspotsPanel() {
		JPanel hotspotsPanel = new JPanel();
		hotspotsPanel.setLayout(new BorderLayout());

		hotspotsPanel.add(new JScrollPane(this.hotspots));

		JPanel hotspotButtons = new JPanel();
		hotspotsPanel.add(hotspotButtons, BorderLayout.NORTH);
		// TODO Disabled until we reenable hotspots
		/*
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

				hotspotButtons.setLayout(new GridLayout(hotspotButtons.getComponentCount(), 0));*/

		return new JScrollPane(hotspotsPanel);
	}

	public void jump() {
		//		TreePath path = this.getTreePanel().getSelectedRelativeTreePath();
		//		this.debugger.focusOnOutput(this.source);
		//		this.source.getTreePanel().showTreePath(path);
	}

	//	public void addHotspot(Debug_TreeNode node, String name) {
	//		this.addHotspot(node.getTreePath(name));
	//	}
	//
	//	public void addHotspot(TreePath path) {
	//		this.hotspotPaths.addElement(path);
	//	}

	public void clear() {
		this.hotspotPaths.clear();
		log.removeListener(this.treeBuilder);
		this.treeBuilder = new TreeBuildingTreeLog<>(treeBuilder.getName());
		log.addListener(treeBuilder);
		logTree.setModel(treeBuilder.getModel());
	}

	public ProxyTreeLog<? extends Message> getLog() {
		return log;
	}

	public boolean isRoot() {
		return !hasParent();
	}

	public boolean hasParent() {
		return this.parent != null;
	}

	private static final long serialVersionUID = -6965306788286409632L;
}