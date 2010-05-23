package com.dafrito.ide.legacy;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
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

import com.dafrito.debug.Exception_InternalError;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.gui.ExtensionFilter;
import com.dafrito.script.Parser;
import com.dafrito.script.ScriptLine;

public class ScriptEditor extends JPanel implements UndoableEditListener, ListSelectionListener,
        ComponentListener {

    private File file;
    private boolean isDirty, hasFile;
    private static int fileNumber = 1;
    private ScriptEditorContext context;
    private JTextArea textArea;
    private Stack<CompoundEdit> edits = new Stack<CompoundEdit>();
    private Stack<CompoundEdit> undoneEdits = new Stack<CompoundEdit>();
    private long lastEdit;
    private JList errors;
    private JSplitPane splitPane;
    private int width;
    private Vector<Exception> exceptions;
    private Vector<String> displayedExceptions;
    private String prefix = "";

    public ScriptEditor(ScriptEditorContext debugger, File file) {
        this.context = debugger;
        this.hasFile = openFile(file);
    }

    public void componentResized(ComponentEvent x) {
        double location = ((double)this.splitPane.getDividerLocation()) / (double)this.width;
        if(location > 1) {
            location = 1;
        }
        this.splitPane.setDividerLocation((int)(getWidth() * location));
        this.width = getWidth();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
    }

    // TODO: Debug_ScriptEvent should use adapters

    public void componentHidden(ComponentEvent x) {
        // Ignore this event
    }

    public void componentShown(ComponentEvent x) {
        // Ignore this event
    }

    public void componentMoved(ComponentEvent x) {
        // Ignore this event
    }

    public void mouseClicked(MouseEvent e) {
        if(e.getClickCount() != 2) {
            return;
        }
        if(this.errors.getSelectedValue() == null) {
            return;
        }
        Exception rawEx = this.exceptions.get(this.errors.getSelectedIndex());
        if(!(rawEx instanceof Exception_Nodeable)) {
            return;
        }
        Exception_Nodeable ex = (Exception_Nodeable)rawEx;
        String[] array = this.textArea.getText().split("\n");
        int offset = 0;
        this.textArea.requestFocus();
        for(int i = 0; i < ex.getLineNumber(); i++) {
            if(array.length - 1 == i) {
                this.textArea.setCaretPosition(offset);
                return;
            }
            offset += array[i].length() + 1;
            if(i == ex.getLineNumber() - 1) {
                i++;
                if(array[i].length() < ex.getOffset()) {
                    this.textArea.setCaretPosition(offset + array[i].length());
                    return;
                }
                if(array[i].substring(ex.getOffset()).length() < (ex.getLength() - ex.getOffset())) {
                    this.textArea.setCaretPosition(offset + ex.getOffset());
                    this.textArea.select(offset + ex.getOffset(), offset + array[i].length() - ex.getOffset());
                    return;
                }
                break;
            }
        }
        this.textArea.select(offset + ex.getOffset() + 1, 1 + offset + ex.getOffset() + ex.getLength());
    }

    public void undoableEditHappened(UndoableEditEvent e) {
        if(this.edits.size() > 0) {
            if(System.currentTimeMillis() - this.lastEdit > 1000) {
                this.edits.peek().end();
            } else {
                this.edits.peek().addEdit(e.getEdit());
                return;
            }
        }
        if(e.getEdit().isSignificant()) {
            setChanged(true);
            this.context.setCanRedo(true);
            CompoundEdit edit = new CompoundEdit();
            edit.addEdit(e.getEdit());
            this.edits.add(edit);
            this.undoneEdits.clear();
        }
        this.lastEdit = System.currentTimeMillis();
    }

    public boolean canUndo() {
        return this.edits.size() > 0;
    }

    public boolean canRedo() {
        return this.undoneEdits.size() > 0;
    }

    public boolean isDirty() {
        return this.isDirty;
    }

    public void undo() {
        setChanged(true);
        this.edits.peek().end();
        if(this.edits.size() > 0) {
            CompoundEdit edit = this.edits.pop();
            if(edit.canUndo()) {
                edit.undo();
            }
            this.undoneEdits.push(edit);
            if(this.edits.size() == 0) {
                this.context.setCanRedo(false);
            }
            this.context.setCanRedo(true);
        }
    }

    public void redo() {
        setChanged(true);
        if(this.undoneEdits.size() > 0) {
            CompoundEdit edit = this.undoneEdits.pop();
            edit.redo();
            this.edits.push(edit);
            if(this.undoneEdits.size() == 0) {
                this.context.setCanRedo(false);
            }
            this.context.setCanRedo(true);
        }
    }

    public void setChanged(boolean changed) {
        this.isDirty = changed;
        if(changed) {
            this.prefix = "* ";
        } else {
            this.prefix = "";
        }
        this.context.setChanged(changed);
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String getName() {
        return this.prefix + getFilename();
    }

    public String getFilename() {
        if(this.file != null) {
            return this.file.getName();
        }
        return "Untitled " + fileNumber++;
    }

    public void addException(Exception exception) {
        this.exceptions.add(exception);
        this.prefix = "X ";
        this.errors.setListData(this.displayedExceptions);
        this.errors.setBorder(BorderFactory.createTitledBorder(getFilename()
            + "("
            + this.exceptions.size()
            + " error(s))"));
        if(exception instanceof Exception_Nodeable) {
            this.displayedExceptions.add(((Exception_Nodeable)exception).getName());
        } else {
            this.displayedExceptions.add(exception.getMessage());
        }
    }

    public boolean compile() {
        String text = this.textArea.getText();
        String[] stringArray = text.split("\n");
        java.util.List<Object> strings = new LinkedList<Object>();
        for(int i = 0; i < stringArray.length; i++) {
            strings.add(new ScriptLine(this.context.getEnvironment(), getFilename(), i + 1, stringArray[i]));
        }
        this.width = getWidth();
        this.splitPane.setRightComponent(new JScrollPane(this.errors = new JList()));
        this.splitPane.setDividerLocation(getWidth() - 200);
        this.errors.addListSelectionListener(this);
        this.exceptions = Parser.preparseFile(this.context.getEnvironment(), getFilename(), strings);
        this.displayedExceptions = new Vector<String>();
        if(this.exceptions.size() == 0) {
            this.errors.setBorder(BorderFactory.createTitledBorder("Compiled Successfully"));
            this.prefix = "";
            return true;
        }
        for(Exception ex : this.exceptions) {
            if(ex instanceof Exception_Nodeable) {
                this.displayedExceptions.add(((Exception_Nodeable)ex).getName());
            } else if(ex instanceof Exception_InternalError) {
                this.displayedExceptions.add(((Exception_InternalError)ex).getName());
            } else {
                this.displayedExceptions.add(ex.getMessage());
            }
        }
        this.errors.setListData(this.displayedExceptions);
        this.errors.setBorder(BorderFactory.createTitledBorder(getName() + "(" + this.exceptions.size() + " error(s))"));
        this.prefix = "X ";
        return false;
    }

    public boolean openFile() {
        return openFile(this.file);
    }

    public boolean openFile(File fileToOpen) {
        setLayout(new BorderLayout());
        this.splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        this.splitPane.add(new JScrollPane(this.textArea = new JTextArea()));
        this.add(this.splitPane, BorderLayout.CENTER);
        this.textArea.setFont(new Font("Courier", Font.PLAIN, 12));
        if(fileToOpen != null) {
            this.file = fileToOpen;
            if(!readFile()) {
                return false;
            }
        }
        this.textArea.getDocument().addUndoableEditListener(this);
        addComponentListener(this);
        setVisible(true);
        return true;
    }

    public void valueChanged(ListSelectionEvent e) {
        // Ignore this event
    }

    public boolean hasFile() {
        return this.hasFile;
    }

    private File getDefaultFile() {
        return this.file;
    }

    public boolean save() {
        if(!isDirty()) {
            return true;
        }
        if(this.file == null) {
            return this.saveAs();
        }
        return this.writeFile(this.getDefaultFile());
    }

    public boolean saveAs() {
        this.context.showScriptEditor(this);
        if(!selectFile(true)) {
            return false;
        }
        if(!this.writeFile(this.getDefaultFile())) {
            return false;
        }
        this.context.resetTitle(this);
        return true;
    }

    public boolean selectFile(boolean isSaving) {
        JFileChooser fileChooser = new JFileChooser(".");
        ExtensionFilter filter = new ExtensionFilter("RiffScripts");
        filter.addExtension("RiffScript");
        fileChooser.setFileFilter(filter);
        int choice = 0;
        if(isSaving) {
            choice = fileChooser.showSaveDialog(this);
        } else {
            choice = fileChooser.showOpenDialog(this);
        }
        if(choice == JFileChooser.APPROVE_OPTION) {
            if(fileChooser.getSelectedFile().getName().indexOf(".") == -1) {
                this.file = new File(fileChooser.getSelectedFile().getName() + ".RiffScript");
            } else {
                this.file = fileChooser.getSelectedFile();
            }
            return true;
        }
        return false;
    }

    public boolean readFile() {
        return readFile(this.file);
    }

    public boolean readFile(File fileToRead) {
        try {
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(fileToRead));
            byte[] b = new byte[in.available()];
            in.read(b, 0, b.length);
            this.textArea.setText(new String(b, 0, b.length));
            in.close();
            return true;
        } catch(IOException ex) {
            JOptionPane.showMessageDialog(
                this,
                "File failed to open: " + fileToRead.getName(),
                "IOException",
                JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    public boolean writeFile(File fileToWrite) {
        try {
            FileWriter writer = new FileWriter(fileToWrite);
            writer.write(this.textArea.getText(), 0, this.textArea.getText().length());
            writer.close();
            setChanged(false);
            return true;
        } catch(IOException ex) {
            JOptionPane.showMessageDialog(
                this,
                "File failed to save: " + fileToWrite.getName(),
                "IOException",
                JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
    
    public boolean closeFile() {
        this.context.showScriptEditor(this);
        if(!isDirty()) {
            return true;
        }
        int option = JOptionPane.showConfirmDialog(
            this.context.getComponent(),
            "This file has unsaved changes. Save?",
            getName(),
            JOptionPane.YES_NO_CANCEL_OPTION);
        if(option == JOptionPane.NO_OPTION) {
            return true;
        }
        if(option == JOptionPane.CANCEL_OPTION) {
            return false;
        }
        return save();
    }
}
