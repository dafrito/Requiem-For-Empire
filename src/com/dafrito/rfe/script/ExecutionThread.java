/**
 * 
 */
package com.dafrito.rfe.script;

import com.dafrito.rfe.logging.Logs;

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
		Logs.hitStopWatch();
		try {
			this.scriptEnvironment.execute();
		} finally {
			Logs.hitStopWatch();
		}
	}
}