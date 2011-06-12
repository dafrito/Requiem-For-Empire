package com.dafrito.rfe.script.values;

import com.dafrito.rfe.gui.debug.Debugger;
import com.dafrito.rfe.inspect.Nodeable;
import com.dafrito.rfe.script.ScriptConvertible;
import com.dafrito.rfe.script.ScriptEnvironment;
import com.dafrito.rfe.script.exceptions.ClassCastScriptException;
import com.dafrito.rfe.script.exceptions.ScriptException;
import com.dafrito.rfe.script.parsing.Referenced;

public class ScriptValue_String implements ScriptValue, ScriptConvertible<String>, Nodeable {
	private String value;
	private final ScriptEnvironment environment;

	public ScriptValue_String(ScriptEnvironment env, String string) {
		this.environment = env;
		this.value = string;
	}

	@Override
	public ScriptValue castToType(Referenced ref, ScriptValueType type) throws ScriptException {
		assert Debugger.addNode("Type Casting", "Casting (" + this.getType() + " to " + type + ")");
		if (this.getType().equals(type)) {
			return this;
		}
		throw new ClassCastScriptException(ref, this, type);
	}

	// Interface implementations
	@Override
	public String convert(ScriptEnvironment env) {
		return this.value;
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
	public ScriptValue getValue() throws ScriptException {
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
	public ScriptValue setValue(Referenced ref, ScriptValue value) throws ScriptException {
		assert Debugger.openNode("Value Assignments", "Setting String Value");
		assert Debugger.addSnapNode("Former value", this);
		this.value = ((ScriptValue_String) value.castToType(ref, this.getType())).getStringValue();
		assert Debugger.closeNode("New value", this);
		return this;
	}

	@Override
	public int valuesCompare(Referenced ref, ScriptValue rhs) throws ScriptException {
		return this.getStringValue().compareTo(((ScriptValue_String) rhs.castToType(ref, this.getType())).getStringValue());
	}

	@Override
	public boolean valuesEqual(Referenced ref, ScriptValue rhs) throws ScriptException {
		return this.getStringValue().equals(((ScriptValue_String) rhs.castToType(ref, this.getType())).getStringValue());
	}
}
