package com.dafrito.rfe.script;

import com.dafrito.rfe.script.exceptions.Exception_Nodeable;


public interface ScriptExecutable {
	// ScriptExecutable implementation
	public ScriptValue execute() throws Exception_Nodeable;

	public ScriptElement getDebugReference();
}
