package com.dafrito.rfe.script.values;

import com.dafrito.rfe.script.exceptions.ScriptException;

public interface Returnable {
	public ScriptValue getReturnValue() throws ScriptException;

	// Returnable implementation
	public boolean shouldReturn();
}
