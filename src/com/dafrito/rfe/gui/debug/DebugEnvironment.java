package com.dafrito.rfe.gui.debug;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.dafrito.rfe.geom.PolygonPipeline;
import com.dafrito.rfe.geom.SplitterThread;
import com.dafrito.rfe.gui.script.ScriptPanel;
import com.dafrito.rfe.script.CompileThread;
import com.dafrito.rfe.script.ExecutionThread;
import com.dafrito.rfe.script.ScriptEnvironment;
import com.dafrito.rfe.script.exceptions.ScriptException;
import com.dafrito.rfe.strings.ExtensionFilter;

/**
 * An environment for running RFE programs.
 * 
 * @author Aaron Faanes
 * 
 */
public class DebugEnvironment extends JFrame implements ActionListener, ChangeListener {
	private static final long serialVersionUID = -8190546125680224912L;

	private final JTabbedPane tabbedPane = new JTabbedPane();
	final JTabbedPane filteredPanes = new JTabbedPane();
	private final JLabel status = new JLabel();

	private final JMenuBar menuBar = new JMenuBar();

	private final JMenuItem newFile = new JMenuItem("New", 'N');
	private final JMenuItem openFile = new JMenuItem("Open...", 'O');
	private final JMenuItem closeFile = new JMenuItem("Close");
	private final JMenuItem saveFile = new JMenuItem("Save", 'S');
	private final JMenuItem saveFileAs = new JMenuItem("Save As...", 'A');
	private final JMenuItem report = new JMenuItem("Report", 'T');
	private final JMenuItem reset = new JMenuItem("Reset");
	private final JMenuItem exit = new JMenuItem("Exit", 'X');

	/**
	 * A menu that contains simple undo and redo actions.
	 */
	private final JMenu editMenu = new JMenu("Edit");
	private final JMenuItem undo = new JMenuItem("Undo", 'U');
	private final JMenuItem redo = new JMenuItem("Redo", 'R');

	/**
	 * A menu that contains script compilation and execution actions.
	 */
	private final JMenu parserMenu = new JMenu("Parser");
	private final JMenuItem compile = new JMenuItem("Compile", 'C');
	private final JMenuItem execute = new JMenuItem("Execute", 'X');
	private final JMenuItem compileAndRun = new JMenuItem("Compile and Run", 'R');

	/**
	 * A menu of actions that add, modify, and remove debug listeners.
	 */
	private final JMenu listenerMenu = new JMenu("Listener");
	private final JMenuItem removeTab;
	private final JMenuItem clearTab;
	private final JMenuItem createListener;
	private final JMenuItem renameTab;
	private final JMenuItem addException;
	private final JMenuItem addExceptionFromList;
	private final JMenuItem addIgnore;
	private final JMenuItem addIgnoreFromList;
	private final JMenuItem removeException;
	private final JMenuItem removeIgnore;
	private final JRadioButtonMenuItem exceptionsMode;
	private final JRadioButtonMenuItem ignoreMode;

	private final LogTreePanel<Object> logPanel = new LogTreePanel<Object>("Log Panel");

	private final List<ScriptPanel> scriptElements = new ArrayList<ScriptPanel>();
	private LogPanel filtering;
	final Map<String, List<LogPanel>> filteredOutputMap = new HashMap<String, List<LogPanel>>();
	private String priorityExecutingClass;

	private final Set<String> ignores = new HashSet<String>();

	private final Set<String> exceptions = new HashSet<String>();

	private final Set<String> knownThreads = new HashSet<String>();

	private void populateKnownThreads() {
		this.knownThreads.add("AWT-EventQueue");
		this.knownThreads.add(CompileThread.COMPILETHREADSTRING);
		this.knownThreads.add(ExecutionThread.EXECUTIONTHREADSTRING);
		this.knownThreads.add(PolygonPipeline.POLYGONPIPELINESTRING);
		this.knownThreads.add(SplitterThread.SPLITTERTHREADSTRING);
	}

