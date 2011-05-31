package com.dafrito.rfe.script;

import com.dafrito.rfe.debug.Debugger;
import com.dafrito.rfe.inspect.Nodeable;
import com.dafrito.rfe.script.exceptions.Exception_InternalError;
import com.dafrito.rfe.script.exceptions.Exception_Nodeable;

public class ScriptValue_Faux implements Nodeable, ScriptValue {
	private final ScriptEnvironment environment;
	private final ScriptValueType type;

	public ScriptValue_Faux(ScriptEnvironment env, ScriptValueType type) {
		this.environment = env;
		this.type = type;
	}

	@Override
	public ScriptValue castToType(Referenced ref, ScriptValueType type) throws Exception_Nodeable {
		throw new Exception_InternalError(this.getEnvironment(), "Invalid call in ScriptValue_Faux");
	}

	@Override
	public ScriptEnvironment getEnvironment() {
		return this.environment;
	}

	@Override
	public ScriptValueType getType() {
		return this.type;
	}

	@Override
	public ScriptValue getValue() throws Exception_Nodeable {
		throw new Exception_InternalError(this.getEnvironment(), "Invalid call in ScriptValue_Faux");
	}

	@Override
	public boolean isConvertibleTo(ScriptValueType type) {
		return ScriptValueType.isConvertibleTo(this.getEnvironment(), this.getType(), type);
	}

	@Override
	public void nodificate() {
		assert Debugger.addNode("Faux Script-Value (" + this.getType() + ")");
	}

	protected void setType(ScriptValueType type) {
		throw new Exception_InternalError(this.getEnvironment(), "Invalid call in ScriptValue_Faux");
	}

	@Override
	public ScriptValue setValue(Referenced ref, ScriptValue value) throws Exception_Nodeable {
		throw new Exception_InternalError(this.getEnvironment(), "Invalid call in ScriptValue_Faux");
	}

	@Override
	public int valuesCompare(Referenced ref, ScriptValue rhs) throws Exception_Nodeable {
		throw new Exception_InternalError(this.getEnvironment(), "Invalid call in ScriptValue_Faux");
	}

	@Override
	public boolean valuesEqual(Referenced ref, ScriptValue rhs) throws Exception_Nodeable {
		throw new Exception_InternalError(this.getEnvironment(), "Invalid call in ScriptValue_Faux");
	}
}
