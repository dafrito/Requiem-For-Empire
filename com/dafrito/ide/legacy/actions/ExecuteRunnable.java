package com.dafrito.ide.legacy.actions;

import com.dafrito.ide.legacy.CodeEnvironment;
import com.dafrito.logging.LegacyDebugger;

public class ExecuteRunnable implements Runnable {

    private CodeEnvironment debugEnvironment;

    public ExecuteRunnable(CodeEnvironment debugEnv) {
        this.debugEnvironment = debugEnv;
    }

    public void run() {
        assert LegacyDebugger.open("Starting Execution ...");
        LegacyDebugger.hitStopWatch(Thread.currentThread().getName());
        this.debugEnvironment.getEnvironment().execute();
        LegacyDebugger.hitStopWatch(Thread.currentThread().getName());
        assert LegacyDebugger.close("Execution Thread complete.");
    }

}
