package com.dafrito.ide.legacy.actions;

import com.dafrito.ide.legacy.CodeEnvironment;
import com.dafrito.ide.legacy.ScriptEditor;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;


public class NewScriptAction extends CodeEnvironmentAction {

    public NewScriptAction(CodeEnvironment env) {
        super(env);
        this.putValue(Action.NAME, "New Script");
        this.putValue(Action.SHORT_DESCRIPTION, "Creates a new script.");
        this.putValue(Action.ACTION_COMMAND_KEY, "createScript");
        this.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_N);
        this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
    }

    public void actionPerformed(ActionEvent e) {
        this.getEnvironment().addScriptEditor(new ScriptEditor(this.getEnvironment(), null));
    }

}
