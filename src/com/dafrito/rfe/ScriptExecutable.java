package com.dafrito.rfe;
public interface ScriptExecutable {
	// ScriptExecutable implementation
	public ScriptValue execute() throws Exception_Nodeable;

	public ScriptElement getDebugReference();
}
