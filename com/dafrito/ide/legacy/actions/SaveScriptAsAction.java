package com.dafrito.ide.legacy.actions;

import com.dafrito.ide.legacy.CodeEnvironment;
import com.dafrito.ide.legacy.ScriptEditor;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;


public class SaveScriptAsAction extends CodeEnvironmentAction {

    public SaveScriptAsAction(CodeEnvironment env) {
        super(env);
        this.putValue(Action.NAME, "Save Script As ...");
        this.putValue(Action.SHORT_DESCRIPTION, "Saves the current script in a specified location.");
        this.putValue(Action.ACTION_COMMAND_KEY, "saveScript");
        this.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_A);
        this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
    }

    public void actionPerformed(ActionEvent e) {
        ScriptEditor editor = this.getEnvironment().getCurrentEditor();
        if (editor == null) {
            return;
        }
        editor.saveAs();
    }
}
