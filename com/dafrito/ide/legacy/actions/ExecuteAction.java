package com.dafrito.ide.legacy.actions;

import com.dafrito.ide.legacy.CodeEnvironment;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;


public class ExecuteAction extends CodeEnvironmentAction {

    public ExecuteAction(CodeEnvironment env) {
        super(env);
        this.putValue(Action.NAME, "Execute");
        this.putValue(Action.SHORT_DESCRIPTION, "Executes the current application.");
        this.putValue(Action.ACTION_COMMAND_KEY, "execute");
        this.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_F8);
        this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_X, 0));
    }

    public void actionPerformed(ActionEvent e) {
        this.getEnvironment().execute();
    }

}
