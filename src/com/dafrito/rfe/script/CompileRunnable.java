/**
 * 
 */
package com.dafrito.rfe.script;

import java.util.List;

import com.dafrito.rfe.gui.script.ScriptEditor;
import com.dafrito.rfe.logging.Logs;
import com.dafrito.rfe.script.parsing.Parser;

public class CompileRunnable implements Runnable {
	public static final String COMPILETHREADSTRING = "Compilation";
	private static int threadNum = 0;

	private final ScriptEditor scriptEditor;
	private final boolean shouldExecute;
	private final ScriptEnvironment scriptEnvironment;

	private final String name;

	public CompileRunnable(ScriptEditor scriptEditor, ScriptEnvironment scriptEnv, boolean shouldExecute) {
		name = COMPILETHREADSTRING + " " + threadNum++;
		this.scriptEditor = scriptEditor;
		this.scriptEnvironment = scriptEnv;
		this.shouldExecute = shouldExecute;
	}

	public String getName() {
		return name;
	}

	@Override
	public void run() {
		try {
			Logs.hitStopWatch();
			this.scriptEnvironment.reset();
			Parser.clearPreparseLists();
			if (!scriptEditor.compileAll()) {
				this.scriptEditor.setStatus("One or more files had errors during compilation.");
				return;
			}
			List<Exception> exceptions = Parser.parseElements(this.scriptEnvironment);

			if (exceptions.isEmpty()) {
				this.scriptEditor.canExecute(true);
				this.scriptEditor.setStatus("All files compiled successfully.");
				assert Logs.addSnapNode("Compile successful", this.scriptEnvironment);
				if (this.shouldExecute) {
					ExecutionThread thread = new ExecutionThread(this.scriptEnvironment);
					thread.start();
				}
			} else {
				this.scriptEditor.setStatus("One or more files had errors during compilation.");
				this.scriptEditor.addExceptions(exceptions);
			}
		} finally {
			Logs.hitStopWatch();
		}
	}
}