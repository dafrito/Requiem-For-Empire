/**
 * 
 */
package com.dafrito.rfe.script;

import com.dafrito.rfe.debug.DebugEnvironment;
import com.dafrito.rfe.debug.Debugger;

public class ExecutionThread extends Thread {
	private DebugEnvironment debugEnvironment;
	public static final String EXECUTIONTHREADSTRING = "Script Execution";
	private static int threadNum = 0;

	public ExecutionThread(DebugEnvironment debugEnv) {
		super(EXECUTIONTHREADSTRING + " " + threadNum++);
		this.debugEnvironment = debugEnv;
	};

	@Override
	public void run() {
		Debugger.hitStopWatch();
		this.debugEnvironment.getEnvironment().execute();
		Debugger.hitStopWatch();
	}
}