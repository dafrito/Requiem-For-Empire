/**
 * 
 */
package com.dafrito.rfe.script;

import java.util.List;

import com.dafrito.rfe.gui.debug.DebugEnvironment;
import com.dafrito.rfe.gui.debug.Debug_ScriptElement;
import com.dafrito.rfe.gui.debug.Debugger;
import com.dafrito.rfe.script.parsing.Parser;

public class CompileThread extends Thread {
	private final DebugEnvironment debugEnvironment;
	private boolean shouldExecute;
	private final ScriptEnvironment scriptEnvironment;
	public static final String COMPILETHREADSTRING = "Compilation";
	private static int threadNum = 0;

	public CompileThread(DebugEnvironment debugEnv, ScriptEnvironment scriptEnv, boolean shouldExecute) {
		super(COMPILETHREADSTRING + " " + threadNum++);
		this.debugEnvironment = debugEnv;
		this.scriptEnvironment = scriptEnv;
		this.shouldExecute = shouldExecute;
	}

	@Override
	public void run() {
		Debugger.hitStopWatch();
		try {
			this.scriptEnvironment.reset();
			Parser.clearPreparseLists();
			boolean quickflag = true;
			for (int i = 0; i < this.debugEnvironment.getScriptElements().size(); i++) {
				Debug_ScriptElement element = this.debugEnvironment.getScriptElements().get(i);
				element.saveFile();
				if (!element.compile(this.scriptEnvironment)) {
					quickflag = false;
					this.debugEnvironment.setTitleAt(i + 1, element.getName());
				}
			}
			if (!quickflag) {
				this.debugEnvironment.setStatus("One or more files had errors during compilation.");
				return;
			}
			List<Exception> exceptions = Parser.parseElements(this.scriptEnvironment);
			if (exceptions.isEmpty()) {
				this.debugEnvironment.canExecute(true);
				this.debugEnvironment.setStatus("All files compiled successfully.");
				Debugger.hitStopWatch();
				assert Debugger.addSnapNode("Compile successful", this.scriptEnvironment);
				if (this.shouldExecute) {
					ExecutionThread thread = new ExecutionThread(this.scriptEnvironment);
					thread.start();
				}
			} else {
				this.debugEnvironment.setStatus("One or more files had errors during compilation.");
				this.debugEnvironment.addExceptions(exceptions);
			}
		} finally {
			Debugger.hitStopWatch();
		}
	}
}