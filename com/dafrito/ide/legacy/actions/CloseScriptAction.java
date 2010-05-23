package com.dafrito.ide.legacy.actions;

import com.dafrito.ide.legacy.CodeEnvironment;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;


public class CloseScriptAction extends CodeEnvironmentAction {

    public CloseScriptAction(CodeEnvironment env) {
        super(env);
        this.putValue(Action.NAME, "Close Script");
        this.putValue(Action.SHORT_DESCRIPTION, "Closes the current script.");
        this.putValue(Action.ACTION_COMMAND_KEY, "closeScript");
        this.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_C);
        this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
    }

    public void actionPerformed(ActionEvent e) {
        // TODO implement close
        //this.getEnvironment().closeEditor(this.getEnvironment().getCurrentEditor());
    }

}
