package com.dafrito.rfe.script.parsing;

import com.dafrito.rfe.script.ScriptEnvironment;


public interface Referenced {
	public ScriptElement getDebugReference();

	public ScriptEnvironment getEnvironment();
}
