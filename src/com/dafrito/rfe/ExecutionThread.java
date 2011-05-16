/**
 * 
 */
package com.dafrito.rfe;

class ExecutionThread extends Thread {
	private Debug_Environment debugEnvironment;
	public static final String EXECUTIONTHREADSTRING = "Script Execution";
	private static int threadNum = 0;

	public ExecutionThread(Debug_Environment debugEnv) {
		super(EXECUTIONTHREADSTRING + " " + threadNum++);
		this.debugEnvironment = debugEnv;
	};

	@Override
	public void run() {
		Debugger.hitStopWatch(Thread.currentThread().getName());
		this.debugEnvironment.getEnvironment().execute();
		Debugger.hitStopWatch(Thread.currentThread().getName());
	}
}