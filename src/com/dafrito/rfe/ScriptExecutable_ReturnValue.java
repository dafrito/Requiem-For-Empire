package com.dafrito.rfe;

public class ScriptExecutable_ReturnValue extends ScriptElement implements ScriptExecutable, Returnable, Nodeable {
	private ScriptValue_Abstract value;

	public ScriptExecutable_ReturnValue(Referenced ref, ScriptValue_Abstract value) {
		super(ref);
		this.value = value;
	}

	// ScriptExecutable implementation
	@Override
	public ScriptValue_Abstract execute() throws Exception_Nodeable {
		assert Debugger.openNode("Executing returnable script-value");
		ScriptValue_Abstract value = this.value.getValue();
		assert Debugger.closeNode();
		return value;
	}

	@Override
	public ScriptValue_Abstract getReturnValue() throws Exception_Nodeable {
		return this.value.getValue();
	}

	// Nodeable implementation
	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Returnable Script-Value");
		assert Debugger.addSnapNode("Returned Value", this.value);
		assert Debugger.closeNode();
		return true;
	}

	// Returnabled implementation
	@Override
	public boolean shouldReturn() {
		return true;
	}
}
