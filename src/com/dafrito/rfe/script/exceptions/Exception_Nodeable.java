package com.dafrito.rfe.script.exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.dafrito.rfe.gui.debug.Debugger;
import com.dafrito.rfe.inspect.Nodeable;
import com.dafrito.rfe.script.Referenced;
import com.dafrito.rfe.script.ScriptElement;
import com.dafrito.rfe.script.ScriptEnvironment;

public abstract class Exception_Nodeable extends Exception implements Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8671870175447120460L;
	private int offset, lineNumber, length;
	private String line, filename;
	private final ScriptEnvironment environment;
	private final Object object;

	public Exception_Nodeable(Referenced ref) {
		this(ref.getDebugReference().getEnvironment(), ref.getDebugReference());
	}

	public Exception_Nodeable(ScriptEnvironment env) {
		this.environment = env;
		this.object = null;
		this.filename = null;
		this.line = null;
		this.offset = 0;
		this.lineNumber = 0;
		this.length = -1;
	}

	public Exception_Nodeable(ScriptEnvironment env, Object element) {
		this.environment = env;
		this.object = element;
		this.filename = null;
		this.line = null;
		this.offset = 0;
		this.lineNumber = -1;
		this.length = -1;
	}

	public Exception_Nodeable(ScriptEnvironment env, ScriptElement element) {
		this.object = null;
		this.environment = env;
		if (element != null) {
			this.filename = element.getFilename();
			this.lineNumber = element.getLineNumber();
			this.line = element.getOriginalString();
			this.offset = element.getOffset();
			this.length = element.getLength();
		} else {
			this.offset = 0;
			this.line = null;
			this.filename = null;
			this.lineNumber = -1;
			this.length = -1;
		}
	}

	public ScriptEnvironment getEnvironment() {
		return this.environment;
	}

	public void getExtendedInformation() {
	}

	public String getFilename() {
		return this.filename;
	}

	public String getFragment() {
		return this.getOriginalString().substring(this.getOffset(), this.getOffset() + this.getLength());
	}

	public int getLength() {
		return this.length;
	}

	public int getLineNumber() {
		return this.lineNumber;
	}

	@Override
	public String getMessage() {
		return "(Exception) " + this.getName();
	}

	public abstract String getName();

	public int getOffset() {
		return this.offset;
	}

	public String getOriginalString() {
		return this.line;
	}

	public boolean isAnonymous() {
		return this.filename == null;
	}

	@Override
	public void nodificate() {
		boolean debug = false;
		assert debug = true;
		if (!debug) {
			return;
		}
		Debugger.openNode("Exceptions and Errors", this.getName());
		if (this.object != null) {
			assert Debugger.addSnapNode("Reference", this.object);
		}
		this.getExtendedInformation();
		StringWriter writer;
		this.printStackTrace(new PrintWriter(writer = new StringWriter()));
		String[] messages = writer.toString().split("\n");
		boolean flag = false;
		int added = 0;
		for (int i = 0; i < messages.length; i++) {
			if (!flag && messages[i].trim().indexOf("at") == 0) {
				flag = true;
				assert Debugger.openNode("Call-stack");
			}
			if (flag && added == 5) {
				assert Debugger.openNode("Full Call-Stack");
			}
			if (messages[i].trim().indexOf("^") != 0) {
				assert Debugger.addNode(messages[i].trim());
			}
			if (flag) {
				added++;
			}
		}
		if (added > 5) {
			assert Debugger.closeNode();
		}
		if (flag) {
			assert Debugger.closeNode();
		}
		assert Debugger.closeNode();
	}

	@Override
	public String toString() {
		if (this.filename == null) {
			return this.getMessage();
		}
		while (this.line.indexOf("\t") == 0 || this.line.indexOf(" ") == 0) {
			this.line = this.line.substring(1);
			this.offset--;
			if (this.offset < 0) {
				this.offset = 0;
			}
		}
		String string = this.filename + ":" + this.lineNumber + ": " + this.getMessage() + "\n\t" + this.line;
		string += "\n\t";
		for (int i = 0; i < this.offset; i++) {
			string += " ";
		}
		string += "^";
		return string;
	}
}
