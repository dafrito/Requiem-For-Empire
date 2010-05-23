package com.dafrito.ide.legacy.actions;

import javax.swing.AbstractAction;

import com.dafrito.ide.legacy.CodeEnvironment;


public abstract class CodeEnvironmentAction extends AbstractAction {
    
    private final CodeEnvironment environment;
    
    public CodeEnvironmentAction(CodeEnvironment env) {
        this.environment = env;
    }

    public CodeEnvironment getEnvironment() {
        return this.environment;
    }

}
