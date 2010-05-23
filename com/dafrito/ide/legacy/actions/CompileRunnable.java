package com.dafrito.ide.legacy.actions;

import com.dafrito.ide.legacy.CodeEnvironment;
import com.dafrito.ide.legacy.ScriptEditor;
import com.dafrito.logging.LegacyDebugger;
import com.dafrito.script.Parser;

import java.util.List;


public class CompileRunnable implements Runnable {
    private CodeEnvironment debugEnvironment;
    private Status status = Status.INITIALIZED;
    public static final String COMPILETHREADSTRING = "Compilation";

    public static enum Status {
        INITIALIZED,
        COMPILING,
        SUCCESSFUL,
        FAILED;
    }

    public CompileRunnable(CodeEnvironment debugEnv) {
        this.debugEnvironment = debugEnv;
    }

    public void run() {
        assert LegacyDebugger.open("Starting Compilation ... ");
        LegacyDebugger.hitStopWatch(Thread.currentThread().getName());
        this.debugEnvironment.getEnvironment().reset();
        Parser.clearPreparseLists();
        boolean quickflag = true;
        for (int i = 0; i < this.debugEnvironment.getScriptElements().size(); i++) {
            ScriptEditor element = this.debugEnvironment.getScriptElements().get(i);
            element.save();
            if (!element.compile()) {
                quickflag = false;
                this.debugEnvironment.setTitleAt(i + 1, element.getName());
            }
        }
        if (!quickflag) {
            this.debugEnvironment.setStatus("One or more files had errors during compilation.");
            this.status = Status.FAILED;
            return;
        }
        List<Exception> exceptions = Parser.parseElements(this.debugEnvironment.getEnvironment());
        if (!exceptions.isEmpty()) {
            LegacyDebugger.hitStopWatch(Thread.currentThread().getName());
            this.debugEnvironment.setStatus("One or more files had errors during compilation.");
            this.debugEnvironment.addExceptions(exceptions);
            assert LegacyDebugger.close("Compile thread complete.");
            return;
        }
        this.debugEnvironment.setStatus("All files compiled successfully.");
        LegacyDebugger.hitStopWatch(Thread.currentThread().getName());
        assert LegacyDebugger.addSnapNode("Compile successful", this.debugEnvironment.getEnvironment());
        this.status = Status.SUCCESSFUL;
        assert LegacyDebugger.close("Compile thread complete.");
    }

    public Status getStatus() {
        return this.status;
    }
}
