package com.dafrito.rfe.script;

import com.dafrito.rfe.Debugger;
import com.dafrito.rfe.Referenced;
import com.dafrito.rfe.inspect.Nodeable;
import com.dafrito.rfe.script.exceptions.Exception_Nodeable;
import com.dafrito.rfe.script.exceptions.Exception_Nodeable_ClassCast;

public class ScriptValue_String implements ScriptValue, ScriptConvertible, Nodeable {
	private String value;
	private final ScriptEnvironment environment;

	public ScriptValue_String(ScriptEnvironment env, String string) {
		this.environment = env;
		this.value = string;
	}

	@Override
	public ScriptValue castToType(Referenced ref, ScriptValueType type) throws Exception_Nodeable {
		assert Debugger.addNode("Type Casting", "Casting (" + this.getType() + " to " + type + ")");
		if (this.getType().equals(type)) {
			return this;
		}
		throw new Exception_Nodeable_ClassCast(ref, this, type);
	}

	// Interface implementations
	@Override
	public Object convert() {
		return new String(this.value);
	}

	// Abstract-value implementation
	@Override
	public ScriptEnvironment getEnvironment() {
		return this.environment;
	}

	public String getStringValue() {
		return this.value;
	}

	@Override
	public ScriptValueType getType() {
		return ScriptValueType.STRING;
	}

	@Override
	public ScriptValue getValue() throws Exception_Nodeable {
		return this;
	}

	@Override
	public boolean isConvertibleTo(ScriptValueType type) {
		return this.getType().equals(type);
	}

	@Override
	public void nodificate() {
		assert Debugger.openNode("String Script-Value (" + this.getStringValue().length() + " character(s): " + this.getStringValue());
		assert Debugger.addSnapNode("Reference", super.toString());
		assert Debugger.closeNode();
	}

	@Override
	public ScriptValue setValue(Referenced ref, ScriptValue value) throws Exception_Nodeable {
		assert Debugger.openNode("Value Assignments", "Setting String Value");
		assert Debugger.addSnapNode("Former value", this);
		this.value = ((ScriptValue_String) value.castToType(ref, this.getType())).getStringValue();
		assert Debugger.closeNode("New value", this);
		return this;
	}

	@Override
	public int valuesCompare(Referenced ref, ScriptValue rhs) throws Exception_Nodeable {
		return this.getStringValue().compareTo(((ScriptValue_String) rhs.castToType(ref, this.getType())).getStringValue());
	}

	@Override
	public boolean valuesEqual(Referenced ref, ScriptValue rhs) throws Exception_Nodeable {
		return this.getStringValue().equals(((ScriptValue_String) rhs.castToType(ref, this.getType())).getStringValue());
	}
}