	private ScriptEnvironment scriptEnvironment;

	public DebugEnvironment(int width, int height) {
		super("RFE Debugger");
		this.setSize(width, height);
		Debugger.setDebugger(this);
		this.populateKnownThreads();

		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(this.status, BorderLayout.SOUTH);
		this.getContentPane().add(this.tabbedPane);
		this.tabbedPane.addChangeListener(this);

		this.setJMenuBar(this.menuBar);

		JMenu fileMenu = new JMenu("File");
		this.menuBar.add(fileMenu);
		fileMenu.setMnemonic('F');

		fileMenu.add(this.newFile);
		this.newFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		fileMenu.add(this.openFile);
		this.newFile.addActionListener(this);

		this.openFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		fileMenu.add(this.closeFile);
		this.openFile.addActionListener(this);

		this.closeFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
		this.closeFile.addActionListener(this);

		fileMenu.add(this.saveFile);
		this.saveFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		this.saveFile.addActionListener(this);

		fileMenu.add(this.saveFileAs);
		this.saveFileAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		this.saveFileAs.addActionListener(this);

		fileMenu.add(this.report);
		this.report.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0));
		this.report.addActionListener(this);

		fileMenu.add(this.reset);
		this.reset.addActionListener(this);

		fileMenu.add(this.exit);
		this.exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		this.exit.addActionListener(this);

		this.menuBar.add(this.editMenu);
		this.editMenu.setMnemonic('E');

		this.editMenu.add(this.undo);
		this.undo.addActionListener(this);
		this.undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));

		this.editMenu.add(this.redo);
		this.redo.addActionListener(this);
		this.redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));

		this.parserMenu.setMnemonic('P');
		this.menuBar.add(this.parserMenu);

		this.parserMenu.add(this.compile);
		this.compile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
		this.compile.addActionListener(this);

		this.parserMenu.add(this.execute);
		this.execute.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0));
		this.execute.addActionListener(this);
		this.execute.setEnabled(false);

		this.parserMenu.add(this.compileAndRun);
		this.compileAndRun.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, ActionEvent.CTRL_MASK));
		this.compileAndRun.addActionListener(this);

		JMenu debugMenu = new JMenu("Debugger");
		this.menuBar.add(debugMenu);

		ButtonGroup group = new ButtonGroup();
		debugMenu.add(this.exceptionsMode = new JRadioButtonMenuItem("Lazy Filter Mode"));
		group.add(this.exceptionsMode);
		debugMenu.add(this.ignoreMode = new JRadioButtonMenuItem("Greedy Filter Mode"));
		group.add(this.ignoreMode);
		this.exceptionsMode.setSelected(true);

		debugMenu.addSeparator();
		debugMenu.add(this.addException = new JMenuItem("Add Exception"));
		this.addException.addActionListener(this);

		debugMenu.add(this.addExceptionFromList = new JMenuItem("Add Exception From List..."));
		this.addExceptionFromList.addActionListener(this);

		debugMenu.add(this.removeException = new JMenuItem("Remove Exception..."));
		this.removeException.addActionListener(this);

		debugMenu.addSeparator();

		debugMenu.add(this.addIgnore = new JMenuItem("Add to Ignore List"));
		this.addIgnore.addActionListener(this);
		debugMenu.add(this.addIgnoreFromList = new JMenuItem("Add Ignore From List..."));
		this.addIgnoreFromList.addActionListener(this);
		debugMenu.add(this.removeIgnore = new JMenuItem("Remove Ignore..."));
		this.removeIgnore.addActionListener(this);
		debugMenu.addSeparator();

		this.menuBar.add(this.listenerMenu);
		this.listenerMenu.setMnemonic('L');
		this.listenerMenu.add(this.createListener = new JMenuItem("Create Listener...", 'C'));
		this.createListener.addActionListener(this);
		this.listenerMenu.add(this.renameTab = new JMenuItem("Rename Tab...", 'N'));
		this.renameTab.addActionListener(this);
		this.listenerMenu.add(this.clearTab = new JMenuItem("Clear Tab", 'C'));
		this.clearTab.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		this.clearTab.addActionListener(this);
		this.listenerMenu.add(this.removeTab = new JMenuItem("Remove Tab", 'R'));
		this.removeTab.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
		this.removeTab.addActionListener(this);

		this.tabbedPane.add("Debug Output", this.filteredPanes);
		this.filteredPanes.add("Log Output", this.logPanel);
		this.filteredPanes.addChangeListener(this);

		// Open all valid scripts in our working directory
		File folder = new File(".");
		ExtensionFilter filter = new ExtensionFilter();
		filter.addExtension("RiffScript");
		File[] files = folder.listFiles(filter);
		for (File file : files) {
			if (file.isFile()) {
				this.addReferenced(new ScriptPanel(this, file));
			}
		}
	}

	// Listeners
	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getActionCommand().equals("Exit")) {
			java.util.List<ScriptPanel> removedElements = new LinkedList<ScriptPanel>();
			int index = this.tabbedPane.getSelectedIndex();
			for (; this.scriptElements.size() > 0;) {
				ScriptPanel element = this.scriptElements.get(0);
				if (!element.closeFile()) {
					for (ScriptPanel added : removedElements) {
						this.scriptElements.add(0, added);
						this.tabbedPane.add(added, 1);
					}
					this.tabbedPane.setSelectedIndex(index);
					return;
				}
				this.tabbedPane.remove(1);
				this.scriptElements.remove(0);
				removedElements.add(element);
			}
			System.exit(0);
		} else if (event.getSource().equals(this.newFile)) {
			this.addReferenced(new ScriptPanel(this, (String) null));
		} else if (event.getSource().equals(this.openFile)) {
			ScriptPanel element = new ScriptPanel(this);
			if (element.hasFile()) {
				this.addReferenced(element);
			}
		} else if (event.getSource().equals(this.closeFile)) {
			if ((this.scriptElements.get(this.tabbedPane.getSelectedIndex() - 1)).closeFile()) {
				int index = this.tabbedPane.getSelectedIndex();
				this.tabbedPane.remove(index);
				this.scriptElements.remove(index - 1);
			}
		} else if (event.getSource().equals(this.renameTab)) {
			Object text = JOptionPane.showInputDialog(null, "Insert new output name", "Rename Output", JOptionPane.QUESTION_MESSAGE, null, null, this.filteredPanes.getTitleAt(this.filteredPanes.getSelectedIndex()));
			if (text != null) {
				this.filteredPanes.setTitleAt(this.filteredPanes.getSelectedIndex(), text.toString());
			}
		} else if (event.getSource().equals(this.createListener)) {
			((LogPanel) this.filteredPanes.getSelectedComponent()).promptCreateListener();
		} else if (event.getSource().equals(this.saveFile)) {
			this.scriptElements.get(this.tabbedPane.getSelectedIndex() - 1).saveFile();
		} else if (event.getSource().equals(this.reset)) {
			this.reset();
		} else if (event.getSource().equals(this.clearTab)) {
			((LogPanel) this.filteredPanes.getSelectedComponent()).clear();
		} else if (event.getSource().equals(this.removeTab)) {
			((LogPanel) this.filteredPanes.getSelectedComponent()).removeTab();
			this.filteredOutputMap.get(((LogPanel) this.filteredPanes.getSelectedComponent()).getThreadName()).remove(this.filteredPanes.getSelectedComponent());
			this.filteredPanes.remove(this.filteredPanes.getSelectedComponent());
		} else if (event.getSource().equals(this.saveFileAs)) {
			this.scriptElements.get(this.tabbedPane.getSelectedIndex() - 1).saveFileAs();
		} else if (event.getSource().equals(this.undo)) {
			this.scriptElements.get(this.tabbedPane.getSelectedIndex() - 1).undo();
		} else if (event.getSource().equals(this.report)) {
			this.report();
		} else if (event.getSource().equals(this.addException)) {
			Object string = null;
			if (this.filteredPanes.getSelectedComponent() != null) {
				string = ((LogPanel) this.filteredPanes.getSelectedComponent()).getThreadName();
			}
			Object text = JOptionPane.showInputDialog(null, "Insert the thread name to add to the exceptions list", "Adding to Exceptions List", JOptionPane.PLAIN_MESSAGE, null, null, string);
			if (text == null) {
				return;
			}
			this.exceptions.add(text.toString());
		} else if (event.getSource().equals(this.addExceptionFromList)) {
			Object text = JOptionPane.showInputDialog(null, "Select the thread name to add to the exceptions list", "Adding to Exceptions List", JOptionPane.PLAIN_MESSAGE, null, this.knownThreads.toArray(), null);
			if (text == null) {
				return;
			}
			this.exceptions.add(text.toString());
		} else if (event.getSource().equals(this.removeException)) {
			if (this.exceptions == null || this.exceptions.size() == 0) {
				JOptionPane.showMessageDialog(null, "No threads to remove from exceptions list.", "Empty Exception List", JOptionPane.WARNING_MESSAGE);
				return;
			}
			Object text = JOptionPane.showInputDialog(null, "Select the thread name to remove from the exceptions list", "Removing Exception", JOptionPane.PLAIN_MESSAGE, null, this.exceptions.toArray(), null);
			if (text == null) {
				return;
			}
			this.exceptions.remove(text.toString());
		} else if (event.getSource().equals(this.addIgnore)) {
			Object string = null;
			if (this.filteredPanes.getSelectedComponent() != null) {
				string = ((LogPanel) this.filteredPanes.getSelectedComponent()).getThreadName();
			}
			Object text = JOptionPane.showInputDialog(null, "Insert the thread name to add to the ignore list", "Adding to Ignore List", JOptionPane.PLAIN_MESSAGE, null, null, string);
			if (text == null) {
				return;
			}
			this.ignores.add(text.toString());
		} else if (event.getSource().equals(this.addIgnoreFromList)) {
			Object text = JOptionPane.showInputDialog(null, "Select the thread name to add to the ignore list", "Adding to Ignore List", JOptionPane.PLAIN_MESSAGE, null, this.knownThreads.toArray(), null);
			if (text == null) {
				return;
			}
			this.ignores.add(text.toString());
		} else if (event.getSource().equals(this.removeIgnore)) {
			if (this.ignores == null || this.ignores.size() == 0) {
				JOptionPane.showMessageDialog(null, "No threads to remove from ignore list.", "Empty Ignore List", JOptionPane.WARNING_MESSAGE);
				return;
			}
			Object text = JOptionPane.showInputDialog(null, "Select the thread name to remove from the ignore list", "Removing Ignore", JOptionPane.PLAIN_MESSAGE, null, this.ignores.toArray(), null);
			if (text == null) {
				return;
			}
			this.ignores.remove(text.toString());
		} else if (event.getSource().equals(this.redo)) {
			this.scriptElements.get(this.tabbedPane.getSelectedIndex() - 1).redo();
		} else if (event.getSource().equals(this.compile) || event.getSource().equals(this.compileAndRun)) {
			this.scriptEnvironment = new ScriptEnvironment();
			this.setStatus("Compiling...");
			this.execute.setEnabled(false);
			CompileThread thread = new CompileThread(this, this.scriptEnvironment, event.getActionCommand().equals("Compile and Run"));
			thread.start();
		} else if (event.getSource().equals(this.execute)) {
			ExecutionThread thread = new ExecutionThread(this.scriptEnvironment);
			thread.start();
		}
	}

	public void addExceptions(List<Exception> exceptions) {
		for (Exception rawEx : exceptions) {
			if (rawEx instanceof ScriptException && !((ScriptException) rawEx).isAnonymous()) {
				ScriptException ex = (ScriptException) rawEx;
				this.getReferenced(ex.getFilename()).addException(ex);
				this.tabbedPane.setTitleAt(this.scriptElements.indexOf(this.getReferenced(ex.getFilename())) + 1, this.getReferenced(ex.getFilename()).getName());
			} else {
				this.scriptElements.get(0).addException(rawEx);
			}
		}
	}

	public LogPanel addOutputListener(LogPanel source, Object filter) {
		if (filter == null || "".equals(filter)) {
			return null;
		}
		if (this.isFilterUsed(filter, source.getThreadName()) != null) {
			JOptionPane.showMessageDialog(this, "An output listener has an identical filter to the one provided.", "Listener Already Exists", JOptionPane.INFORMATION_MESSAGE);
			this.focusOnOutput(this.isFilterUsed(filter, source.getThreadName()));
			return null;
		}
		LogPanel output = new LogPanel(source.getThreadName(), this, source, filter.toString());
		output.getTreePanel().getFilter().addFilter(filter);
		output.getTreePanel().refresh();
		source.addChildOutput(output);
		this.filteredPanes.add(filter.toString(), output);
		this.filteredPanes.setSelectedIndex(this.filteredPanes.getComponentCount() - 1);
		this.filteredOutputMap.get(source.getThreadName()).add(output);
		return output;
	}

	public void addReferenced(ScriptPanel element) {
		this.tabbedPane.add(element.getName(), element);
		this.scriptElements.add(element);
		this.tabbedPane.setSelectedIndex(this.tabbedPane.getComponents().length - 1);
	}

	public void canExecute(boolean value) {
		this.execute.setEnabled(value);
	}

	public void focusOnOutput(LogPanel output) {
		assert output != null;
		this.filteredPanes.setSelectedIndex(this.filteredPanes.indexOfComponent(output));
	}

	public LogPanel getFilteringOutput() {
		return this.filtering;
	}

	public String getPriorityExecutingClass() {
		return this.priorityExecutingClass;
	}

	public ScriptPanel getReferenced(String name) {
		for (ScriptPanel element : this.scriptElements) {
			if (element.getFilename().equals(name)) {
				return element;
			}
		}
		throw new IllegalArgumentException("Script element not found: " + name);
	}

	public List<ScriptPanel> getScriptElements() {
		return this.scriptElements;
	}

	public Debug_TreeNode getUnfilteredCurrentNode() {
		return this.getUnfilteredOutput().getTreePanel().getCurrentNode();
	}

	public LogPanel getUnfilteredOutput() {
		return this.filteredOutputMap.get(Thread.currentThread().getName()).get(0);
	}

	public LogPanel isFilterUsed(Object filter, String threadName) {
		for (LogPanel listener : this.filteredOutputMap.get(threadName)) {
			if (!listener.isUnfiltered() && listener.getTreePanel().getFilter().isFilterUsed(filter)) {
				return listener;
			}
		}
		return null;
	}

	public boolean isIgnoringThisThread() {
		if (!this.reset.isEnabled()) {
			return true;
		}
		String currentThread = Thread.currentThread().getName();
		if (this.isInExceptionsMode()) {
			for (String thread : this.exceptions) {
				if (currentThread.contains(thread)) {
					return false;
				}
			}
			return true;
		} else {
			for (String thread : this.ignores) {
				if (currentThread.contains(thread)) {
					return true;
				}
			}
			return false;
		}
	}

	public boolean isInExceptionsMode() {
		return this.exceptionsMode.isSelected();
	}

	public boolean isResetting() {
		return !this.reset.isEnabled();
	}

	public void removeListenerListener(LogPanel listener) {
		this.filteredPanes.removeTabAt(this.filteredPanes.getSelectedIndex());
		this.filteredOutputMap.get(listener.getThreadName()).remove(listener);
	}

	//Template Preparsing
	public void report() {
		System.out.println("Performance Report");
		NumberFormat nf = NumberFormat.getInstance();
		System.out.println("Maximum Memory Available: " + nf.format(Runtime.getRuntime().maxMemory()) + " bytes (" + Debugger.getAllocationPercentage() + "% allocated)");
		System.out.println("Used Memory Before GC: " + nf.format(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) + " bytes (" + Debugger.getFreePercentage() + "% free)");
		System.gc();
		System.out.println("Used Memory After GC : " + nf.format(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) + " bytes (" + Debugger.getFreePercentage() + "% free)");
		System.out.println("Free Memory: " + nf.format(Runtime.getRuntime().freeMemory()) + " bytes");
		Debug_TreeNode.report();
	}

	public void reset() {
		this.reset.setEnabled(false);
		if (this.filteredPanes.getModel().isSelected()) {
			this.filteredPanes.setSelectedIndex(0);
		}
		int children = this.filteredPanes.getComponentCount();
		for (int i = 0; i < children; i++) {
			((LogPanel) this.filteredPanes.getComponent(i)).removeTab();
		}
		this.filteredPanes.removeAll();
		this.filteredOutputMap.clear();
		Debug_TreeNode.reset();
		System.gc();
		this.reset.setEnabled(true);
	}

	public void resetTitle(ScriptPanel element) {
		this.tabbedPane.setTitleAt(this.scriptElements.indexOf(element) + 1, element.getName());
	}

	public void setCanRedo(boolean canRedo) {
		this.redo.setEnabled(canRedo);
	}

	public void setCanUndo(boolean canUndo) {
		this.undo.setEnabled(canUndo);
	}

	public void setChanged(boolean changed) {
		this.saveFile.setEnabled(changed);
		this.tabbedPane.setTitleAt(this.tabbedPane.getSelectedIndex(), this.scriptElements.get(this.tabbedPane.getSelectedIndex() - 1).getName());
	}

	public void setExceptionsMode(boolean value) {
		this.exceptionsMode.setSelected(value);
		this.ignoreMode.setSelected(!value);
	}

	public void setFilteringOutput(LogPanel output) {
		this.filtering = output;
	}

	public void setPriorityExecutingClass(String template) {
		this.priorityExecutingClass = template;
	}

	public void setStatus(String text) {
		this.status.setText(" " + text);
	}

	public void setTitleAt(int i, String title) {
		this.tabbedPane.setTitleAt(i, title);
	}

	public void showReferenced(ScriptPanel element) {
		this.tabbedPane.setSelectedIndex(this.scriptElements.indexOf(element) + 1);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (this.tabbedPane.getSelectedIndex() == 0) {
			this.closeFile.setEnabled(false);
			this.saveFile.setEnabled(false);
			this.saveFileAs.setEnabled(false);
			this.editMenu.setEnabled(false);
			this.listenerMenu.setEnabled(true);
		} else {
			this.listenerMenu.setEnabled(false);
			this.editMenu.setEnabled(true);
			this.saveFileAs.setEnabled(true);
			this.closeFile.setEnabled(true);
			this.setChanged(this.scriptElements.get(this.tabbedPane.getSelectedIndex() - 1).hasChanged());
			this.setCanUndo(this.scriptElements.get(this.tabbedPane.getSelectedIndex() - 1).canUndo());
			this.setCanRedo(this.scriptElements.get(this.tabbedPane.getSelectedIndex() - 1).canRedo());
		}
	}

	private final TreeBuildingInspector inspector = new TreeBuildingInspector(this);

	public TreeBuildingInspector getInspector() {
		return this.inspector;
	}
}
