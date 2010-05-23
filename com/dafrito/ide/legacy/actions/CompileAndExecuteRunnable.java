package com.dafrito.ide.legacy.actions;

import com.dafrito.ide.legacy.CodeEnvironment;

public class CompileAndExecuteRunnable implements Runnable {

    private CodeEnvironment debugEnvironment;

    public CompileAndExecuteRunnable(CodeEnvironment debugEnvironment) {
        this.debugEnvironment = debugEnvironment;
    }

    public void run() {
        CompileRunnable compile = new CompileRunnable(this.debugEnvironment);
        compile.run();
        if (compile.getStatus() == CompileRunnable.Status.SUCCESSFUL) {
            new ExecuteRunnable(this.debugEnvironment).run();
        }
    }

}
