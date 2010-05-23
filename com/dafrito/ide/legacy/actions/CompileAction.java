package com.dafrito.ide.legacy.actions;

import com.dafrito.ide.legacy.CodeEnvironment;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;


public class CompileAction extends CodeEnvironmentAction {

    public CompileAction(CodeEnvironment env) {
        super(env);
        this.putValue(Action.NAME, "Compile");
        this.putValue(Action.SHORT_DESCRIPTION, "Compiles the current application.");
        this.putValue(Action.ACTION_COMMAND_KEY, "compile");
        this.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_C);
        this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
    }

    public void actionPerformed(ActionEvent e) {
        try {
            this.getEnvironment().compile();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

}
