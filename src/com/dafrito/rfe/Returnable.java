package com.dafrito.rfe;
public interface Returnable {
	public ScriptValue_Abstract getReturnValue() throws Exception_Nodeable;

	// Returnable implementation
	public boolean shouldReturn();
}
