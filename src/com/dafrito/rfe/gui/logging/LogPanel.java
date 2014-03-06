package com.dafrito.rfe.gui.logging;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

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
import com.dafrito.rfe.logging.ProxyTreeLog;
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

		setParent(parent);
		setSource(source);

		log.addListener(replayLog);

		createTreeBuilder();

	}

	private void setSource(BufferedTreeLog<? extends Message> source) {
		if (sourceLog == source) {
			return;
		}
		if (sourceLog != null) {
			sourceLog.setSink(null);
			sourceLog.setNotifier(null);
		}
		sourceLog = source;
		if (sourceLog != null) {
			sourceLog.setSink(log);
			sourceLog.setNotifier(new Runnable() {
				@Override
				public void run() {
					SwingUtilities.invokeLater(sourceLog);
				}
			});
		}
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

	private LogPanel<Message> createLogPanel(ProxyTreeLog<Message> childSource, String name) {
		BufferedTreeLog<Message> bufferedLog = new BufferedTreeLog<>();
		childSource.setSink(bufferedLog);

		LogPanel<Message> panel = new LogPanel<>(viewer, bufferedLog, name, LogPanel.this);

		children.add(panel);
		viewer.addLogPanel(panel);

		replayLog.play(childSource);
		log.addListener(childSource);

		return panel;
	}

	@SuppressWarnings("unchecked")
	private void expandTree(DefaultMutableTreeNode node, int depth) {
		if (node == null) {
			return;
		}
		logTree.scrollPathToVisible(new TreePath(node.getPath()));
		if (depth > 0) {
			for (Object child : Collections.<Object> list(node.children())) {
				expandTree((DefaultMutableTreeNode) child, depth - 1);
			}
		}
	}

	private JPanel buildButtons() {
		JPanel buttons = new JPanel();
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.LINE_AXIS));

		jump = new JButton("Show in parent");
		jump.setMnemonic(KeyEvent.VK_A);
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

		JButton expandTree = new JButton("Expand Tree");
		expandTree.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				expandTree(getSelectedNode(), 3);
			}
		});
		expandTree.setMnemonic(KeyEvent.VK_X);
		buttons.add(expandTree);

		JButton filterBySubtree = new JButton("Show subtree");
		filterBySubtree.setMnemonic(KeyEvent.VK_W);
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

				viewer.setSelectedLogPanel(createLogPanel(guard, selectedMessage.toString()));
			}
		});
		buttons.add(filterBySubtree);

		JButton filterByMessage = new JButton("Filter by message");
		filterByMessage.setMnemonic(KeyEvent.VK_S);
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

						if (!selectedMessage.getCategory().equals("") && selectedMessage.getCategory().equals(candidate.getCategory())) {
							return true;
						}

						return false;
					}
				});

				String title;
				if (selectedMessage.getCategory() != null) {
					title = selectedMessage.getCategory().toString();
				}
				else {
					title = selectedMessage.getMessage().toString();
				}

				viewer.setSelectedLogPanel(createLogPanel(guard, title));
			}
		});
		buttons.add(filterByMessage);

		JButton filterBySender = new JButton("Filter by sender");
		filterBySender.setMnemonic(KeyEvent.VK_D);
		filterBySender.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final LogMessage<? extends Message> selectedMessage = getSelectedMessage();
				String panelName;

				ScopeGuardedTreeLog<Message> guard = new ScopeGuardedTreeLog<Message>();

				if (selectedMessage != null && selectedMessage.getSender() != null) {
					guard.setGuard(new Predicate<LogMessage<? extends Message>>() {
						@Override
						public boolean test(LogMessage<? extends Message> candidate) {
							return candidate != null && selectedMessage.getSender().equals(candidate.getSender());
						}
					});

					panelName = selectedMessage.getSender().toString();
				} else {
					try {
						String senderPattern = Dialogs.getString("Enter the name of the sender used for this filter");
						final Pattern pattern = Pattern.compile(senderPattern);

						guard.setGuard(new Predicate<LogMessage<? extends Message>>() {
							@Override
							public boolean test(LogMessage<? extends Message> candidate) {
								if (candidate == null || candidate.getSender() == null) {
									return false;
								}
								return pattern.matcher(candidate.getSender().toString()).find();
							}
						});

						panelName = "\"" + senderPattern + "\"";
					} catch (CancelledException ex) {
						return;
					}
				}

				viewer.setSelectedLogPanel(createLogPanel(guard, panelName));
			}
		});
		buttons.add(filterBySender);

		JButton filterByText = new JButton("Filter by text");
		filterByText.setMnemonic(KeyEvent.VK_E);
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

					viewer.setSelectedLogPanel(createLogPanel(guard, filterText));
				} catch (CancelledException e1) {
					return;
				}
			}
		});
		buttons.add(filterByText);

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

	public DefaultMutableTreeNode getSelectedNode() {
		TreePath path = logTree.getSelectionPath();
		if (path == null) {
			return null;
		}
		return (DefaultMutableTreeNode) path.getLastPathComponent();
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

	private DefaultMutableTreeNode getNodeFor(LogMessage<? extends Message> message) {
		if (message == null) {
			throw new NullPointerException("Message must not be null");
		}
		DefaultMutableTreeNode node = treeBuilder.getNodeFor(message);
		if (node == null) {
			throw new IllegalArgumentException("The specified message does not have a corresponding node.");
		}
		return node;
	}

	public void showNode(LogMessage<? extends Message> message) {
		TreePath path = new TreePath(treeBuilder.getModel().getPathToRoot(getNodeFor(message)));
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