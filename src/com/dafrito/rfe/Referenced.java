package com.dafrito.rfe;

import com.dafrito.rfe.script.ScriptElement;
import com.dafrito.rfe.script.ScriptEnvironment;

public interface Referenced {
	public ScriptElement getDebugReference();

	public ScriptEnvironment getEnvironment();
}
