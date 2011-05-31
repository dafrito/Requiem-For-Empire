package com.dafrito.rfe.script;

import com.dafrito.rfe.debug.DebugString;
import com.dafrito.rfe.debug.Debugger;
import com.dafrito.rfe.inspect.Inspectable;
import com.dafrito.rfe.inspect.Nodeable;

@Inspectable
public class ScriptElement implements Nodeable, Referenced {
	private final ScriptEnvironment environment;
	private final int lineNumber, originalLineOffset, length;
	private final String original, filename;

	public ScriptElement() {
		this.environment = null;
		this.filename = "";
		this.original = "";
		this.lineNumber = -1;
		this.length = -1;
		this.originalLineOffset = 0;
	}

	public ScriptElement(Referenced element) {
		this(element.getDebugReference());
	}

	public ScriptElement(ScriptElement element) {
		this(element.getEnvironment(), element, 0, element.getLength());
	}

	public ScriptElement(ScriptElement element, int oLO, int length) {
		this(element.getEnvironment(), element, oLO, length);
	}

	public ScriptElement(ScriptEnvironment env) {
		this.environment = env;
		this.filename = "";
		this.original = "";
		this.lineNumber = -1;
		this.length = -1;
		this.originalLineOffset = 0;
	}

	public ScriptElement(ScriptEnvironment env, ScriptElement element, int oLO, int length) {
		this.environment = env;
		if (element != null) {
			this.filename = element.getFilename();
			this.lineNumber = element.getLineNumber();
			this.originalLineOffset = element.getOffset() + oLO;
			this.original = element.getOriginalString();
			this.length = length;
		} else {
			this.filename = "";
			this.original = "";
			this.lineNumber = -1;
			this.originalLineOffset = 0;
			this.length = -1;
		}
	}

	public ScriptElement(ScriptEnvironment env, String filename, int lineNumber, String original, int length) {
		this.environment = env;
		this.filename = filename;
		this.lineNumber = lineNumber;
		this.original = original;
		this.originalLineOffset = 0;
		this.length = length;
	}

	@Override
	public ScriptElement getDebugReference() {
		return this;
	}

	@Override
	public ScriptEnvironment getEnvironment() {
		assert this.environment != null : "Environment is null." + this;
		return this.environment;
	}

	@Inspectable
	public String getFilename() {
		return this.filename;
	}

	public String getFragment() {
		return this.original.substring(this.getOffset(), this.getLength());
	}

	public int getLength() {
		return this.length;
	}

	@Inspectable
	public int getLineNumber() {
		return this.lineNumber;
	}

	public int getOffset() {
		return this.originalLineOffset;
	}

	@Inspectable
	public String getOriginalString() {
		return this.original;
	}

	@Override
	public void nodificate() {
		if (this.getLineNumber() == -1) {
			assert Debugger.addNode("ScriptElement: No information provided");
			return;
		}
		assert Debugger.addSnapNode("ScriptElement (" + this.getFilename() + " @ " + this.getLineNumber() + ")", Debugger.getString(DebugString.ORIGINALSTRING) + this.getOriginalString() + "'");
	}
}
