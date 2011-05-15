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
		m_debugger = debugger;
		if (selectFile(false)) {
			m_isValid = openFile();
		}
	}

	public Debug_ScriptElement(Debug_Environment debugger, File file) {
		m_debugger = debugger;
		m_isValid = openFile(file);
	}

	public Debug_ScriptElement(Debug_Environment debugger, String string) {
		m_debugger = debugger;
		if (string != null) {
			m_file = new File(string);
		}
		m_isValid = openFile();
	}

	public void addException(Exception exception) {
		m_exceptions.add(exception);
		m_prefix = "X ";
		m_errors.setListData(m_displayedExceptions);
		m_errors.setBorder(BorderFactory.createTitledBorder(getFilename() + "(" + m_exceptions.size() + " error(s))"));
		if (exception instanceof Exception_Nodeable) {
			m_displayedExceptions.add(((Exception_Nodeable) exception).getName());
		} else {
			m_displayedExceptions.add(exception.getMessage());
		}
	}

	public boolean canRedo() {
		return m_undoneEdits.size() > 0;
	}

	public boolean canUndo() {
		return m_edits.size() > 0;
	}

	public boolean closeFile() {
		m_debugger.showReferenced(this);
		if (!hasChanged()) {
			return true;
		}
		int option = JOptionPane.showConfirmDialog(m_debugger, "This file has unsaved changes. Save?", getName(), JOptionPane.YES_NO_CANCEL_OPTION);
		if (option == JOptionPane.NO_OPTION) {
			return true;
		}
		if (option == JOptionPane.CANCEL_OPTION) {
			return false;
		}
		return saveFile();
	}

	public boolean compile() {
		String text = m_textArea.getText();
		String[] stringArray = text.split("\n");
		java.util.List<Object> strings = new LinkedList<Object>();
		for (int i = 0; i < stringArray.length; i++) {
			strings.add(new ScriptLine(m_debugger.getEnvironment(), getFilename(), i + 1, stringArray[i]));
		}
		m_width = getWidth();
		m_splitPane.setRightComponent(new JScrollPane(m_errors = new JList()));
		m_splitPane.setDividerLocation(getWidth() - 200);
		m_errors.addListSelectionListener(this);
		m_errors.addMouseListener(this);
		m_exceptions = Parser.preparseFile(m_debugger.getEnvironment(), getFilename(), strings);
		m_displayedExceptions = new Vector<String>();
		if (m_exceptions.size() == 0) {
			m_errors.setBorder(BorderFactory.createTitledBorder("Compiled Successfully"));
			m_prefix = "";
			return true;
		} else {
			for (Exception ex : m_exceptions) {
				if (ex instanceof Exception_Nodeable) {
					m_displayedExceptions.add(((Exception_Nodeable) ex).getName());
				} else if (ex instanceof Exception_InternalError) {
					m_displayedExceptions.add(((Exception_InternalError) ex).getName());
				} else {
					m_displayedExceptions.add(ex.getMessage());
				}
			}
			m_errors.setListData(m_displayedExceptions);
			m_errors.setBorder(BorderFactory.createTitledBorder(getName() + "(" + m_exceptions.size() + " error(s))"));
			m_prefix = "X ";
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
		double location = ((double) m_splitPane.getDividerLocation()) / (double) m_width;
		if (location > 1) {
			location = 1;
		}
		m_splitPane.setDividerLocation((int) (getWidth() * location));
		m_width = getWidth();
	}

	@Override
	public void componentShown(ComponentEvent x) {
	}

	public String getFilename() {
		if (m_file != null) {
			return m_file.getName();
		}
		return "Untitled " + m_fileNumber++;
	}

	@Override
	public String getName() {
		return m_prefix + getFilename();
	}

	public boolean hasChanged() {
		return m_hasChanged;
	}

	@Override
	public boolean isValid() {
		return m_isValid;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() != 2) {
			return;
		}
		if (m_errors.getSelectedValue() == null) {
			return;
		}
		Exception rawEx = m_exceptions.get(m_errors.getSelectedIndex());
		if (!(rawEx instanceof Exception_Nodeable)) {
			return;
		}
		Exception_Nodeable ex = (Exception_Nodeable) rawEx;
		String[] array = m_textArea.getText().split("\n");
		int offset = 0;
		m_textArea.requestFocus();
		for (int i = 0; i < ex.getLineNumber(); i++) {
			if (array.length - 1 == i) {
				m_textArea.setCaretPosition(offset);
				return;
			}
			offset += array[i].length() + 1;
			if (i == ex.getLineNumber() - 1) {
				i++;
				if (array[i].length() < ex.getOffset()) {
					m_textArea.setCaretPosition(offset + array[i].length());
					return;
				}
				if (array[i].substring(ex.getOffset()).length() < (ex.getLength() - ex.getOffset())) {
					m_textArea.setCaretPosition(offset + ex.getOffset());
					m_textArea.select(offset + ex.getOffset(), offset + array[i].length() - ex.getOffset());
					return;
				}
				break;
			}
		}
		m_textArea.select(offset + ex.getOffset() + 1, 1 + offset + ex.getOffset() + ex.getLength());
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
		return openFile(m_file);
	}

	public boolean openFile(File file) {
		setLayout(new GridLayout(0, 1));
		m_splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		m_splitPane.add(new JScrollPane(m_textArea = new JTextArea()));
		add(m_splitPane);
		m_textArea.setFont(new Font("Courier", Font.PLAIN, 12));
		if (file != null) {
			m_file = file;
			if (!readFile()) {
				return false;
			}
		}
		m_textArea.getDocument().addUndoableEditListener(this);
		addComponentListener(this);
		setVisible(true);
		return true;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
	}

	public boolean readFile() {
		return readFile(m_file);
	}

	public boolean readFile(File file) {
		try {
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
			byte[] b = new byte[in.available()];
			in.read(b, 0, b.length);
			m_textArea.setText(new String(b, 0, b.length));
			in.close();
			return true;
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(this, "File failed to open: " + file.getName(), "IOException", JOptionPane.ERROR_MESSAGE);
		}
		return false;
	}

	public void redo() {
		setChanged(true);
		if (m_undoneEdits.size() > 0) {
			CompoundEdit edit = m_undoneEdits.pop();
			edit.redo();
			m_edits.push(edit);
			if (m_undoneEdits.size() == 0) {
				m_debugger.setCanRedo(false);
			}
			m_debugger.setCanUndo(true);
		}
	}

	public boolean saveFile() {
		if (!hasChanged()) {
			return true;
		}
		if (m_file == null) {
			return saveFileAs();
		}
		return writeFile();
	}

	public boolean saveFileAs() {
		m_debugger.showReferenced(this);
		if (!selectFile(true)) {
			return false;
		}
		if (!writeFile()) {
			return false;
		}
		m_debugger.resetTitle(this);
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
				m_file = new File(fileChooser.getSelectedFile().getName() + ".RiffScript");
			} else {
				m_file = fileChooser.getSelectedFile();
			}
			return true;
		}
		return false;
	}

	public void setChanged(boolean changed) {
		m_hasChanged = changed;
		if (changed) {
			m_prefix = "* ";
		} else {
			m_prefix = "";
		}
		m_debugger.setChanged(changed);
	}

	public void setPrefix(String prefix) {
		m_prefix = prefix;
	}

	public void undo() {
		setChanged(true);
		m_edits.peek().end();
		if (m_edits.size() > 0) {
			CompoundEdit edit = m_edits.pop();
			if (edit.canUndo()) {
				edit.undo();
			}
			m_undoneEdits.push(edit);
			if (m_edits.size() == 0) {
				m_debugger.setCanUndo(false);
			}
			m_debugger.setCanRedo(true);
		}
	}

	@Override
	public void undoableEditHappened(UndoableEditEvent e) {
		if (m_edits.size() > 0) {
			if (System.currentTimeMillis() - m_lastEdit > 1000) {
				m_edits.peek().end();
			} else {
				m_edits.peek().addEdit(e.getEdit());
				return;
			}
		}
		if (e.getEdit().isSignificant()) {
			setChanged(true);
			m_debugger.setCanUndo(true);
			CompoundEdit edit = new CompoundEdit();
			edit.addEdit(e.getEdit());
			m_edits.add(edit);
			m_undoneEdits.clear();
		}
		m_lastEdit = System.currentTimeMillis();
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {

	}

	public boolean writeFile() {
		return writeFile(m_file);
	}

	public boolean writeFile(File file) {
		try {
			FileWriter writer = new FileWriter(file);
			writer.write(m_textArea.getText(), 0, m_textArea.getText().length());
			writer.close();
			setChanged(false);
			return true;
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(this, "File failed to save: " + file.getName(), "IOException", JOptionPane.ERROR_MESSAGE);
		}
		return false;
	}
}
