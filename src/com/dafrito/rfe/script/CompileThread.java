/**
 * 
 */
package com.dafrito.rfe.script;

import java.util.List;

import com.dafrito.rfe.gui.script.ScriptEditor;
import com.dafrito.rfe.gui.script.ScriptPanel;
import com.dafrito.rfe.logging.Logs;
import com.dafrito.rfe.script.parsing.Parser;

public class CompileThread extends Thread {
	private final ScriptEditor scriptEditor;
	private boolean shouldExecute;
	private final ScriptEnvironment scriptEnvironment;
	public static final String COMPILETHREADSTRING = "Compilation";
	private static int threadNum = 0;

	public CompileThread(ScriptEditor scriptEditor, ScriptEnvironment scriptEnv, boolean shouldExecute) {
		super(COMPILETHREADSTRING + " " + threadNum++);
		this.scriptEditor = scriptEditor;
		this.scriptEnvironment = scriptEnv;
		this.shouldExecute = shouldExecute;
	}

	@Override
	public void run() {
		Logs.hitStopWatch();
		try {
			this.scriptEnvironment.reset();
			Parser.clearPreparseLists();
			boolean compilationFailed = true;
			for (int i = 0; i < this.scriptEditor.getScriptElements().size(); i++) {
				ScriptPanel element = this.scriptEditor.getScriptElements().get(i);
				element.saveFile();
				if (!element.compile(this.scriptEnvironment)) {
					compilationFailed = false;
					this.scriptEditor.setTitleAt(i + 1, element.getName());
				}
			}
			if (!compilationFailed) {
				this.scriptEditor.setStatus("One or more files had errors during compilation.");
				return;
			}
			List<Exception> exceptions = Parser.parseElements(this.scriptEnvironment);
			if (exceptions.isEmpty()) {
				this.scriptEditor.canExecute(true);
				this.scriptEditor.setStatus("All files compiled successfully.");
				Logs.hitStopWatch();
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