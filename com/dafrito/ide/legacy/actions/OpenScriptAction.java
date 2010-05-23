package com.dafrito.ide.legacy.actions;

import com.dafrito.gui.ExtensionFilter;
import com.dafrito.ide.legacy.CodeEnvironment;
import com.dafrito.ide.legacy.ScriptEditor;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import java.io.File;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;


public class OpenScriptAction extends CodeEnvironmentAction {

    public OpenScriptAction(CodeEnvironment env) {
        super(env);
        this.putValue(Action.NAME, "Open Script");
        this.putValue(Action.SHORT_DESCRIPTION, "Opens an existing script.");
        this.putValue(Action.ACTION_COMMAND_KEY, "openScript");
        this.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_O);
        this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
    }

    public void actionPerformed(ActionEvent e) {
        // TODO This complexity irritates me, but I can't think of a good way to fix it yet
        JFileChooser fileChooser = new JFileChooser(".");
        ExtensionFilter filter = new ExtensionFilter("RiffScripts");
        filter.addExtension("RiffScript");
        fileChooser.setFileFilter(filter);
        fileChooser.showOpenDialog(this.getEnvironment().getComponent());
        File file = fileChooser.getSelectedFile();
        if (file == null) {
            return;
        }
        this.getEnvironment().addScriptEditor(new ScriptEditor(this.getEnvironment(), file));
    }

}
