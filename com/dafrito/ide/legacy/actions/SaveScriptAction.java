package com.dafrito.ide.legacy.actions;

import com.dafrito.ide.legacy.CodeEnvironment;
import com.dafrito.ide.legacy.ScriptEditor;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;


public class SaveScriptAction extends CodeEnvironmentAction {

    public SaveScriptAction(CodeEnvironment env) {
        super(env);
        this.putValue(Action.NAME, "Save Script");
        this.putValue(Action.SHORT_DESCRIPTION, "Saves the current script.");
        this.putValue(Action.ACTION_COMMAND_KEY, "saveScript");
        this.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
        this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
    }

    public void actionPerformed(ActionEvent e) {
        ScriptEditor editor = this.getEnvironment().getCurrentEditor();
        if (editor == null) {
            return;
        }
        editor.save();
    }
}
