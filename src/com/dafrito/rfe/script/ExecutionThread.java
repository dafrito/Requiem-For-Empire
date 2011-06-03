/**
 * 
 */
package com.dafrito.rfe.script;

import com.dafrito.rfe.gui.debug.DebugEnvironment;
import com.dafrito.rfe.gui.debug.Debugger;

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
		try {
			this.debugEnvironment.getEnvironment().execute();
		} finally {
			Debugger.hitStopWatch();
		}
	}
}