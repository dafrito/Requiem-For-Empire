package com.dafrito.rfe;
public interface Returnable {
	public ScriptValue getReturnValue() throws Exception_Nodeable;

	// Returnable implementation
	public boolean shouldReturn();
}
