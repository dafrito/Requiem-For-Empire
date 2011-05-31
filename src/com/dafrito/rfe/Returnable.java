package com.dafrito.rfe;

import com.dafrito.rfe.script.ScriptValue;
import com.dafrito.rfe.script.exceptions.Exception_Nodeable;

public interface Returnable {
	public ScriptValue getReturnValue() throws Exception_Nodeable;

	// Returnable implementation
	public boolean shouldReturn();
}
