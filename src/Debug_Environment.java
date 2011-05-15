import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

class CompileThread extends Thread {
	private Debug_Environment m_debugEnvironment;
	private boolean m_shouldExecute;
	public static final String COMPILETHREADSTRING = "Compilation";
	private static int m_threadNum = 0;

	public CompileThread(Debug_Environment debugEnv, boolean shouldExecute) {
		super(COMPILETHREADSTRING + " " + m_threadNum++);
		this.m_debugEnvironment = debugEnv;
		this.m_shouldExecute = shouldExecute;
	}

	public void run() {
		Debugger.hitStopWatch(Thread.currentThread().getName());
		this.m_debugEnvironment.getEnvironment().reset();
		Parser.clearPreparseLists();
		boolean quickflag = true;
		for (int i = 0; i < this.m_debugEnvironment.getScriptElements().size(); i++) {
			Debug_ScriptElement element = this.m_debugEnvironment.getScriptElements().get(i);
			element.saveFile();
			if (!element.compile()) {
				quickflag = false;
				this.m_debugEnvironment.setTitleAt(i + 1, element.getName());
			}
		}
		if (!quickflag) {
			this.m_debugEnvironment.setStatus("One or more files had errors during compilation.");
			return;
		}
		Vector<Exception> exceptions = Parser.parseElements(this.m_debugEnvironment.getEnvironment());
		if (exceptions.size() == 0) {
			this.m_debugEnvironment.canExecute(true);
			this.m_debugEnvironment.setStatus("All files compiled successfully.");
			Debugger.hitStopWatch(Thread.currentThread().getName());
			assert Debugger.addSnapNode("Compile successful", this.m_debugEnvironment.getEnvironment());
			if (this.m_shouldExecute) {
				ExecutionThread thread = new ExecutionThread(this.m_debugEnvironment);
				thread.start();
			}
			//m_debugEnvironment.report();
			return;
		} else {
			Debugger.hitStopWatch(Thread.currentThread().getName());
			this.m_debugEnvironment.setStatus("One or more files had errors during compilation.");
			this.m_debugEnvironment.addExceptions(exceptions);
		}
	}
}

