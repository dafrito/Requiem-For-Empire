/**
 * 
 */
package com.dafrito.rfe.script;

import com.dafrito.rfe.gui.debug.Debugger;

public class ExecutionThread extends Thread {
	private ScriptEnvironment scriptEnvironment;
	public static final String EXECUTIONTHREADSTRING = "Script Execution";
	private static int threadNum = 0;

	public ExecutionThread(ScriptEnvironment env) {
		super(EXECUTIONTHREADSTRING + " " + threadNum++);
		this.scriptEnvironment = env;
	};

	@Override
	public void run() {
		Debugger.hitStopWatch();
		try {
			this.scriptEnvironment.execute();
		} finally {
			Debugger.hitStopWatch();
		}
	}
}