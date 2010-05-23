package com.dafrito.ide.legacy;

import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.geom.PolygonPipeline;
import com.dafrito.geom.SplitterThread;
import com.dafrito.gui.ExtensionFilter;
import com.dafrito.ide.legacy.actions.CloseScriptAction;
import com.dafrito.ide.legacy.actions.CompileAction;
import com.dafrito.ide.legacy.actions.CompileRunnable;
import com.dafrito.ide.legacy.actions.ExecuteAction;
import com.dafrito.ide.legacy.actions.ExecuteRunnable;
import com.dafrito.ide.legacy.actions.NewScriptAction;
import com.dafrito.ide.legacy.actions.OpenScriptAction;
import com.dafrito.ide.legacy.actions.SaveScriptAction;
import com.dafrito.ide.legacy.actions.SaveScriptAsAction;
import com.dafrito.logging.LegacyDebugger;
import com.dafrito.logging.LogFilter;
import com.dafrito.logging.LogManager;
import com.dafrito.logging.LogView;
import com.dafrito.logging.tree.LogViewTreeNode;
import com.dafrito.script.ScriptEnvironment;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import java.io.File;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class CodeEnvironment extends JPanel implements ActionListener, ChangeListener, LogManager,
                                                       ScriptEditorContext {


    private JLabel status = new JLabel();

    private JTabbedPane tabbedPane = new JTabbedPane();
    private JTabbedPane filteredPanes = new JTabbedPane();
    private JMenuItem exit, reset;
    private JMenuItem undo, redo;
    private JMenuItem removeTab, clearTab, createListener, renameTab;
    private JMenuItem addException, addExceptionFromList, addIgnore, addIgnoreFromList, removeException, removeIgnore;
    private JMenuItem addAssertionFailure, removeAssertionFailure;
    private JRadioButtonMenuItem exceptionsMode, ignoreMode;
    public JMenuBar menuBar;
    private JMenu parserMenu, editMenu, listenerMenu;
    private LogView filtering;

    private Map<String, List<LogView>> filteredOutputMap = new HashMap<String, List<LogView>>();
    private String bootstrappingClassName;

    private List<String> exceptions = new ArrayList<String>();
    private List<String> ignores = new ArrayList<String>();
    private List<String> allThreads = new ArrayList<String>();

    private ScriptEnvironment environment;

    private JMenuItem compileAndRun;

    private List<ScriptEditor> scriptElements = new ArrayList<ScriptEditor>();

    public void compile() {
        this.setStatus("Compiling...");
        new Thread(new CompileRunnable(this)).start();
    }

    public void execute() {
        new Thread(new ExecuteRunnable(this)).start();
    }

    public CodeEnvironment() {
        LegacyDebugger.setDebugger(this);

        this.allThreads.add("AWT-EventQueue-0");
        // this.allThreads.add(CompileThread.COMPILETHREADSTRING);
        // this.allThreads.add(ExecutionThread.EXECUTIONTHREADSTRING);
        this.allThreads.add(PolygonPipeline.POLYGONPIPELINESTRING);
        this.allThreads.add(SplitterThread.SPLITTERTHREADSTRING);

        this.setLayout(new BorderLayout());

        this.setStatus("Ready");
        this.add(this.status, BorderLayout.SOUTH);

        this.add(this.tabbedPane, BorderLayout.CENTER);
        this.tabbedPane.addChangeListener(this);


        this.tabbedPane.add("Debug Output", this.filteredPanes);

        this.menuBar = new JMenuBar();
        this.add(this.menuBar, BorderLayout.NORTH);

        // File Menu

        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');
        fileMenu.add(new NewScriptAction(this));
        fileMenu.add(new OpenScriptAction(this));
        fileMenu.add(new SaveScriptAction(this));
        fileMenu.add(new SaveScriptAsAction(this));
        fileMenu.add(new CloseScriptAction(this));
        //        fileMenu.add(new ResetAction(this));
        //        fileMenu.add(new ExitAction(this));
        this.menuBar.add(fileMenu);

        this.reset = new JMenuItem("Reset");
        fileMenu.add(this.reset);

        this.exit = new JMenuItem("Exit", 'X');
        this.exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        this.exit.addActionListener(this);
        fileMenu.add(this.exit);

        // Edit Menu

        this.editMenu = new JMenu("Edit");
        this.editMenu.setMnemonic('E');
        this.menuBar.add(this.editMenu);

        this.undo = new JMenuItem("Undo", 'U');
        this.undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
        this.undo.addActionListener(this);
        this.editMenu.add(this.undo);

        this.redo = new JMenuItem("Redo", 'R');
        this.redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
        this.redo.addActionListener(this);
        this.editMenu.add(this.redo);

        // Parser Menu

        this.parserMenu = new JMenu("Parser");
        this.parserMenu.setMnemonic('P');
        this.menuBar.add(this.parserMenu);

        this.parserMenu.add(new CompileAction(this));
        this.parserMenu.add(new ExecuteAction(this));

        this.compileAndRun = new JMenuItem("Compile and Run", 'R');
        this.compileAndRun.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, ActionEvent.CTRL_MASK));
        this.compileAndRun.addActionListener(this);
        this.parserMenu.add(this.compileAndRun);

        // Debug Menu

        JMenu debugMenu = new JMenu("Debugger");
        this.menuBar.add(debugMenu);

        ButtonGroup filteringModes = new ButtonGroup();

        this.exceptionsMode = new JRadioButtonMenuItem("Lazy Filter Mode");
        this.exceptionsMode.setSelected(true);
        filteringModes.add(this.exceptionsMode);
        debugMenu.add(this.exceptionsMode);

        this.ignoreMode = new JRadioButtonMenuItem("Greedy Filter Mode");
        filteringModes.add(this.ignoreMode);
        debugMenu.add(this.ignoreMode);

        debugMenu.addSeparator();

        debugMenu.add(this.addException = new JMenuItem("Add Exception"));
        debugMenu.add(this.addExceptionFromList = new JMenuItem("Add Exception From List..."));
        debugMenu.add(this.removeException = new JMenuItem("Remove Exception..."));

        debugMenu.addSeparator();

        debugMenu.add(this.addIgnore = new JMenuItem("Add to Ignore List"));
        debugMenu.add(this.addIgnoreFromList = new JMenuItem("Add Ignore From List..."));
        debugMenu.add(this.removeIgnore = new JMenuItem("Remove Ignore..."));

        debugMenu.addSeparator();

        debugMenu.add(this.addAssertionFailure = new JMenuItem("Add Assertion Failure"));
        debugMenu.add(this.removeAssertionFailure = new JMenuItem("Remove Assertion Failure"));

        // Listener Menu

        this.listenerMenu = new JMenu("Listener");
        this.menuBar.add(this.listenerMenu);

        this.listenerMenu.setMnemonic('L');
        this.listenerMenu.add(this.createListener = new JMenuItem("Create Listener...", 'C'));
        this.listenerMenu.add(this.renameTab = new JMenuItem("Rename Tab...", 'N'));
        this.listenerMenu.add(this.clearTab = new JMenuItem("Clear Tab", 'C'));
        this.listenerMenu.add(this.removeTab = new JMenuItem("Remove Tab", 'R'));

        this.clearTab.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        this.clearTab.addActionListener(this);

        this.removeTab.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
        this.removeTab.addActionListener(this);


        this.reset.addActionListener(this);
        this.renameTab.addActionListener(this);
        this.filteredPanes.addChangeListener(this);
        this.createListener.addActionListener(this);
        this.addException.addActionListener(this);
        this.addExceptionFromList.addActionListener(this);
        this.addIgnoreFromList.addActionListener(this);
        this.addIgnore.addActionListener(this);
        this.removeException.addActionListener(this);
        this.removeIgnore.addActionListener(this);
        this.addAssertionFailure.addActionListener(this);
        this.removeAssertionFailure.addActionListener(this);

        this.openScripts(new File("."));

        this.environment = new ScriptEnvironment();
    }

    private void openScripts(File folder) {
        ExtensionFilter filter = new ExtensionFilter();
        filter.addExtension("RiffScript");
        File[] files = folder.listFiles(filter);
        for (File file : files) {
            if (file.isFile()) {
                this.addScriptEditor(new ScriptEditor(this, file));
            }
        }
    }

    public void setExceptionsMode(boolean value) {
        this.exceptionsMode.setSelected(value);
        this.ignoreMode.setSelected(!value);
    }

    public boolean isInExceptionsMode() {
        return this.exceptionsMode.isSelected();
    }


    public void addExceptions(java.util.List<Exception> exceptionsToAdd) {
        for (Exception rawEx : exceptionsToAdd) {
            if (rawEx instanceof Exception_Nodeable && !((Exception_Nodeable)rawEx).isAnonymous()) {
                Exception_Nodeable ex = (Exception_Nodeable)rawEx;
                this.getScriptEditor(ex.getFilename()).addException(ex);
                this.tabbedPane.setTitleAt(this.scriptElements.indexOf(this.getScriptEditor(ex.getFilename())) + 1,
                                           this.getScriptEditor(ex.getFilename()).getName());
            } else {
                this.scriptElements.get(0).addException(rawEx);
            }
        }
    }

    public void setTitleAt(int i, String title) {
        this.tabbedPane.setTitleAt(i, title);
    }

    public void setFilteringOutput(LogView output) {
        this.filtering = output;
    }

    public LogView getFilteringOutput() {
        return this.filtering;
    }

    public String getBootstrappingClassName() {
        return this.bootstrappingClassName;
    }


    public void setBootstrappingClassName(String bootstrappingClassName) {
        this.bootstrappingClassName = bootstrappingClassName;
    }

    public LogView isFilterUsed(Object filter, String threadName) {
        for (LogView listener : this.filteredOutputMap.get(threadName)) {
            if (!listener.isUnfiltered() && listener.getTreePanel().getFilter().isFilterUsed(filter)) {
                return listener;
            }
        }
        return null;
    }

    public void removeListenerListener(LogView listener) {
        this.filteredPanes.removeTabAt(this.filteredPanes.getSelectedIndex());
        this.filteredOutputMap.get(listener.getThreadName()).remove(listener);
    }

    public LogView addOutputListener(LogView source, Object filter) {
        if (filter == null || "".equals(filter)) {
            return null;
        }
        if (isFilterUsed(filter, source.getThreadName()) != null) {
            JOptionPane.showMessageDialog(this, "An output listener has an identical filter to the one provided.",
                                          "Listener Already Exists", JOptionPane.INFORMATION_MESSAGE);
            focusOnOutput(isFilterUsed(filter, source.getThreadName()));
            return null;
        }
        LogView output = new LogView(source.getThreadName(), this, source, filter.toString());
        output.getTreePanel().getFilter().addFilter(filter);
        output.getTreePanel().refresh();
        source.addChildOutput(output);
        this.filteredPanes.add(filter.toString(), output);
        this.filteredPanes.setSelectedIndex(this.filteredPanes.getComponentCount() - 1);
        this.filteredOutputMap.get(source.getThreadName()).add(output);
        return output;
    }

    public void focusOnOutput(LogView output) {
        assert output != null;
        this.filteredPanes.setSelectedIndex(this.filteredPanes.indexOfComponent(output));
    }

    public void addScriptEditor(ScriptEditor element) {
        this.tabbedPane.add(element.getName(), element);
        this.scriptElements.add(element);
        this.tabbedPane.setSelectedIndex(this.tabbedPane.getComponents().length - 1);
    }

    // Node stuff

    public void openNode(LogViewTreeNode node) {
        addNode(node, true);
    }

    public boolean ensureCurrentNode(Object obj) {
        if (!this.reset.isEnabled()) {
            return true;
        }
        if (isInExceptionsMode() && !this.exceptions.contains(Thread.currentThread().getName())) {
            return true;
        }
        if (this.ignores.contains(Thread.currentThread().getName())) {
            return true;
        }
        Object data = getUnfilteredCurrentNode().getData();
        return data.equals(obj);
    }

    public LogViewTreeNode getUnfilteredCurrentNode() {
        return getUnfilteredOutput().getTreePanel().getCurrentNode();
    }

    // Listeners

    public void actionPerformed(ActionEvent event) {
        if (event.getActionCommand().equals("Exit")) {
            List<ScriptEditor> removedElements = new ArrayList<ScriptEditor>();
            int index = this.tabbedPane.getSelectedIndex();
            while (!this.scriptElements.isEmpty()) {
                ScriptEditor element = this.scriptElements.get(0);
                if (!element.closeFile()) {
                    for (ScriptEditor added : removedElements) {
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
            // TODO: This seems reckless; we shouldn't automatically exit everything here.
            System.exit(0);
            // } else if(event.getSource().equals(this.closeFile)) {
            /* if((this.scriptElements.get(this.tabbedPane.getSelectedIndex() - 1)).closeFile()) {
                int index = this.tabbedPane.getSelectedIndex();
                this.tabbedPane.remove(index);
                this.scriptElements.remove(index - 1);
            }*/
        } else if (event.getSource().equals(this.renameTab)) {
            Object text = JOptionPane.showInputDialog(null, "Insert new output name", "Rename Output",
                                                      JOptionPane.QUESTION_MESSAGE, null, null,
                                                      this.filteredPanes.getTitleAt(this.filteredPanes.getSelectedIndex()));
            if (text != null) {
                this.filteredPanes.setTitleAt(this.filteredPanes.getSelectedIndex(), text.toString());
            }
        } else if (event.getSource().equals(this.createListener)) {
            ((LogView)this.filteredPanes.getSelectedComponent()).promptCreateListener();
        } else if (event.getSource().equals(this.reset)) {
            reset();
        } else if (event.getSource().equals(this.clearTab)) {
            ((LogView)this.filteredPanes.getSelectedComponent()).clearTab();
        } else if (event.getSource().equals(this.removeTab)) {
            ((LogView)this.filteredPanes.getSelectedComponent()).removeTab();
            this.filteredOutputMap.get(((LogView)this.filteredPanes.getSelectedComponent()).getThreadName()).remove(this.filteredPanes.getSelectedComponent());
            this.filteredPanes.remove(this.filteredPanes.getSelectedComponent());
        } else if (event.getSource().equals(this.undo)) {
            this.scriptElements.get(this.tabbedPane.getSelectedIndex() - 1).undo();
        } else if (event.getSource().equals(this.addException)) {
            Object string = null;
            if (this.filteredPanes.getSelectedComponent() != null) {
                string = ((LogView)this.filteredPanes.getSelectedComponent()).getThreadName();
            }
            Object text = JOptionPane.showInputDialog(null, "Insert the thread name to add to the exceptions list",
                                                      "Adding to Exceptions List", JOptionPane.PLAIN_MESSAGE, null,
                                                      null, string);
            if (text == null) {
                return;
            }
            this.exceptions.add(text.toString());
        } else if (event.getSource().equals(this.addExceptionFromList)) {
            if (this.allThreads.size() == 0) {
                JOptionPane.showMessageDialog(null, "There are no threads in the selection list.", "Empty Thread List",
                                              JOptionPane.WARNING_MESSAGE);
                return;
            }
            Object text = JOptionPane.showInputDialog(null, "Select the thread name to add to the exceptions list",
                                                      "Adding to Exceptions List", JOptionPane.PLAIN_MESSAGE, null,
                                                      this.allThreads.toArray(), null);
            if (text == null) {
                return;
            }
            this.exceptions.add(text.toString());
        } else if (event.getSource().equals(this.removeException)) {
            if (this.exceptions == null || this.exceptions.size() == 0) {
                JOptionPane.showMessageDialog(null, "No threads to remove from exceptions list.",
                                              "Empty Exception List", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Object text = JOptionPane.showInputDialog(null,
                                                      "Select the thread name to remove from the exceptions list",
                                                      "Removing Exception", JOptionPane.PLAIN_MESSAGE, null,
                                                      this.exceptions.toArray(), null);
            if (text == null) {
                return;
            }
            this.exceptions.remove(text.toString());
        } else if (event.getSource().equals(this.addIgnore)) {
            Object string = null;
            if (this.filteredPanes.getSelectedComponent() != null) {
                string = ((LogView)this.filteredPanes.getSelectedComponent()).getThreadName();
            }
            Object text = JOptionPane.showInputDialog(null, "Insert the thread name to add to the ignore list",
                                                      "Adding to Ignore List", JOptionPane.PLAIN_MESSAGE, null, null,
                                                      string);
            if (text == null) {
                return;
            }
            this.ignores.add(text.toString());
        } else if (event.getSource().equals(this.addIgnoreFromList)) {
            if (this.allThreads == null || this.allThreads.size() == 0) {
                JOptionPane.showMessageDialog(null, "There are no threads in the selection list.", "Empty Thread List",
                                              JOptionPane.WARNING_MESSAGE);
                return;
            }
            Object text = JOptionPane.showInputDialog(null, "Select the thread name to add to the ignore list",
                                                      "Adding to Ignore List", JOptionPane.PLAIN_MESSAGE, null,
                                                      this.allThreads.toArray(), null);
            if (text == null) {
                return;
            }
            this.ignores.add(text.toString());
        } else if (event.getSource().equals(this.removeIgnore)) {
            if (this.ignores == null || this.ignores.size() == 0) {
                JOptionPane.showMessageDialog(null, "No threads to remove from ignore list.", "Empty Ignore List",
                                              JOptionPane.WARNING_MESSAGE);
                return;
            }
            Object text = JOptionPane.showInputDialog(null, "Select the thread name to remove from the ignore list",
                                                      "Removing Ignore", JOptionPane.PLAIN_MESSAGE, null,
                                                      this.ignores.toArray(), null);
            if (text == null) {
                return;
            }
            this.ignores.remove(text.toString());
        } else if (event.getSource().equals(this.redo)) {
            this.scriptElements.get(this.tabbedPane.getSelectedIndex() - 1).redo();
        } else {
            throw new IllegalArgumentException("Unhandled event");
        }
    }

    public boolean isResetting() {
        return !this.reset.isEnabled();
    }

    public void setStatus(String text) {
        this.status.setText(" " + text);
    }

    public ScriptEditor getScriptEditor(String name) {
        for (ScriptEditor element : this.scriptElements) {
            if (element.getFilename().equals(name)) {
                return element;
            }
        }
        throw new IllegalArgumentException("Script element not found: " + name);
    }

    public ScriptEditor getCurrentEditor() {
        Component selected = this.tabbedPane.getSelectedComponent();
        return selected instanceof ScriptEditor ? (ScriptEditor)selected : null;
    }

    public void setChanged(boolean changed) {
        this.tabbedPane.setTitleAt(this.tabbedPane.getSelectedIndex(),
                                   this.scriptElements.get(this.tabbedPane.getSelectedIndex() - 1).getName());
    }

    public void stateChanged(ChangeEvent e) {
        // TODO Commented out until we get a handle on how this system should actually work.
        /*if(this.tabbedPane.getSelectedIndex() == 0) {
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
            setChanged(this.scriptElements.get(this.tabbedPane.getSelectedIndex() - 1).isDirty());
            setCanUndo(this.scriptElements.get(this.tabbedPane.getSelectedIndex() - 1).canUndo());
            setCanRedo(this.scriptElements.get(this.tabbedPane.getSelectedIndex() - 1).canRedo());
        }*/
    }

    public List<ScriptEditor> getScriptElements() {
        return this.scriptElements;
    }

    //-------------------------------------------------------------------------
    //
    //  DebugNodeListener implementation
    //
    //-------------------------------------------------------------------------
    /*void addNode(Debug_TreeNode debug_TreeNode);
    Debug_TreeNode getLastNodeAdded();
    Debug_Listener getUnfilteredOutput();
    boolean isIgnoringThisThread();
    void closeNode();
    void closeNodeTo(Object nodeValue);
    void openNode(Debug_TreeNode debug_TreeNode);

    void reset();
    boolean ensureCurrentNode(Object nodeValue);

    String getBootstrappingClassName();
    void setBootstrappingClassName(String name);
    boolean isResetting();
    void setExceptionsMode(boolean value);*/

    public void addNode(LogViewTreeNode node) {
        addNode(node, false);
    }

    public void addNode(LogViewTreeNode node, boolean setAsCurrent) {
        if (isIgnoringThisThread()) {
            return;
        }
        String threadName = Thread.currentThread().getName();
        List<LogView> listeners = this.filteredOutputMap.get(threadName);
        if (listeners == null || listeners.isEmpty()) {
            listeners = new ArrayList<LogView>();
            this.filteredOutputMap.put(threadName, listeners);
            listeners.add(new LogView(threadName, this, null, threadName));
            this.filteredPanes.add(threadName, listeners.get(0));
        }
        for (LogView listener : listeners) {
            if (listener.isCapturing() == false)
                continue;
            LogFilter filter = listener.getTreePanel().getFilter();
            if (!filter.isListening()) {
                if (filter.sniffNode(node)) {
                    this.addNodeToOutput(node, setAsCurrent, listener);
                }
            } else {
                addNodeToOutput(node, setAsCurrent, listener);
            }
            return;
        }
        for (LogView listener : listeners) {
            addNodeToOutput(node.duplicate(), setAsCurrent, listener);
        }
    }

    private void addNodeToOutput(LogViewTreeNode passedNode, boolean setAsCurrent, LogView listener) {
        LogViewTreeNode node = passedNode;
        if (!listener.getTreePanel().getFilter().isListening()) {
            if (!listener.getTreePanel().getFilter().sniffNode(node))
                return;
            node = new LogViewTreeNode(listener.getSource().getTreePanel().getLastNodeAdded());
        }
        if (listener.getTreePanel().getCurrentNode().hasChildren() && listener.getTreePanel().getCurrentNode().getLastChild().getGroup() != null && node.getGroup() != null && listener.getTreePanel().getCurrentNode().getLastChild().getGroup().equals(node.getGroup())) {
            if (listener.getTreePanel().getCurrentNode().getLastChild().getData().equals(node.getGroup())) {
                listener.getTreePanel().getCurrentNode().getLastChild().addChild(node);
                node.setPracticalParent(listener.getTreePanel().getCurrentNode());
                if (setAsCurrent) {
                    listener.getTreePanel().setAsCurrent(node);
                }
            } else {
                LogViewTreeNode lastNode = listener.getTreePanel().getCurrentNode().removeLastChild();
                LogViewTreeNode groupNode = new LogViewTreeNode(node.getGroupCode(), node.getGroupCode());
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

    public void closeNode() {
        if (isIgnoringThisThread()) {
            return;
        }
        for (LogView listener : this.filteredOutputMap.get(Thread.currentThread().getName())) {
            if (listener.isCapturing() && listener.getTreePanel().getFilter().isListening()) {
                listener.getTreePanel().closeNode();
                return;
            }
        }
        for (LogView listener : this.filteredOutputMap.get(Thread.currentThread().getName())) {
            if (listener.getTreePanel().getFilter().isListening()) {
                listener.getTreePanel().closeNode();
            }
        }
    }

    public void closeNodeTo(Object string) {
        if (isIgnoringThisThread()) {
            return;
        }
        for (LogView listener : this.filteredOutputMap.get(Thread.currentThread().getName())) {
            while (listener.getTreePanel().getFilter().isListening() && !listener.getTreePanel().getCurrentNode().getData().equals(string)) {
                listener.getTreePanel().closeNode();
            }
        }
    }

    public LogView getUnfilteredOutput() {
        return this.filteredOutputMap.get(Thread.currentThread().getName()).get(0);
    }

    public LogViewTreeNode getLastNodeAdded() {
        return getUnfilteredOutput().getTreePanel().getLastNodeAdded();
    }

    public boolean isIgnoringThisThread() {
        if (!this.reset.isEnabled()) {
            return true;
        }
        String currentThread = Thread.currentThread().getName();
        if (isInExceptionsMode()) {
            for (String thread : this.exceptions) {
                if (currentThread.contains(thread))
                    return false;
            }
            return true;
        }
        for (String thread : this.ignores) {
            if (currentThread.contains(thread))
                return true;
        }
        return false;
    }

    public void reset() {
        this.reset.setEnabled(false);
        this.filteredPanes.setSelectedIndex(0);
        int children = this.filteredPanes.getComponentCount();
        for (int i = 0; i < children; i++) {
            ((LogView)this.filteredPanes.getComponent(i)).removeTab();
        }
        this.filteredPanes.removeAll();
        this.filteredOutputMap.clear();
        LogViewTreeNode.reset();
        this.reset.setEnabled(true);
    }

    //-------------------------------------------------------------------------
    //
    //  ScriptEditorContext implementation
    //
    //-------------------------------------------------------------------------

    public ScriptEnvironment getEnvironment() {
        return this.environment;
    }

    public void resetTitle(ScriptEditor element) {
        this.tabbedPane.setTitleAt(this.scriptElements.indexOf(element) + 1, element.getName());
    }

    public void showScriptEditor(ScriptEditor element) {
        this.tabbedPane.setSelectedIndex(this.scriptElements.indexOf(element) + 1);
    }


    public Component getComponent() {
        return this;
    }

    public void setCanRedo(boolean redo) {
        // TODO Auto-generated method stub

    }

}