public class Debug_Environment extends JFrame implements ActionListener, ChangeListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8190546125680224912L;

	public static void initialize() {
		Debug_TreeNode.addPrecached(DebugString.ELEMENTS, "Elements");
		Debug_TreeNode.addPrecached(DebugString.SCRIPTGROUPPARENTHETICAL, "Script Group (parenthetical)");
		Debug_TreeNode.addPrecached(DebugString.SCRIPTGROUPCURLY, "Script Group (curly)");
		Debug_TreeNode.addPrecached(DebugString.NUMERICSCRIPTVALUESHORT, "Numeric Script-Value (short)");
		Debug_TreeNode.addPrecached(DebugString.NUMERICSCRIPTVALUEINT, "Numeric Script-Value (int)");
		Debug_TreeNode.addPrecached(DebugString.NUMERICSCRIPTVALUELONG, "Numeric Script-Value (long)");
		Debug_TreeNode.addPrecached(DebugString.NUMERICSCRIPTVALUEFLOAT, "Numeric Script-Value (float)");
		Debug_TreeNode.addPrecached(DebugString.NUMERICSCRIPTVALUEDOUBLE, "Numeric Script-Value (double)");
		Debug_TreeNode.addPrecached(DebugString.PERMISSIONNULL, "Permission: null");
		Debug_TreeNode.addPrecached(DebugString.PERMISSIONPRIVATE, "Permission: private");
		Debug_TreeNode.addPrecached(DebugString.PERMISSIONPROTECTED, "Permission: protected");
		Debug_TreeNode.addPrecached(DebugString.PERMISSIONPUBLIC, "Permission: public");
		Debug_TreeNode.addPrecached(DebugString.SCRIPTLINE, "Script Line: ");
		Debug_TreeNode.addPrecached(DebugString.SCRIPTOPERATOR, "Script Operator: ");
		Debug_TreeNode.addPrecached(DebugString.ORIGINALSTRING, "Original String: '");
		Debug_TreeNode.addPrecached(DebugString.REFERENCEDELEMENTNULL, "Referenced Element: null");
		Debug_TreeNode.addPrecached(DebugString.OUTPUTTREE, "Output Tree");
	}

	public static void main(String[] args) {
		new Debug_Environment(800, 800);
	}

	private JSplitPane m_vertSplitPane;
	private JTabbedPane m_tabbedPane, m_filteredPanes;
	private JLabel m_status;
	private JMenuItem m_newFile, m_openFile, m_closeFile, m_saveFile, m_saveFileAs, m_exit,
			m_reset, m_report;
	private JMenuItem m_compile, m_execute, m_compileAndRun;
	private JMenuItem m_undo, m_redo;
	private JMenuItem m_removeTab, m_clearTab, m_createListener, m_renameTab;
	private JMenuItem m_addException, m_addExceptionFromList, m_addIgnore, m_addIgnoreFromList,
			m_removeException, m_removeIgnore;
	private JMenuItem m_addAssertionFailure, m_removeAssertionFailure;
	private JRadioButtonMenuItem m_exceptionsMode, m_ignoreMode;
	private JMenuBar m_menuBar;
	private JMenu m_parserMenu, m_editMenu, m_listenerMenu;
	private java.util.List<Debug_ScriptElement> m_scriptElements;
	private Debug_Listener m_filtering;
	private Map<String, java.util.List<Debug_Listener>> m_filteredOutputMap = new HashMap<String, java.util.List<Debug_Listener>>();
	private String m_priorityExecutingClass;
	private java.util.List<String> m_exceptions, m_ignores, m_allThreads;

	private Map<String, Integer> m_exceptionMap = new HashMap<String, Integer>();

	private ScriptEnvironment m_environment;

	public Debug_Environment(int width, int height) {
		super("RFE Debugger");
		Debugger.setDebugger(this);
		initialize();
		this.m_scriptElements = new LinkedList<Debug_ScriptElement>();
		this.m_exceptions = new LinkedList<String>();
		this.m_ignores = new LinkedList<String>();
		this.m_allThreads = new LinkedList<String>();
		this.m_allThreads.add("AWT-EventQueue-0");
		this.m_allThreads.add(CompileThread.COMPILETHREADSTRING);
		this.m_allThreads.add(ExecutionThread.EXECUTIONTHREADSTRING);
		this.m_allThreads.add(PolygonPipeline.POLYGONPIPELINESTRING);
		this.m_allThreads.add(SplitterThread.SPLITTERTHREADSTRING);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(width, height);
		// Menu bar
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(this.m_status = new JLabel(" Ready"), BorderLayout.SOUTH);
		this.getContentPane().add(this.m_tabbedPane = new JTabbedPane());
		this.m_tabbedPane.addChangeListener(this);
		this.m_menuBar = new JMenuBar();
		this.setJMenuBar(this.m_menuBar);
		JMenu fileMenu = new JMenu("File");
		this.m_menuBar.add(fileMenu);
		fileMenu.setMnemonic('F');
		this.m_editMenu = new JMenu("Edit");
		this.m_menuBar.add(this.m_editMenu);
		this.m_editMenu.setMnemonic('E');
		this.m_parserMenu = new JMenu("Parser");
		this.m_parserMenu.setMnemonic('P');
		this.m_menuBar.add(this.m_parserMenu);
		fileMenu.add(this.m_newFile = new JMenuItem("New Script", 'N'));
		fileMenu.add(this.m_openFile = new JMenuItem("Open Script...", 'O'));
		fileMenu.add(this.m_closeFile = new JMenuItem("Close Script", 'C'));
		fileMenu.add(this.m_saveFile = new JMenuItem("Save Script", 'S'));
		fileMenu.add(this.m_saveFileAs = new JMenuItem("Save Script As...", 'A'));
		fileMenu.add(this.m_report = new JMenuItem("Report", 'T'));
		fileMenu.add(this.m_reset = new JMenuItem("Reset"));
		fileMenu.add(this.m_exit = new JMenuItem("Exit", 'X'));
		this.m_editMenu.add(this.m_undo = new JMenuItem("Undo", 'U'));
		this.m_editMenu.add(this.m_redo = new JMenuItem("Redo", 'R'));
		this.m_parserMenu.add(this.m_compile = new JMenuItem("Compile", 'C'));
		this.m_parserMenu.add(this.m_execute = new JMenuItem("Execute", 'X'));
		this.m_parserMenu.add(this.m_compileAndRun = new JMenuItem("Compile and Run", 'R'));
		this.m_execute.setEnabled(false);
		JMenu debugMenu = new JMenu("Debugger");
		this.m_menuBar.add(debugMenu);
		debugMenu.add(this.m_exceptionsMode = new JRadioButtonMenuItem("Lazy Filter Mode"));
		debugMenu.add(this.m_ignoreMode = new JRadioButtonMenuItem("Greedy Filter Mode"));
		debugMenu.addSeparator();
		debugMenu.add(this.m_addException = new JMenuItem("Add Exception"));
		debugMenu.add(this.m_addExceptionFromList = new JMenuItem("Add Exception From List..."));
		debugMenu.add(this.m_removeException = new JMenuItem("Remove Exception..."));
		debugMenu.addSeparator();
		debugMenu.add(this.m_addIgnore = new JMenuItem("Add to Ignore List"));
		debugMenu.add(this.m_addIgnoreFromList = new JMenuItem("Add Ignore From List..."));
		debugMenu.add(this.m_removeIgnore = new JMenuItem("Remove Ignore..."));
		debugMenu.addSeparator();
		debugMenu.add(this.m_addAssertionFailure = new JMenuItem("Add Assertion Failure"));
		debugMenu.add(this.m_removeAssertionFailure = new JMenuItem("Remove Assertion Failure"));
		this.m_listenerMenu = new JMenu("Listener");
		this.m_menuBar.add(this.m_listenerMenu);
		this.m_listenerMenu.setMnemonic('L');
		this.m_listenerMenu.add(this.m_createListener = new JMenuItem("Create Listener...", 'C'));
		this.m_listenerMenu.add(this.m_renameTab = new JMenuItem("Rename Tab...", 'N'));
		this.m_listenerMenu.add(this.m_clearTab = new JMenuItem("Clear Tab", 'C'));
		this.m_listenerMenu.add(this.m_removeTab = new JMenuItem("Remove Tab", 'R'));
		ButtonGroup group = new ButtonGroup();
		group.add(this.m_exceptionsMode);
		group.add(this.m_ignoreMode);
		this.m_exceptionsMode.setSelected(true);
		// Set up our debug spew and script tabs
		this.m_tabbedPane.add("Debug Output", this.m_filteredPanes = new JTabbedPane());
		// Accelerators
		this.m_clearTab.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		this.m_removeTab.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
		this.m_newFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		this.m_openFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		this.m_closeFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
		this.m_saveFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		this.m_saveFileAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		this.m_exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		this.m_undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
		this.m_redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
		this.m_compile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
		this.m_execute.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0));
		this.m_report.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0));
		this.m_compileAndRun.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, ActionEvent.CTRL_MASK));
		// Listeners
		this.m_reset.addActionListener(this);
		this.m_renameTab.addActionListener(this);
		this.m_report.addActionListener(this);
		this.m_filteredPanes.addChangeListener(this);
		this.m_clearTab.addActionListener(this);
		this.m_removeTab.addActionListener(this);
		this.m_createListener.addActionListener(this);
		this.m_newFile.addActionListener(this);
		this.m_openFile.addActionListener(this);
		this.m_closeFile.addActionListener(this);
		this.m_saveFile.addActionListener(this);
		this.m_saveFileAs.addActionListener(this);
		this.m_exit.addActionListener(this);
		this.m_undo.addActionListener(this);
		this.m_redo.addActionListener(this);
		this.m_compile.addActionListener(this);
		this.m_execute.addActionListener(this);
		this.m_compileAndRun.addActionListener(this);
		this.m_addException.addActionListener(this);
		this.m_addExceptionFromList.addActionListener(this);
		this.m_addIgnoreFromList.addActionListener(this);
		this.m_addIgnore.addActionListener(this);
		this.m_removeException.addActionListener(this);
		this.m_removeIgnore.addActionListener(this);
		this.m_addAssertionFailure.addActionListener(this);
		this.m_removeAssertionFailure.addActionListener(this);
		// Open all valid scripts in our working directory
		File folder = new File(".");
		ExtensionFilter filter = new ExtensionFilter();
		filter.addExtension("RiffScript");
		File[] files = folder.listFiles(filter);
		for (File file : files) {
			if (file.isFile()) {
				this.addReferenced(new Debug_ScriptElement(this, file));
			}
		}
		// Finally, show the window.
		this.m_environment = new ScriptEnvironment();
		this.setVisible(true);
	}

	// Listeners
	public void actionPerformed(ActionEvent event) {
		if (event.getActionCommand().equals("Exit")) {
			java.util.List<Debug_ScriptElement> removedElements = new LinkedList<Debug_ScriptElement>();
			int index = this.m_tabbedPane.getSelectedIndex();
			for (; this.m_scriptElements.size() > 0;) {
				Debug_ScriptElement element = this.m_scriptElements.get(0);
				if (!element.closeFile()) {
					for (Debug_ScriptElement added : removedElements) {
						this.m_scriptElements.add(0, added);
						this.m_tabbedPane.add(added, 1);
					}
					this.m_tabbedPane.setSelectedIndex(index);
					return;
				}
				this.m_tabbedPane.remove(1);
				this.m_scriptElements.remove(0);
				removedElements.add(element);
			}
			System.exit(0);
		} else if (event.getSource().equals(this.m_newFile)) {
			this.addReferenced(new Debug_ScriptElement(this, (String) null));
		} else if (event.getSource().equals(this.m_openFile)) {
			Debug_ScriptElement element = new Debug_ScriptElement(this);
			if (element.isValid()) {
				this.addReferenced(element);
			}
		} else if (event.getSource().equals(this.m_closeFile)) {
			if (((Debug_ScriptElement) this.m_scriptElements.get(this.m_tabbedPane.getSelectedIndex() - 1)).closeFile()) {
				int index = this.m_tabbedPane.getSelectedIndex();
				this.m_tabbedPane.remove(index);
				this.m_scriptElements.remove(index - 1);
			}
		} else if (event.getSource().equals(this.m_renameTab)) {
			Object text = JOptionPane.showInputDialog(null, "Insert new output name", "Rename Output", JOptionPane.QUESTION_MESSAGE, null, null, this.m_filteredPanes.getTitleAt(this.m_filteredPanes.getSelectedIndex()));
			if (text != null) {
				this.m_filteredPanes.setTitleAt(this.m_filteredPanes.getSelectedIndex(), text.toString());
			}
		} else if (event.getSource().equals(this.m_createListener)) {
			((Debug_Listener) this.m_filteredPanes.getSelectedComponent()).promptCreateListener();
		} else if (event.getSource().equals(this.m_saveFile)) {
			this.m_scriptElements.get(this.m_tabbedPane.getSelectedIndex() - 1).saveFile();
		} else if (event.getSource().equals(this.m_reset)) {
			this.reset();
		} else if (event.getSource().equals(this.m_clearTab)) {
			((Debug_Listener) this.m_filteredPanes.getSelectedComponent()).clearTab();
		} else if (event.getSource().equals(this.m_removeTab)) {
			((Debug_Listener) this.m_filteredPanes.getSelectedComponent()).removeTab();
			this.m_filteredOutputMap.get(((Debug_Listener) this.m_filteredPanes.getSelectedComponent()).getThreadName()).remove(this.m_filteredPanes.getSelectedComponent());
			this.m_filteredPanes.remove(this.m_filteredPanes.getSelectedComponent());
		} else if (event.getSource().equals(this.m_saveFileAs)) {
			this.m_scriptElements.get(this.m_tabbedPane.getSelectedIndex() - 1).saveFileAs();
		} else if (event.getSource().equals(this.m_undo)) {
			this.m_scriptElements.get(this.m_tabbedPane.getSelectedIndex() - 1).undo();
		} else if (event.getSource().equals(this.m_report)) {
			this.report();
		} else if (event.getSource().equals(this.m_addException)) {
			Object string = null;
			if (this.m_filteredPanes.getSelectedComponent() != null) {
				string = ((Debug_Listener) this.m_filteredPanes.getSelectedComponent()).getThreadName();
			}
			Object text = JOptionPane.showInputDialog(null, "Insert the thread name to add to the exceptions list", "Adding to Exceptions List", JOptionPane.PLAIN_MESSAGE, null, null, string);
			if (text == null) {
				return;
			}
			this.m_exceptions.add(text.toString());
		} else if (event.getSource().equals(this.m_addExceptionFromList)) {
			if (this.m_allThreads == null || this.m_allThreads.size() == 0) {
				JOptionPane.showMessageDialog(null, "There are no threads in the selection list.", "Empty Thread List", JOptionPane.WARNING_MESSAGE);
				return;
			}
			Object text = JOptionPane.showInputDialog(null, "Select the thread name to add to the exceptions list", "Adding to Exceptions List", JOptionPane.PLAIN_MESSAGE, null, this.m_allThreads.toArray(), null);
			if (text == null) {
				return;
			}
			this.m_exceptions.add(text.toString());
		} else if (event.getSource().equals(this.m_removeException)) {
			if (this.m_exceptions == null || this.m_exceptions.size() == 0) {
				JOptionPane.showMessageDialog(null, "No threads to remove from exceptions list.", "Empty Exception List", JOptionPane.WARNING_MESSAGE);
				return;
			}
			Object text = JOptionPane.showInputDialog(null, "Select the thread name to remove from the exceptions list", "Removing Exception", JOptionPane.PLAIN_MESSAGE, null, this.m_exceptions.toArray(), null);
			if (text == null) {
				return;
			}
			this.m_exceptions.remove(text.toString());
		} else if (event.getSource().equals(this.m_addIgnore)) {
			Object string = null;
			if (this.m_filteredPanes.getSelectedComponent() != null) {
				string = ((Debug_Listener) this.m_filteredPanes.getSelectedComponent()).getThreadName();
			}
			Object text = JOptionPane.showInputDialog(null, "Insert the thread name to add to the ignore list", "Adding to Ignore List", JOptionPane.PLAIN_MESSAGE, null, null, string);
			if (text == null) {
				return;
			}
			this.m_ignores.add(text.toString());
		} else if (event.getSource().equals(this.m_addIgnoreFromList)) {
			if (this.m_allThreads == null || this.m_allThreads.size() == 0) {
				JOptionPane.showMessageDialog(null, "There are no threads in the selection list.", "Empty Thread List", JOptionPane.WARNING_MESSAGE);
				return;
			}
			Object text = JOptionPane.showInputDialog(null, "Select the thread name to add to the ignore list", "Adding to Ignore List", JOptionPane.PLAIN_MESSAGE, null, this.m_allThreads.toArray(), null);
			if (text == null) {
				return;
			}
			this.m_ignores.add(text.toString());
		} else if (event.getSource().equals(this.m_removeIgnore)) {
			if (this.m_ignores == null || this.m_ignores.size() == 0) {
				JOptionPane.showMessageDialog(null, "No threads to remove from ignore list.", "Empty Ignore List", JOptionPane.WARNING_MESSAGE);
				return;
			}
			Object text = JOptionPane.showInputDialog(null, "Select the thread name to remove from the ignore list", "Removing Ignore", JOptionPane.PLAIN_MESSAGE, null, this.m_ignores.toArray(), null);
			if (text == null) {
				return;
			}
			this.m_ignores.remove(text.toString());
		} else if (event.getSource().equals(this.m_addAssertionFailure)) {

		} else if (event.getSource().equals(this.m_redo)) {
			this.m_scriptElements.get(this.m_tabbedPane.getSelectedIndex() - 1).redo();
		} else if (event.getSource().equals(this.m_compile) || event.getSource().equals(this.m_compileAndRun)) {
			this.setStatus("Compiling...");
			this.m_execute.setEnabled(false);
			CompileThread thread = new CompileThread(this, event.getActionCommand().equals("Compile and Run"));
			thread.start();
		} else if (event.getSource().equals(this.m_execute)) {
			ExecutionThread thread = new ExecutionThread(this);
			thread.start();
		}
	}

	public void addExceptions(java.util.List<Exception> exceptions) {
		for (Exception rawEx : exceptions) {
			if (rawEx instanceof Exception_Nodeable && !((Exception_Nodeable) rawEx).isAnonymous()) {
				Exception_Nodeable ex = (Exception_Nodeable) rawEx;
				this.getReferenced(ex.getFilename()).addException(ex);
				this.m_tabbedPane.setTitleAt(this.m_scriptElements.indexOf(this.getReferenced(ex.getFilename())) + 1, this.getReferenced(ex.getFilename()).getName());
			} else {
				this.m_scriptElements.get(0).addException(rawEx);
			}
		}
	}

	public void addNode(Debug_TreeNode node) {
		this.addNode(node, false);
	}

	public void addNode(Debug_TreeNode node, boolean setAsCurrent) {
		if (this.isIgnoringThisThread()) {
			return;
		}
		if (this.m_filteredOutputMap.get(Thread.currentThread().getName()) == null || this.m_filteredOutputMap.get(Thread.currentThread().getName()).size() == 0) {
			this.m_filteredOutputMap.put(Thread.currentThread().getName(), new Vector<Debug_Listener>());
			this.m_filteredOutputMap.get(Thread.currentThread().getName()).add(new Debug_Listener(Thread.currentThread().getName(), this, null, Thread.currentThread().getName()));
			this.m_filteredPanes.add(Thread.currentThread().getName(), this.m_filteredOutputMap.get(Thread.currentThread().getName()).get(0));
		}
		for (Debug_Listener listener : this.m_filteredOutputMap.get(Thread.currentThread().getName())) {
			if (listener.isCapturing()) {
				if (!listener.getTreePanel().getFilter().isListening()) {
					listener.getTreePanel().getFilter().sniffNode(node);
					if (listener.getTreePanel().getFilter().isListening()) {
						this.addNodeToOutput(node, setAsCurrent, listener);
					}
				} else {
					this.addNodeToOutput(node, setAsCurrent, listener);
				}
				return;
			}
		}
		for (Debug_Listener listener : this.m_filteredOutputMap.get(Thread.currentThread().getName())) {
			this.addNodeToOutput(node.duplicate(), setAsCurrent, listener);
		}
	}

	private void addNodeToOutput(Debug_TreeNode node, boolean setAsCurrent, Debug_Listener listener) {
		if (!listener.getTreePanel().getFilter().isListening()) {
			listener.getTreePanel().getFilter().sniffNode(node);
			if (!listener.getTreePanel().getFilter().isListening()) {
				return;
			}
			node = new Debug_TreeNode_Orphaned(listener.getSource().getTreePanel().getLastNodeAdded());
		}
		if (listener.getTreePanel().getCurrentNode().hasChildren() && listener.getTreePanel().getCurrentNode().getLastChild().getGroup() != null && node.getGroup() != null && listener.getTreePanel().getCurrentNode().getLastChild().getGroup().equals(node.getGroup())) {
			if (listener.getTreePanel().getCurrentNode().getLastChild().getData().equals(node.getGroup())) {
				listener.getTreePanel().getCurrentNode().getLastChild().addChild(node);
				node.setPracticalParent(listener.getTreePanel().getCurrentNode());
				if (setAsCurrent) {
					listener.getTreePanel().setAsCurrent(node);
				}
			} else {
				Debug_TreeNode lastNode = listener.getTreePanel().getCurrentNode().removeLastChild();
				Debug_TreeNode groupNode = new Debug_TreeNode(node.getGroupCode(), node.getGroupCode());
				groupNode.addChild(lastNode);
				groupNode.addChild(node);
				lastNode.setPracticalParent(listener.getTreePanel().getCurrentNode());
				node.setPracticalParent(listener.getTreePanel().getCurrentNode());
				listener.getTreePanel().addNode(groupNode);
				if (setAsCurrent) {
					listener.getTreePanel().setAsCurrent(node);
				}
			}
			return;
		}
		listener.getTreePanel().addNode(node);
		if (setAsCurrent) {
			listener.getTreePanel().setAsCurrent(node);
		}
	}

	public Debug_Listener addOutputListener(Debug_Listener source, Object filter) {
		if (filter == null || "".equals(filter)) {
			return null;
		}
		if (this.isFilterUsed(filter, source.getThreadName()) != null) {
			JOptionPane.showMessageDialog(this, "An output listener has an identical filter to the one provided.", "Listener Already Exists", JOptionPane.INFORMATION_MESSAGE);
			this.focusOnOutput(this.isFilterUsed(filter, source.getThreadName()));
			return null;
		}
		Debug_Listener output = new Debug_Listener(source.getThreadName(), this, source, filter.toString());
		output.getTreePanel().getFilter().addFilter(filter);
		output.getTreePanel().refresh();
		source.addChildOutput(output);
		this.m_filteredPanes.add(filter.toString(), output);
		this.m_filteredPanes.setSelectedIndex(this.m_filteredPanes.getComponentCount() - 1);
		this.m_filteredOutputMap.get(source.getThreadName()).add(output);
		return output;
	}

	public void addReferenced(Debug_ScriptElement element) {
		this.m_tabbedPane.add(element.getName(), element);
		this.m_scriptElements.add(element);
		this.m_tabbedPane.setSelectedIndex(this.m_tabbedPane.getComponents().length - 1);
	}

	public void canExecute(boolean value) {
		this.m_execute.setEnabled(value);
	}

	public void closeNode() {
		if (this.isIgnoringThisThread()) {
			return;
		}
		for (Debug_Listener listener : this.m_filteredOutputMap.get(Thread.currentThread().getName())) {
			if (listener.isCapturing() && listener.getTreePanel().getFilter().isListening()) {
				listener.getTreePanel().closeNode();
				return;
			}
		}
		for (Debug_Listener listener : this.m_filteredOutputMap.get(Thread.currentThread().getName())) {
			if (listener.getTreePanel().getFilter().isListening()) {
				listener.getTreePanel().closeNode();
			}
		}
	}

	public void closeNodeTo(Object string) {
		if (this.isIgnoringThisThread()) {
			return;
		}
		for (Debug_Listener listener : this.m_filteredOutputMap.get(Thread.currentThread().getName())) {
			while (listener.getTreePanel().getFilter().isListening() && !listener.getTreePanel().getCurrentNode().getData().equals(string)) {
				listener.getTreePanel().closeNode();
			}
		}
	}

	public boolean ensureCurrentNode(Object obj) {
		if (!this.m_reset.isEnabled()) {
			return true;
		}
		if (this.isInExceptionsMode() && !this.m_exceptions.contains(Thread.currentThread().getName())) {
			return true;
		}
		if (this.m_ignores.contains(Thread.currentThread().getName())) {
			return true;
		}
		return this.getUnfilteredCurrentNode().getData().equals(obj);
	}

	public void focusOnOutput(Debug_Listener output) {
		assert output != null;
		this.m_filteredPanes.setSelectedIndex(this.m_filteredPanes.indexOfComponent(output));
	}

	public ScriptEnvironment getEnvironment() {
		return this.m_environment;
	}

	public Debug_Listener getFilteringOutput() {
		return this.m_filtering;
	}

	public Debug_TreeNode getLastNodeAdded() {
		return this.getUnfilteredOutput().getTreePanel().getLastNodeAdded();
	}

	public String getPriorityExecutingClass() {
		return this.m_priorityExecutingClass;
	}

	public Debug_ScriptElement getReferenced(String name) {
		for (Debug_ScriptElement element : this.m_scriptElements) {
			if (element.getFilename().equals(name)) {
				return element;
			}
		}
		throw new Exception_InternalError("Script element not found: " + name);
	}

	public java.util.List<Debug_ScriptElement> getScriptElements() {
		return this.m_scriptElements;
	}

	public Debug_TreeNode getUnfilteredCurrentNode() {
		return this.getUnfilteredOutput().getTreePanel().getCurrentNode();
	}

	public Debug_Listener getUnfilteredOutput() {
		return this.m_filteredOutputMap.get(Thread.currentThread().getName()).get(0);
	}

	public Debug_Listener isFilterUsed(Object filter, String threadName) {
		for (Debug_Listener listener : this.m_filteredOutputMap.get(threadName)) {
			if (!listener.isUnfiltered() && listener.getTreePanel().getFilter().isFilterUsed(filter)) {
				return listener;
			}
		}
		return null;
	}

	public boolean isIgnoringThisThread() {
		if (!this.m_reset.isEnabled()) {
			return true;
		}
		String currentThread = Thread.currentThread().getName();
		if (this.isInExceptionsMode()) {
			for (String thread : this.m_exceptions) {
				if (currentThread.contains(thread)) {
					return false;
				}
			}
			return true;
		} else {
			for (String thread : this.m_ignores) {
				if (currentThread.contains(thread)) {
					return true;
				}
			}
			return false;
		}
	}

	public boolean isInExceptionsMode() {
		return this.m_exceptionsMode.isSelected();
	}

	public boolean isResetting() {
		return !this.m_reset.isEnabled();
	}

	// Node stuff
	public void openNode(Debug_TreeNode node) {
		this.addNode(node, true);
	}

	// Deprecated, deprecated, deprecated...
	public boolean printDebug(String category, Object obj) {
		return true;
	}

	public void removeListenerListener(Debug_Listener listener) {
		this.m_filteredPanes.removeTabAt(this.m_filteredPanes.getSelectedIndex());
		this.m_filteredOutputMap.get(listener.getThreadName()).remove(listener);
	}

	//Template Preparsing
	public void report() {
		System.out.println("Performance Report");
		NumberFormat nf = NumberFormat.getInstance();
		System.out.println("Maximum Memory Available: " + nf.format(Runtime.getRuntime().maxMemory()) + " bytes (" + Debugger.getAllocationPercentage() + "% allocated)");
		System.out.println("Used Memory Before GC: " + nf.format(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) + " bytes (" + Debugger.getFreePercentage() + "% free)");
		System.gc();
		System.out.println("Used Memory After GC : " + nf.format(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) + " bytes (" + Debugger.getFreePercentage() + "% free)");
		System.out.println("Used Memory: " + nf.format(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) + " bytes");
		System.out.println("Free Memory: " + nf.format(Runtime.getRuntime().freeMemory()) + " bytes");
		Debug_TreeNode.report();
	}

	public void reset() {
		this.m_reset.setEnabled(false);
		this.m_filteredPanes.setSelectedIndex(0);
		int children = this.m_filteredPanes.getComponentCount();
		for (int i = 0; i < children; i++) {
			((Debug_Listener) this.m_filteredPanes.getComponent(i)).removeTab();
		}
		this.m_filteredPanes.removeAll();
		this.m_filteredOutputMap.clear();
		Debug_TreeNode.reset();
		System.gc();
		this.m_reset.setEnabled(true);
	}

	public void resetTitle(Debug_ScriptElement element) {
		this.m_tabbedPane.setTitleAt(this.m_scriptElements.indexOf(element) + 1, element.getName());
	}

	public void setCanRedo(boolean canRedo) {
		this.m_redo.setEnabled(canRedo);
	}

	public void setCanUndo(boolean canUndo) {
		this.m_undo.setEnabled(canUndo);
	}

	public void setChanged(boolean changed) {
		this.m_saveFile.setEnabled(changed);
		this.m_tabbedPane.setTitleAt(this.m_tabbedPane.getSelectedIndex(), this.m_scriptElements.get(this.m_tabbedPane.getSelectedIndex() - 1).getName());
	}

	public void setExceptionsMode(boolean value) {
		this.m_exceptionsMode.setSelected(value);
		this.m_ignoreMode.setSelected(!value);
	}

	public void setFilteringOutput(Debug_Listener output) {
		this.m_filtering = output;
	}

	public void setPriorityExecutingClass(String template) {
		this.m_priorityExecutingClass = template;
	}

	public void setStatus(String text) {
		this.m_status.setText(" " + text);
	}

	public void setTitleAt(int i, String title) {
		this.m_tabbedPane.setTitleAt(i, title);
	}

	public void showReferenced(Debug_ScriptElement element) {
		this.m_tabbedPane.setSelectedIndex(this.m_scriptElements.indexOf(element) + 1);
	}

	public void stateChanged(ChangeEvent e) {
		if (this.m_tabbedPane.getSelectedIndex() == 0) {
			this.m_closeFile.setEnabled(false);
			this.m_saveFile.setEnabled(false);
			this.m_saveFileAs.setEnabled(false);
			this.m_editMenu.setEnabled(false);
			this.m_listenerMenu.setEnabled(true);
		} else {
			this.m_listenerMenu.setEnabled(false);
			this.m_editMenu.setEnabled(true);
			this.m_saveFileAs.setEnabled(true);
			this.m_closeFile.setEnabled(true);
			this.setChanged(this.m_scriptElements.get(this.m_tabbedPane.getSelectedIndex() - 1).hasChanged());
			this.setCanUndo(this.m_scriptElements.get(this.m_tabbedPane.getSelectedIndex() - 1).canUndo());
			this.setCanRedo(this.m_scriptElements.get(this.m_tabbedPane.getSelectedIndex() - 1).canRedo());
		}
	}
}

