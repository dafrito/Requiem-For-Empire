package com.dafrito.rfe.script.operations;

import com.dafrito.rfe.script.exceptions.ScriptException;
import com.dafrito.rfe.script.parsing.ScriptElement;
import com.dafrito.rfe.script.values.ScriptValue;

public interface ScriptExecutable {

	public ScriptValue execute() throws ScriptException;

	public ScriptElement getDebugReference();
}
