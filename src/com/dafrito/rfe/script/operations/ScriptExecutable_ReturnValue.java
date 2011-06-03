package com.dafrito.rfe.script.operations;

import com.dafrito.rfe.gui.debug.Debugger;
import com.dafrito.rfe.inspect.Nodeable;
import com.dafrito.rfe.script.exceptions.Exception_Nodeable;
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
	public ScriptValue execute() throws Exception_Nodeable {
		assert Debugger.openNode("Executing returnable script-value");
		ScriptValue value = this.value.getValue();
		assert Debugger.closeNode();
		return value;
	}

	@Override
	public ScriptValue getReturnValue() throws Exception_Nodeable {
		return this.value.getValue();
	}

	@Override
	public void nodificate() {
		assert Debugger.openNode("Returnable Script-Value");
		assert Debugger.addSnapNode("Returned Value", this.value);
		assert Debugger.closeNode();
	}

	// Returnabled implementation
	@Override
	public boolean shouldReturn() {
		return true;
	}
}
