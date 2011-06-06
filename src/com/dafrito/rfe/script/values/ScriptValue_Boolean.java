package com.dafrito.rfe.script.values;

import com.dafrito.rfe.gui.debug.Debugger;
import com.dafrito.rfe.inspect.Nodeable;
import com.dafrito.rfe.script.ScriptConvertible;
import com.dafrito.rfe.script.ScriptEnvironment;
import com.dafrito.rfe.script.exceptions.Exception_Nodeable;
import com.dafrito.rfe.script.exceptions.Exception_Nodeable_ClassCast;
import com.dafrito.rfe.script.exceptions.Exception_Nodeable_IncomparableObjects;
import com.dafrito.rfe.script.parsing.Referenced;

public class ScriptValue_Boolean implements ScriptConvertible<Boolean>, ScriptValue, Nodeable {
	private boolean value;
	private final ScriptEnvironment environment;

	public ScriptValue_Boolean(ScriptEnvironment env, boolean value) {
		this.environment = env;
		this.value = value;
	}

	@Override
	public ScriptValue castToType(Referenced ref, ScriptValueType type) throws Exception_Nodeable {
		assert Debugger.addNode("Type Casting", "Casting (" + this.getType() + " to " + type + ")");
		if (this.getType().equals(type)) {
			return this;
		}
		throw new Exception_Nodeable_ClassCast(ref, this, type);
	}

	// Overloaded and miscellaneous functions
	@Override
	public Boolean convert(ScriptEnvironment env) {
		return Boolean.valueOf(this.value);
	}

	public boolean getBooleanValue() {
		return this.value;
	}

	// Abstract-value implementation
	@Override
	public ScriptEnvironment getEnvironment() {
		return this.environment;
	}

	@Override
	public ScriptValueType getType() {
		return ScriptValueType.BOOLEAN;
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
		assert Debugger.openNode("Boolean Script Value (" + this.getBooleanValue() + ")");
		assert Debugger.addNode("Reference: " + this);
		assert Debugger.closeNode();
	}

	@Override
	public ScriptValue setValue(Referenced ref, ScriptValue value) throws Exception_Nodeable {
		assert Debugger.openNode("Value Assignments", "Setting Boolean Value");
		assert Debugger.addSnapNode("Former value", this);
		this.value = ((ScriptValue_Boolean) value.castToType(ref, this.getType())).getBooleanValue();
		assert Debugger.closeNode("New value", this);
		return this;
	}

	@Override
	public int valuesCompare(Referenced ref, ScriptValue rhs) throws Exception_Nodeable {
		throw new Exception_Nodeable_IncomparableObjects(ref, this, rhs);
	}

	@Override
	public boolean valuesEqual(Referenced ref, ScriptValue rhs) throws Exception_Nodeable {
		return ((ScriptValue_Boolean) this.getValue()).getBooleanValue() == ((ScriptValue_Boolean) rhs.castToType(ref, this.getType())).getBooleanValue();
	}
}