class Debugger {
	// Debug_Environment fxns
	private static Map<String, Long> m_stopWatches = new HashMap<String, Long>();
	private static Debug_Environment m_debugger;

	public synchronized static boolean addCollectionNode(Object group, Collection list) {
		if (list.size() == 0) {
			return true;
		}
		Iterator iter = list.iterator();
		while (iter.hasNext()) {
			addNode(iter.next());
		}
		return true;
	}

	public synchronized static boolean addMapNode(Object group, Map map) {
		if (map.size() == 0) {
			return true;
		}
		Iterator iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			addSnapNode(entry.getKey().toString(), entry.getValue());
		}
		return true;
	}

	public static boolean addNode(Object o) {
		return addNode(null, o);
	}

	public static boolean addNode(Object group, Object o) {
		if (getDebugger().isIgnoringThisThread()) {
			return true;
		}
		if (o == null) {
			getDebugger().addNode(new Debug_TreeNode(group, "null"));
			return true;
		}
		if (o instanceof Nodeable) {
			addNodeableNode(group, (Nodeable) o);
			if (o instanceof Exception) {
				String exceptionName;
				if (o instanceof Exception_Nodeable) {
					exceptionName = ((Exception_Nodeable) o).getName();
				} else if (o instanceof Exception_InternalError) {
					exceptionName = ((Exception_InternalError) o).getMessage();
				} else {
					exceptionName = "Exception";
				}
				getDebugger().getUnfilteredOutput().getHotspotPanel().createHotspot(getDebugger().getLastNodeAdded(), exceptionName);
			}
			return true;
		}
		if (o instanceof Collection) {
			return addCollectionNode(group, (Collection) o);
		}
		if (o instanceof Map) {
			return addMapNode(group, (Map) o);
		}
		getDebugger().addNode(new Debug_TreeNode(group, o));
		if (o instanceof Exception) {
			String exceptionName;
			if (o instanceof Exception_Nodeable) {
				exceptionName = ((Exception_Nodeable) o).getName();
			} else if (o instanceof Exception_InternalError) {
				exceptionName = ((Exception_InternalError) o).getMessage();
			} else {
				exceptionName = "Exception";
			}
			getDebugger().getUnfilteredOutput().getHotspotPanel().createHotspot(getDebugger().getLastNodeAdded(), exceptionName);
		}
		return true;
	}

	public static boolean addNodeableNode(Object group, Nodeable nodeable) {
		nodeable.nodificate();
		return true;
	}

	public static boolean addSnapNode(Object name, Object o) {
		return addSnapNode(null, name, o);
	}

	public static boolean addSnapNode(Object group, Object name, Object o) {
		openNode(group, name);
		addNode(o);
		closeNode();
		return true;
	}

	public static boolean atFullAllocation() {
		return getAllocationPercentage() == 100;
	}

	public static boolean closeNode() {
		if (getDebugger().isIgnoringThisThread()) {
			return true;
		}
		getDebugger().closeNode();
		return true;
	}

	public static boolean closeNode(Object string) {
		addNode(string);
		closeNode();
		return true;
	}

	public static boolean closeNode(Object string, Object object) {
		addSnapNode(string, object);
		closeNode();
		return true;
	}

	public static boolean closeNodeTo(Object string) {
		getDebugger().closeNodeTo(string);
		return true;
	}

	public static boolean ensureCurrentNode(Object string) {
		return getDebugger().ensureCurrentNode(string);
	}

	public static int getAllocationPercentage() {
		return (int) ((((double) Runtime.getRuntime().totalMemory()) / ((double) Runtime.getRuntime().maxMemory())) * 100);
	}

	public static Debug_Environment getDebugger() {
		return m_debugger;
	}

	public static int getFreePercentage() {
		return (int) ((((double) Runtime.getRuntime().freeMemory()) / ((double) Runtime.getRuntime().totalMemory())) * 100);
	}

	public static Debug_TreeNode getLastNodeAdded() {
		return getDebugger().getLastNodeAdded();
	}

	public static String getPriorityExecutingClass() {
		return getDebugger().getPriorityExecutingClass();
	}

	public static String getString(DebugString value) {
		return Debug_TreeNode.getPrecached(value).toString();
	}

	public static void hitStopWatch(String name) {
		if (m_stopWatches.get(name) == null) {
			m_stopWatches.put(name, new Long(System.currentTimeMillis()));
			return;
		}
		System.out.println("StopWatcher: " + name + " executed in " + (((double) (System.currentTimeMillis() - m_stopWatches.get(name).longValue())) / 1000) + " seconds");
		m_stopWatches.remove(name);
	}

	public static boolean isResetting() {
		return getDebugger().isResetting();
	}

	// Noding functions.
	public static boolean openNode(Object string) {
		openNode(null, string);
		return true;
	}

	public static boolean openNode(Object group, Object string) {
		if (getDebugger().isIgnoringThisThread()) {
			return true;
		}
		getDebugger().openNode(new Debug_TreeNode(group, string));
		return true;
	}

	public static boolean printDebug(String category) {
		String slash = "";
		int offset = 0;
		if (category.charAt(0) == '/') {
			slash = "/";
			offset++;
		}
		String[] categoryArray = category.split("/");
		return printDebug(category, "(" + slash + categoryArray[1 + offset] + ":" + categoryArray[0 + offset] + ")");
	}

	public static boolean printDebug(String category, Collection list) {
		return printDebug(category, RiffToolbox.displayList(list));
	}

	public static boolean printDebug(String category, Object obj) {
		return getDebugger().printDebug(category, obj);
	}

	public static void printException(Exception ex) {
		System.out.println(ex);
		if (ex instanceof Exception_Nodeable || ex instanceof Exception_InternalError) {
			assert addNode("Exceptions and Errors", ex);
		} else {
			assert addSnapNode("Exceptions and Errors", "Exception", ex);
		}
	}

	public static void report() {
		getDebugger().report();
	}

	public static void reset() {
		getDebugger().reset();
	}

	public static void setDebugger(Debug_Environment debugger) {
		if (m_debugger == null) {
			m_debugger = debugger;
		}
	}

	public static void setExceptionsMode(boolean value) {
		getDebugger().setExceptionsMode(value);
	}

	public static void setPriorityExecutingClass(String name) {
		getDebugger().setPriorityExecutingClass(name);
	}
}

