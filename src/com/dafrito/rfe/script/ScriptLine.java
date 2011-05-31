package com.dafrito.rfe.script;

import com.dafrito.rfe.debug.DebugString;
import com.dafrito.rfe.debug.Debugger;
import com.dafrito.rfe.inspect.Inspectable;
import com.dafrito.rfe.inspect.Nodeable;

@Inspectable
public class ScriptLine extends ScriptElement implements Nodeable {
	private String string;

	public ScriptLine(ScriptEnvironment env, String filename, int num, String string) {
		super(env, filename, num, string, string.length());
		this.string = string;
	}

	public ScriptLine(String string, ScriptLine otherLine, int oLO) {
		super(otherLine, oLO, string.length());
		this.string = string;
	}

	@Inspectable
	public String getString() {
		return this.string;
	}

	@Override
	public void nodificate() {
		assert Debugger.openNode(Debugger.getString(DebugString.SCRIPTLINE) + this.string);
		super.nodificate();
		assert Debugger.closeNode();
	}

	public void setString(String string) {
		this.string = string;
	}

	@Override
	public String toString() {
		return this.getFilename() + "@" + this.getLineNumber() + ": \"" + this.string + '"';
	}
}
