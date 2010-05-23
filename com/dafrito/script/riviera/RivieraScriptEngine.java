package com.dafrito.script.riviera;


import com.dafrito.script.AbstractScriptEngine;
import com.dafrito.script.DefaultScriptContext;
import com.dafrito.script.ScriptContext;

import java.util.concurrent.FutureTask;

import javax.tools.FileObject;


public class RivieraScriptEngine extends AbstractScriptEngine<FileObject> {

    private ScriptContext globalContext = new DefaultScriptContext();

    public FutureTask<?> getCompileTask(FileObject data) {
        // TODO Fix this bug.
        throw new UnsupportedOperationException("Not implemented");
    }

    public ScriptContext getScriptContext() {
        return this.globalContext;
    }

    public void reset() {
        this.globalContext = new DefaultScriptContext();
    }

}
