package com.dafrito.script.riffscript;


import com.dafrito.script.AbstractScriptEngine;
import com.dafrito.script.Parser;
import com.dafrito.script.ScriptContext;
import com.dafrito.script.ScriptEnvironment;

import java.io.Reader;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;


public class LegacyRiffScriptEngine extends AbstractScriptEngine<Reader> {

    protected ScriptEnvironment environment = new ScriptEnvironment();

    public FutureTask<?> getCompileTask(final Reader reader) {
        return new FutureTask<Object>(new Callable<Object>() {

                public Object call() throws Exception {
                    LegacyRiffScriptEngine.this.reset();
                    Parser.compile(LegacyRiffScriptEngine.this.environment, reader);
                    LegacyRiffScriptEngine.this.environment.execute();
                    return null;
                }

            });
    }

    public ScriptContext getScriptContext() {
        // TODO Auto-generated method stub
        return null;
    }

    public void reset() {
        this.environment.reset();
    }

}
