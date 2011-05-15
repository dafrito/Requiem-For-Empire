import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Stack;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.CompoundEdit;

public class Debug_ScriptElement extends JPanel implements UndoableEditListener, ListSelectionListener, ComponentListener, MouseListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1513897604566683983L;
	private File m_file;
	private boolean m_hasChanged, m_isValid;
	private static int m_fileNumber = 1;
	private Debug_Environment m_debugger;
	private JTextArea m_textArea;
	private Stack<CompoundEdit> m_edits = new Stack<CompoundEdit>();
	private Stack<CompoundEdit> m_undoneEdits = new Stack<CompoundEdit>();
	private long m_lastEdit;
	private JList m_errors;
	private JSplitPane m_splitPane;
	private int m_width;
	private Vector<Exception> m_exceptions;
	private Vector<String> m_displayedExceptions;
	private String m_prefix = "";

	public Debug_ScriptElement(Debug_Environment debugger) {
		this.m_debugger = debugger;
		if (this.selectFile(false)) {
			this.m_isValid = this.openFile();
		}
	}

	public Debug_ScriptElement(Debug_Environment debugger, File file) {
		this.m_debugger = debugger;
		this.m_isValid = this.openFile(file);
	}

	public Debug_ScriptElement(Debug_Environment debugger, String string) {
		this.m_debugger = debugger;
		if (string != null) {
			this.m_file = new File(string);
		}
		this.m_isValid = this.openFile();
	}

	public void addException(Exception exception) {
		this.m_exceptions.add(exception);
		this.m_prefix = "X ";
		this.m_errors.setListData(this.m_displayedExceptions);
		this.m_errors.setBorder(BorderFactory.createTitledBorder(this.getFilename() + "(" + this.m_exceptions.size() + " error(s))"));
		if (exception instanceof Exception_Nodeable) {
			this.m_displayedExceptions.add(((Exception_Nodeable) exception).getName());
		} else {
			this.m_displayedExceptions.add(exception.getMessage());
		}
	}

	public boolean canRedo() {
		return this.m_undoneEdits.size() > 0;
	}

	public boolean canUndo() {
		return this.m_edits.size() > 0;
	}

	public boolean closeFile() {
		this.m_debugger.showReferenced(this);
		if (!this.hasChanged()) {
			return true;
		}
		int option = JOptionPane.showConfirmDialog(this.m_debugger, "This file has unsaved changes. Save?", this.getName(), JOptionPane.YES_NO_CANCEL_OPTION);
		if (option == JOptionPane.NO_OPTION) {
			return true;
		}
		if (option == JOptionPane.CANCEL_OPTION) {
			return false;
		}
		return this.saveFile();
	}

	public boolean compile() {
		String text = this.m_textArea.getText();
		String[] stringArray = text.split("\n");
		java.util.List<Object> strings = new LinkedList<Object>();
		for (int i = 0; i < stringArray.length; i++) {
			strings.add(new ScriptLine(this.m_debugger.getEnvironment(), this.getFilename(), i + 1, stringArray[i]));
		}
		this.m_width = this.getWidth();
		this.m_splitPane.setRightComponent(new JScrollPane(this.m_errors = new JList()));
		this.m_splitPane.setDividerLocation(this.getWidth() - 200);
		this.m_errors.addListSelectionListener(this);
		this.m_errors.addMouseListener(this);
		this.m_exceptions = Parser.preparseFile(this.m_debugger.getEnvironment(), this.getFilename(), strings);
		this.m_displayedExceptions = new Vector<String>();
		if (this.m_exceptions.size() == 0) {
			this.m_errors.setBorder(BorderFactory.createTitledBorder("Compiled Successfully"));
			this.m_prefix = "";
			return true;
		} else {
			for (Exception ex : this.m_exceptions) {
				if (ex instanceof Exception_Nodeable) {
					this.m_displayedExceptions.add(((Exception_Nodeable) ex).getName());
				} else if (ex instanceof Exception_InternalError) {
					this.m_displayedExceptions.add(((Exception_InternalError) ex).getName());
				} else {
					this.m_displayedExceptions.add(ex.getMessage());
				}
			}
			this.m_errors.setListData(this.m_displayedExceptions);
			this.m_errors.setBorder(BorderFactory.createTitledBorder(this.getName() + "(" + this.m_exceptions.size() + " error(s))"));
			this.m_prefix = "X ";
			return false;
		}
	}

	@Override
	public void componentHidden(ComponentEvent x) {
	}

	@Override
	public void componentMoved(ComponentEvent x) {
	}

	@Override
	public void componentResized(ComponentEvent x) {
		double location = ((double) this.m_splitPane.getDividerLocation()) / (double) this.m_width;
		if (location > 1) {
			location = 1;
		}
		this.m_splitPane.setDividerLocation((int) (this.getWidth() * location));
		this.m_width = this.getWidth();
	}

	@Override
	public void componentShown(ComponentEvent x) {
	}

	public String getFilename() {
		if (this.m_file != null) {
			return this.m_file.getName();
		}
		return "Untitled " + m_fileNumber++;
	}

	@Override
	public String getName() {
		return this.m_prefix + this.getFilename();
	}

	public boolean hasChanged() {
		return this.m_hasChanged;
	}

	@Override
	public boolean isValid() {
		return this.m_isValid;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() != 2) {
			return;
		}
		if (this.m_errors.getSelectedValue() == null) {
			return;
		}
		Exception rawEx = this.m_exceptions.get(this.m_errors.getSelectedIndex());
		if (!(rawEx instanceof Exception_Nodeable)) {
			return;
		}
		Exception_Nodeable ex = (Exception_Nodeable) rawEx;
		String[] array = this.m_textArea.getText().split("\n");
		int offset = 0;
		this.m_textArea.requestFocus();
		for (int i = 0; i < ex.getLineNumber(); i++) {
			if (array.length - 1 == i) {
				this.m_textArea.setCaretPosition(offset);
				return;
			}
			offset += array[i].length() + 1;
			if (i == ex.getLineNumber() - 1) {
				i++;
				if (array[i].length() < ex.getOffset()) {
					this.m_textArea.setCaretPosition(offset + array[i].length());
					return;
				}
				if (array[i].substring(ex.getOffset()).length() < (ex.getLength() - ex.getOffset())) {
					this.m_textArea.setCaretPosition(offset + ex.getOffset());
					this.m_textArea.select(offset + ex.getOffset(), offset + array[i].length() - ex.getOffset());
					return;
				}
				break;
			}
		}
		this.m_textArea.select(offset + ex.getOffset() + 1, 1 + offset + ex.getOffset() + ex.getLength());
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	public boolean openFile() {
		return this.openFile(this.m_file);
	}

	public boolean openFile(File file) {
		this.setLayout(new GridLayout(0, 1));
		this.m_splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		this.m_splitPane.add(new JScrollPane(this.m_textArea = new JTextArea()));
		this.add(this.m_splitPane);
		this.m_textArea.setFont(new Font("Courier", Font.PLAIN, 12));
		if (file != null) {
			this.m_file = file;
			if (!this.readFile()) {
				return false;
			}
		}
		this.m_textArea.getDocument().addUndoableEditListener(this);
		this.addComponentListener(this);
		this.setVisible(true);
		return true;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
	}

	public boolean readFile() {
		return this.readFile(this.m_file);
	}

	public boolean readFile(File file) {
		try {
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
			byte[] b = new byte[in.available()];
			in.read(b, 0, b.length);
			this.m_textArea.setText(new String(b, 0, b.length));
			in.close();
			return true;
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(this, "File failed to open: " + file.getName(), "IOException", JOptionPane.ERROR_MESSAGE);
		}
		return false;
	}

	public void redo() {
		this.setChanged(true);
		if (this.m_undoneEdits.size() > 0) {
			CompoundEdit edit = this.m_undoneEdits.pop();
			edit.redo();
			this.m_edits.push(edit);
			if (this.m_undoneEdits.size() == 0) {
				this.m_debugger.setCanRedo(false);
			}
			this.m_debugger.setCanUndo(true);
		}
	}

	public boolean saveFile() {
		if (!this.hasChanged()) {
			return true;
		}
		if (this.m_file == null) {
			return this.saveFileAs();
		}
		return this.writeFile();
	}

	public boolean saveFileAs() {
		this.m_debugger.showReferenced(this);
		if (!this.selectFile(true)) {
			return false;
		}
		if (!this.writeFile()) {
			return false;
		}
		this.m_debugger.resetTitle(this);
		return true;
	}

	public boolean selectFile(boolean isSaving) {
		JFileChooser fileChooser = new JFileChooser(".");
		ExtensionFilter filter = new ExtensionFilter("RiffScripts");
		filter.addExtension("RiffScript");
		fileChooser.setFileFilter(filter);
		int choice = 0;
		if (isSaving) {
			choice = fileChooser.showSaveDialog(this);
		} else {
			choice = fileChooser.showOpenDialog(this);
		}
		if (choice == JFileChooser.APPROVE_OPTION) {
			if (fileChooser.getSelectedFile().getName().indexOf(".") == -1) {
				this.m_file = new File(fileChooser.getSelectedFile().getName() + ".RiffScript");
			} else {
				this.m_file = fileChooser.getSelectedFile();
			}
			return true;
		}
		return false;
	}

	public void setChanged(boolean changed) {
		this.m_hasChanged = changed;
		if (changed) {
			this.m_prefix = "* ";
		} else {
			this.m_prefix = "";
		}
		this.m_debugger.setChanged(changed);
	}

	public void setPrefix(String prefix) {
		this.m_prefix = prefix;
	}

	public void undo() {
		this.setChanged(true);
		this.m_edits.peek().end();
		if (this.m_edits.size() > 0) {
			CompoundEdit edit = this.m_edits.pop();
			if (edit.canUndo()) {
				edit.undo();
			}
			this.m_undoneEdits.push(edit);
			if (this.m_edits.size() == 0) {
				this.m_debugger.setCanUndo(false);
			}
			this.m_debugger.setCanRedo(true);
		}
	}

	@Override
	public void undoableEditHappened(UndoableEditEvent e) {
		if (this.m_edits.size() > 0) {
			if (System.currentTimeMillis() - this.m_lastEdit > 1000) {
				this.m_edits.peek().end();
			} else {
				this.m_edits.peek().addEdit(e.getEdit());
				return;
			}
		}
		if (e.getEdit().isSignificant()) {
			this.setChanged(true);
			this.m_debugger.setCanUndo(true);
			CompoundEdit edit = new CompoundEdit();
			edit.addEdit(e.getEdit());
			this.m_edits.add(edit);
			this.m_undoneEdits.clear();
		}
		this.m_lastEdit = System.currentTimeMillis();
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {

	}

	public boolean writeFile() {
		return this.writeFile(this.m_file);
	}

	public boolean writeFile(File file) {
		try {
			FileWriter writer = new FileWriter(file);
			writer.write(this.m_textArea.getText(), 0, this.m_textArea.getText().length());
			writer.close();
			this.setChanged(false);
			return true;
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(this, "File failed to save: " + file.getName(), "IOException", JOptionPane.ERROR_MESSAGE);
		}
		return false;
	}
}
