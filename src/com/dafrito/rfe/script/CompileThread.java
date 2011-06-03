/**
 * 
 */
package com.dafrito.rfe.script;

import java.util.Vector;

import com.dafrito.rfe.gui.debug.DebugEnvironment;
import com.dafrito.rfe.gui.debug.Debug_ScriptElement;
import com.dafrito.rfe.gui.debug.Debugger;
import com.dafrito.rfe.script.parsing.Parser;

public class CompileThread extends Thread {
	private DebugEnvironment debugEnvironment;
	private boolean shouldExecute;
	public static final String COMPILETHREADSTRING = "Compilation";
	private static int threadNum = 0;

	public CompileThread(DebugEnvironment debugEnv, boolean shouldExecute) {
		super(COMPILETHREADSTRING + " " + threadNum++);
		this.debugEnvironment = debugEnv;
		this.shouldExecute = shouldExecute;
	}

	@Override
	public void run() {
		Debugger.hitStopWatch();
		this.debugEnvironment.getEnvironment().reset();
		Parser.clearPreparseLists();
		boolean quickflag = true;
		for (int i = 0; i < this.debugEnvironment.getScriptElements().size(); i++) {
			Debug_ScriptElement element = this.debugEnvironment.getScriptElements().get(i);
			element.saveFile();
			if (!element.compile()) {
				quickflag = false;
				this.debugEnvironment.setTitleAt(i + 1, element.getName());
			}
		}
		if (!quickflag) {
			this.debugEnvironment.setStatus("One or more files had errors during compilation.");
			return;
		}
		Vector<Exception> exceptions = Parser.parseElements(this.debugEnvironment.getEnvironment());
		if (exceptions.size() == 0) {
			this.debugEnvironment.canExecute(true);
			this.debugEnvironment.setStatus("All files compiled successfully.");
			Debugger.hitStopWatch();
			assert Debugger.addSnapNode("Compile successful", this.debugEnvironment.getEnvironment());
			if (this.shouldExecute) {
				ExecutionThread thread = new ExecutionThread(this.debugEnvironment);
				thread.start();
			}
			//debugEnvironment.report();
			return;
		} else {
			Debugger.hitStopWatch();
			this.debugEnvironment.setStatus("One or more files had errors during compilation.");
			this.debugEnvironment.addExceptions(exceptions);
		}
	}
}