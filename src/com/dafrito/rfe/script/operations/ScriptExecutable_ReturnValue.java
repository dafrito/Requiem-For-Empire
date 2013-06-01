package com.dafrito.rfe.script.operations;

import com.dafrito.rfe.inspect.Nodeable;
import com.dafrito.rfe.logging.Logs;
import com.dafrito.rfe.script.exceptions.ScriptException;
import com.dafrito.rfe.script.parsing.Referenced;
import com.dafrito.rfe.script.parsing.ScriptElement;
import com.dafrito.rfe.script.values.Returnable;
import com.dafrito.rfe.script.values.ScriptValue;

public class ScriptExecutable_ReturnValue extends ScriptElement implements ScriptExecutable, Returnable, Nodeable {
	private ScriptValue value;

	public ScriptExecutable_ReturnValue(Referenced ref, ScriptValue value) {
		super(ref);
		this.value = value;
	}

	// ScriptExecutable implementation
	@Override
	public ScriptValue execute() throws ScriptException {
		assert Logs.openNode("Executing returnable script-value");
		ScriptValue value = this.value.getValue();
		assert Logs.closeNode();
		return value;
	}

	@Override
	public ScriptValue getReturnValue() throws ScriptException {
		return this.value.getValue();
	}

	@Override
	public void nodificate() {
		assert Logs.openNode("Returnable Script-Value");
		assert Logs.addSnapNode("Returned Value", this.value);
		assert Logs.closeNode();
	}

	// Returnabled implementation
	@Override
	public boolean shouldReturn() {
		return true;
	}
}