enum DebugString {
	ELEMENTS,
	SCRIPTGROUPPARENTHETICAL,
	SCRIPTGROUPCURLY,
	NUMERICSCRIPTVALUESHORT,
	NUMERICSCRIPTVALUEINT,
	NUMERICSCRIPTVALUELONG,
	NUMERICSCRIPTVALUEFLOAT,
	NUMERICSCRIPTVALUEDOUBLE,
	PERMISSIONNULL,
	PERMISSIONPRIVATE,
	PERMISSIONPROTECTED,
	PERMISSIONPUBLIC,
	SCRIPTLINE,
	SCRIPTOPERATOR,
	ORIGINALSTRING,
	REFERENCEDELEMENTNULL,
	OUTPUTTREE
}

class ExecutionThread extends Thread {
	private Debug_Environment m_debugEnvironment;
	public static final String EXECUTIONTHREADSTRING = "Script Execution";
	private static int m_threadNum = 0;

	public ExecutionThread(Debug_Environment debugEnv) {
		super(EXECUTIONTHREADSTRING + " " + m_threadNum++);
		this.m_debugEnvironment = debugEnv;
	};

	public void run() {
		Debugger.hitStopWatch(Thread.currentThread().getName());
		this.m_debugEnvironment.getEnvironment().execute();
		Debugger.hitStopWatch(Thread.currentThread().getName());
	}
}
