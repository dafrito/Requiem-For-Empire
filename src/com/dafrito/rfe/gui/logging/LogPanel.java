package com.dafrito.rfe.gui.logging;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.bluespot.logic.predicates.Predicate;
import com.bluespot.swing.Dialogs;
import com.bluespot.swing.Dialogs.CancelledException;
import com.dafrito.rfe.logging.BufferedTreeLog;
import com.dafrito.rfe.logging.CompositeTreeLog;
import com.dafrito.rfe.logging.LogMessage;
import com.dafrito.rfe.logging.ReplayableTreeLog;
import com.dafrito.rfe.logging.ScopeGuardedTreeLog;
import com.dafrito.rfe.logging.TreeBuildingTreeLog;
import com.dafrito.rfe.logging.TreeLog;

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

	private JButton jump;

	private final JTree logTree = new JTree();

	private TreeBuildingTreeLog<Message> treeBuilder;

	private LogPanel<Message> parent;
	private List<LogPanel<Message>> children = new ArrayList<>();

	/**
	 * This log is buffered, so children can safely connect to it as long as
	 * they're on the Swing EDT.
	 */
	private BufferedTreeLog<? extends Message> sourceLog;

	private CompositeTreeLog<Message> log = new CompositeTreeLog<>();

	private ReplayableTreeLog<Message> replayLog = new ReplayableTreeLog<>();

	public LogPanel(LogViewer<Message> viewer, BufferedTreeLog<? extends Message> source) {
		this(viewer, source, "<untitled>");
	}

	public LogPanel(LogViewer<Message> viewer, BufferedTreeLog<? extends Message> source, String name) {
		this(viewer, source, name, null);
	}

	public LogPanel(LogViewer<Message> viewer, BufferedTreeLog<? extends Message> source, String name, LogPanel<Message> parent) {
		this.viewer = viewer;
		setName(name);

		setLayout(new BorderLayout());

		add(buildButtons(), BorderLayout.NORTH);
		add(new JScrollPane(this.logTree), BorderLayout.CENTER);

		// TODO Disabled until we reimplement hotspots
		//			hotspotAndFiltersPanel.setEnabledAt(1, hasParent());

		setParent(parent);
		setupLogs(source);
		createTreeBuilder();
	}

	private void setupLogs(CompositeTreeLog<? extends Message> source) {
		// The ordering here is quite sensitive (to ensure the buffered log generates useful output)
		// so be sure to consider threading issues before making changes. Generally speaking, the buffered
		// log is safe to use if you're on the EDT.
		final BufferedTreeLog<Message> bufferedLog = new BufferedTreeLog<>();
		bufferedLog.setFlushSize(10000);
		bufferedLog.setNotifier(new Runnable() {
			@Override
			public void run() {
				SwingUtilities.invokeLater(bufferedLog);
			}
		});
		bufferedLog.setSink(log);
		source.addListener(bufferedLog);

		log.addListener(replayLog);
	}

	private void createTreeBuilder() {
		if (treeBuilder != null) {
			log.removeListener(treeBuilder);
			logTree.setModel(null);
		}

		treeBuilder = new TreeBuildingTreeLog<Message>(getName());
		logTree.setModel(treeBuilder.getModel());

		// Clean up the display of the root node.
		logTree.setRootVisible(false);
		treeBuilder.getModel().addTreeModelListener(new TreeModelListener() {

			@Override
			public void treeStructureChanged(TreeModelEvent arg0) {
				// Do nothing
			}

			@Override
			public void treeNodesRemoved(TreeModelEvent arg0) {
				// Do nothing
			}

			@Override
			public void treeNodesInserted(final TreeModelEvent event) {
				if (event.getTreePath().getPathCount() > 1) {
					// It's not a direct child of root, so just ignore it.
					return;
				}
				// XXX The invokeLater works around a bug in the Synth look and feel.
				// A correct implementation can invoke expandPath() directly.
				// https://netbeans.org/bugzilla/show_bug.cgi?id=192080
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						logTree.expandPath(event.getTreePath());
					}
				});
			}

			@Override
			public void treeNodesChanged(TreeModelEvent arg0) {
				// Do nothing
			}
		});

		log.addListener(treeBuilder);
	}

	private LogPanel<Message> createLogPanel(CompositeTreeLog<? extends Message> log, String name) {
		LogPanel<Message> panel = new LogPanel<>(viewer, log, name, LogPanel.this);

		children.add(panel);
		viewer.addLogPanel(panel);
		return panel;
	}

	private JPanel buildButtons() {
		JPanel buttons = new JPanel();
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.LINE_AXIS));

		jump = new JButton("Show in parent");
		jump.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (parent == null) {
					return;
				}
				parent.showNode(getSelectedMessage());
				if (viewer.getSelectedLogPanel() == LogPanel.this) {
					viewer.setSelectedLogPanel(parent);
				}
			}
		});
		buttons.add(jump);

		JButton filterByMessage = new JButton("Filter by message");
		filterByMessage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final LogMessage<? extends Message> selectedMessage = getSelectedMessage();
				if (selectedMessage == null) {
					return;
				}

				ScopeGuardedTreeLog<Message> guard = new ScopeGuardedTreeLog<Message>();
				guard.setGuard(new Predicate<LogMessage<? extends Message>>() {

					@Override
					public boolean test(LogMessage<? extends Message> candidate) {
						if (candidate == null) {
							return false;
						}

						if (selectedMessage.getMessage() != null && selectedMessage.getMessage().equals(candidate.getMessage())) {
							return true;
						}

						if (selectedMessage.getCategory() != null && selectedMessage.getCategory().equals(candidate.getCategory())) {
							return true;
						}

						return false;
					}
				});

				CompositeTreeLog<Message> childLog = new CompositeTreeLog<>();
				guard.setSink(childLog);

				String title;
				if (selectedMessage.getCategory() != null) {
					title = selectedMessage.getCategory().toString();
				}
				else {
					title = selectedMessage.getMessage().toString();
				}

				createLogPanel(childLog, title);

				replayLog.play(guard);
				log.addListener(guard);
			}
		});
		buttons.add(filterByMessage);

		JButton filterBySender = new JButton("Filter by sender");
		filterBySender.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final LogMessage<? extends Message> selectedMessage = getSelectedMessage();
				if (selectedMessage == null || selectedMessage.getSender() == null) {
					return;
				}

				ScopeGuardedTreeLog<Message> guard = new ScopeGuardedTreeLog<Message>();
				guard.setGuard(new Predicate<LogMessage<? extends Message>>() {

					@Override
					public boolean test(LogMessage<? extends Message> candidate) {
						return candidate != null && selectedMessage.getSender().equals(candidate.getSender());
					}
				});

				CompositeTreeLog<Message> childLog = new CompositeTreeLog<>();
				guard.setSink(childLog);

				createLogPanel(childLog, selectedMessage.getSender().toString());

				replayLog.play(guard);
				log.addListener(guard);
			}
		});
		buttons.add(filterBySender);

		JButton filterByText = new JButton("Filter by text");
		filterByText.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final LogMessage<? extends Message> selectedMessage = getSelectedMessage();

				try {
					final String filterText = Dialogs.getString("Enter the text used for this filter", selectedMessage != null ? selectedMessage.toString() : "");

					ScopeGuardedTreeLog<Message> guard = new ScopeGuardedTreeLog<Message>();
					guard.setGuard(new Predicate<LogMessage<? extends Message>>() {

						@Override
						public boolean test(LogMessage<? extends Message> candidate) {
							if (candidate == null) {
								return false;
							}
							return candidate.toString().contains(filterText);
						}
					});

					CompositeTreeLog<Message> childLog = new CompositeTreeLog<>();
					guard.setSink(childLog);

					createLogPanel(childLog, filterText);

					replayLog.play(guard);
					log.addListener(guard);
				} catch (CancelledException e1) {
					return;
				}
			}
		});
		buttons.add(filterByText);

		JButton filterBySubtree = new JButton("Filter by subtree");
		filterBySubtree.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final LogMessage<? extends Message> selectedMessage = getSelectedMessage();
				if (selectedMessage == null) {
					return;
				}

				ScopeGuardedTreeLog<Message> guard = new ScopeGuardedTreeLog<Message>();
				guard.setGuard(new Predicate<LogMessage<? extends Message>>() {

					@Override
					public boolean test(LogMessage<? extends Message> candidate) {
						return candidate != null && selectedMessage.equals(candidate);
					}
				});

				CompositeTreeLog<Message> childLog = new CompositeTreeLog<>();
				guard.setSink(childLog);

				createLogPanel(childLog, selectedMessage.toString());

				replayLog.play(guard);
				log.addListener(guard);
			}
		});
		buttons.add(filterBySubtree);

		buttons.add(Box.createHorizontalGlue());

		JButton clear = new JButton("Clear");
		clear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				log.removeListener(replayLog);
				replayLog = new ReplayableTreeLog<>();
				log.addListener(replayLog);
				createTreeBuilder();
			}
		});
		buttons.add(clear);

		return buttons;
	}

	@SuppressWarnings("unchecked")
	public LogMessage<? extends Message> getSelectedMessage() {
		TreePath path = logTree.getSelectionPath();
		if (path == null) {
			return null;
		}
		DefaultMutableTreeNode lastComponent = (DefaultMutableTreeNode) path.getLastPathComponent();
		if (lastComponent == null) {
			return null;
		}
		return (LogMessage<? extends Message>) lastComponent.getUserObject();
	}

	public void showNode(LogMessage<? extends Message> message) {
		if (message == null) {
			return;
		}
		DefaultMutableTreeNode node = treeBuilder.getNodeFor(message);
		if (node == null) {
			return;
		}
		TreePath path = new TreePath(treeBuilder.getModel().getPathToRoot(node));
		logTree.expandPath(path);
		logTree.setSelectionPath(path);
		logTree.scrollPathToVisible(path);
	}

	public void prepareToRemove() {
		for (LogPanel<Message> child : children) {
			child.setParent(this.parent);
		}
	}

	public boolean isRoot() {
		return !hasParent();
	}

	public boolean hasParent() {
		return this.parent != null;
	}

	public void setParent(LogPanel<Message> parent) {
		this.parent = parent;
		if (jump != null) {
			jump.setEnabled(hasParent());
		}
	}

	private static final long serialVersionUID = -6965306788286409632L;
}