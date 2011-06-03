package com.dafrito.rfe.script.values;

import com.dafrito.rfe.script.exceptions.Exception_Nodeable;


public class ScriptValueType_ValueDeferrer extends ScriptValueType {
	private ScriptValue value;

	public ScriptValueType_ValueDeferrer(ScriptValue value) {
		super(value.getEnvironment());
		assert value != null;
		this.value = value;
	}

	@Override
	public ScriptValueType getBaseType() throws Exception_Nodeable {
		return this.value.getType();
	}
}
