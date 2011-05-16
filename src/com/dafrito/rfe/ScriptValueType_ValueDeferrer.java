package com.dafrito.rfe;
public class ScriptValueType_ValueDeferrer extends ScriptValueType {
	private ScriptValue_Abstract value;

	public ScriptValueType_ValueDeferrer(ScriptValue_Abstract value) {
		super(value.getEnvironment());
		assert value != null;
		this.value = value;
	}

	@Override
	public ScriptValueType getBaseType() throws Exception_Nodeable {
		return this.value.getType();
	}
}
