package com.dafrito.rfe.script.values;

import com.dafrito.rfe.script.exceptions.ScriptException;


public class ScriptValueType_ValueDeferrer extends ScriptValueType {
	private ScriptValue value;

	public ScriptValueType_ValueDeferrer(ScriptValue value) {
		super(value.getEnvironment());
		assert value != null;
		this.value = value;
	}

	@Override
	public ScriptValueType getBaseType() throws ScriptException {
		return this.value.getType();
	}
}
