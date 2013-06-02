/**
 * Copyright (c) 2013 Aaron Faanes
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.dafrito.rfe.gui.script;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

import com.dafrito.rfe.logging.Logs;
import com.dafrito.rfe.script.CompileRunnable;
import com.dafrito.rfe.script.ExecutionThread;
import com.dafrito.rfe.script.ScriptEnvironment;
import com.dafrito.rfe.script.exceptions.ScriptException;
import com.dafrito.rfe.strings.ExtensionFilter;

/**
 * @author Aaron Faanes
 * 
 */
public class ScriptEditor extends JFrame {

	private final JTabbedPane scriptElements = new JTabbedPane();

	private final JLabel status = new JLabel();

	private final JMenuBar menuBar = new JMenuBar();

	private final JMenuItem newFile = new JMenuItem("New", 'N');
	private final JMenuItem openFile = new JMenuItem("Open...", 'O');
	private final JMenuItem closeFile = new JMenuItem("Close");

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

	private final JMenuItem saveFile = new JMenuItem("Save", 'S');

	private ScriptEnvironment scriptEnvironment;

	public ScriptEditor() {
		super("RFE Script Editor");

		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(this.status, BorderLayout.SOUTH);
		this.getContentPane().add(this.scriptElements);

		this.setJMenuBar(this.menuBar);

		JMenu fileMenu = new JMenu("File");
		this.menuBar.add(fileMenu);
		fileMenu.setMnemonic('F');

		fileMenu.add(this.newFile);
		this.newFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		fileMenu.add(this.openFile);
		this.newFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addReferenced(new ScriptPanel(ScriptEditor.this, (String) null));
			}
		});

		this.openFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		fileMenu.add(this.closeFile);
		this.openFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ScriptPanel element = new ScriptPanel(ScriptEditor.this);
				if (element.hasFile()) {
					addReferenced(element);
				}
			}
		});

		this.closeFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
		this.closeFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (getSelectedScriptPanel().closeFile()) {
					int index = scriptElements.getSelectedIndex();
					scriptElements.remove(index);
				}
			}
		});

		saveFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		saveFile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				getSelectedScriptPanel().saveFile();
			}
		});
		fileMenu.add(saveFile);

		JMenuItem saveFileAs = new JMenuItem("Save As...", 'A');
		saveFileAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		saveFileAs.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				getSelectedScriptPanel().saveFileAs();
			}
		});
		fileMenu.add(saveFileAs);

		JMenuItem report = new JMenuItem("Report", 'T');
		report.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0));
		report.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Logs.report();
			}
		});
		fileMenu.add(report);

		JMenuItem exit = new JMenuItem("Exit", 'X');
		exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		exit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				for (int i = 0; i < scriptElements.getComponentCount(); ++i) {
					ScriptPanel element = (ScriptPanel) scriptElements.getComponentAt(i);
					if (!element.closeFile()) {
						return;
					}
				}
				dispose();
			}
		});
		fileMenu.add(exit);

		this.menuBar.add(this.editMenu);
		this.editMenu.setMnemonic('E');

		this.editMenu.add(this.undo);
		this.undo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				getSelectedScriptPanel().undo();
			}
		});
		this.undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));

		this.editMenu.add(this.redo);
		this.redo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getSelectedScriptPanel().redo();
			}
		});
		this.redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));

		this.parserMenu.setMnemonic('P');
		this.menuBar.add(this.parserMenu);

		this.parserMenu.add(this.compile);
		this.compile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
		this.compile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				compile(false);
			}
		});

		this.parserMenu.add(this.execute);
		this.execute.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0));
		this.execute.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				run();
			}
		});
		this.execute.setEnabled(false);

		JMenuItem compileAndRun = new JMenuItem("Compile and Run", 'R');
		compileAndRun.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, ActionEvent.CTRL_MASK));
		compileAndRun.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				compile(true);
			}
		});
		this.parserMenu.add(compileAndRun);

		openScripts(new File("."));
	}

	private ScriptPanel getSelectedScriptPanel() {
		return (ScriptPanel) this.scriptElements.getSelectedComponent();
	}

	private void run() {
		ExecutionThread thread = new ExecutionThread(this.scriptEnvironment);
		thread.start();
	}

	private void compile(boolean runOnSuccess) {
		scriptEnvironment = new ScriptEnvironment();
		setStatus("Compiling...");
		execute.setEnabled(false);

		CompileRunnable job = new CompileRunnable(this, scriptEnvironment, runOnSuccess);
		new Thread(job, job.getName()).start();
	}

	public boolean compileAll() {
		boolean succeeded = true;
		for (int i = 0; i < scriptElements.getComponentCount(); ++i) {
			ScriptPanel panel = (ScriptPanel) scriptElements.getComponentAt(i);
			succeeded = panel.compile(this.scriptEnvironment) && succeeded;
			resetTitle(panel);
		}
		return succeeded;
	}

	/**
	 * Open all scripts in the specified directory. This will not recurse.
	 * 
	 * @param directory
	 *            the directory to open
	 */
	public void openScripts(File directory) {
		ExtensionFilter filter = new ExtensionFilter();
		filter.addExtension("RiffScript");
		File[] files = directory.listFiles(filter);
		for (File file : files) {
			if (file.isFile()) {
				this.addReferenced(new ScriptPanel(this, file));
			}
		}
	}

	public void addReferenced(ScriptPanel element) {
		this.scriptElements.add(element);
		this.scriptElements.setSelectedComponent(element);
	}

	public void canExecute(boolean value) {
		this.execute.setEnabled(value);
	}

	public ScriptPanel getReferenced(String name) {
		for (int i = 0; i < this.scriptElements.getComponentCount(); ++i) {
			ScriptPanel element = (ScriptPanel) this.scriptElements.getComponentAt(i);
			if (element.getFilename().equals(name)) {
				return element;
			}
		}
		throw new IllegalArgumentException("Script element not found: " + name);
	}

	public void setStatus(String text) {
		this.status.setText(" " + text);
	}

	public void setTitleAt(int i, String title) {
		this.scriptElements.setTitleAt(i, title);
	}

	public void showReferenced(ScriptPanel element) {
		this.scriptElements.setSelectedComponent(element);
	}

	public void addExceptions(List<? extends Exception> exceptions) {
		for (Exception rawEx : exceptions) {
			if (rawEx instanceof ScriptException && !((ScriptException) rawEx).isAnonymous()) {
				ScriptException ex = (ScriptException) rawEx;
				this.getReferenced(ex.getFilename()).addException(ex);
				resetTitle(this.getReferenced(ex.getFilename()));
			}
		}
	}

	public void resetTitle(ScriptPanel element) {
		for (int i = 0; i < this.scriptElements.getComponentCount(); ++i) {
			if (this.scriptElements.getComponentAt(i).equals(element)) {
				this.scriptElements.setTitleAt(i, element.getName());
			}
		}
	}

	public void setCanRedo(boolean canRedo) {
		this.redo.setEnabled(canRedo);
	}

	public void setCanUndo(boolean canUndo) {
		this.undo.setEnabled(canUndo);
	}

	public void setChanged(boolean changed) {
		this.saveFile.setEnabled(changed);
		resetTitle(getSelectedScriptPanel());
	}

	private static final long serialVersionUID = -7041673678775610605L;
}
