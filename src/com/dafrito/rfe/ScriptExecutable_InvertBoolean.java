package com.dafrito.rfe;

public class ScriptExecutable_InvertBoolean extends ScriptElement implements ScriptExecutable, ScriptValue {
	private ScriptExecutable value;

	public ScriptExecutable_InvertBoolean(Referenced ref, ScriptExecutable value) {
		super(ref);
		this.value = value;
	}

	@Override
	public ScriptValue castToType(Referenced ref, ScriptValueType type) throws Exception_Nodeable {
		return this.getValue().castToType(ref, type);
	}

	// ScriptExecutable implementation
	@Override
	public ScriptValue execute() throws Exception_Nodeable {
		return new ScriptValue_Boolean(this.getEnvironment(), !((ScriptValue_Boolean) this.value.execute()).getBooleanValue());
	}

	// ScriptValue_Abstract implementation
	@Override
	public ScriptValueType getType() {
		return ScriptValueType.BOOLEAN;
	}

	@Override
	public ScriptValue getValue() throws Exception_Nodeable {
		return this.execute();
	}

	@Override
	public boolean isConvertibleTo(ScriptValueType type) {
		return this.getType().equals(type);
	}

	// Nodeable implementation
	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Boolean Inverter");
		assert super.nodificate();
		assert Debugger.addSnapNode("Value", this.value);
		assert Debugger.closeNode();
		return true;
	}

	@Override
	public ScriptValue setValue(Referenced ref, ScriptValue value) throws Exception_Nodeable {
		return this.getValue().setValue(ref, value);
	}

	@Override
	public int valuesCompare(Referenced ref, ScriptValue rhs) throws Exception_Nodeable {
		return this.getValue().valuesCompare(ref, rhs);
	}

	@Override
	public boolean valuesEqual(Referenced ref, ScriptValue rhs) throws Exception_Nodeable {
		return this.getValue().valuesEqual(ref, rhs);
	}
}
