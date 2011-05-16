package com.dafrito.rfe;

public class ScriptValue_Faux implements Nodeable, ScriptValue_Abstract {
	private final ScriptEnvironment environment;
	private final ScriptValueType type;

	public ScriptValue_Faux(ScriptEnvironment env, ScriptValueType type) {
		this.environment = env;
		this.type = type;
	}

	@Override
	public ScriptValue_Abstract castToType(Referenced ref, ScriptValueType type) throws Exception_Nodeable {
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
	public ScriptValue_Abstract getValue() throws Exception_Nodeable {
		throw new Exception_InternalError(this.getEnvironment(), "Invalid call in ScriptValue_Faux");
	}

	@Override
	public boolean isConvertibleTo(ScriptValueType type) {
		return ScriptValueType.isConvertibleTo(this.getEnvironment(), this.getType(), type);
	}

	@Override
	public boolean nodificate() {
		assert Debugger.addNode("Faux Script-Value (" + this.getType() + ")");
		return true;
	}

	protected void setType(ScriptValueType type) {
		throw new Exception_InternalError(this.getEnvironment(), "Invalid call in ScriptValue_Faux");
	}

	@Override
	public ScriptValue_Abstract setValue(Referenced ref, ScriptValue_Abstract value) throws Exception_Nodeable {
		throw new Exception_InternalError(this.getEnvironment(), "Invalid call in ScriptValue_Faux");
	}

	@Override
	public int valuesCompare(Referenced ref, ScriptValue_Abstract rhs) throws Exception_Nodeable {
		throw new Exception_InternalError(this.getEnvironment(), "Invalid call in ScriptValue_Faux");
	}

	@Override
	public boolean valuesEqual(Referenced ref, ScriptValue_Abstract rhs) throws Exception_Nodeable {
		throw new Exception_InternalError(this.getEnvironment(), "Invalid call in ScriptValue_Faux");
	}
}
