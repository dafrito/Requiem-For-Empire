package com.dafrito.rfe.script.operations;

import com.dafrito.rfe.inspect.Nodeable;
import com.dafrito.rfe.logging.Logs;
import com.dafrito.rfe.script.exceptions.ScriptException;
import com.dafrito.rfe.script.parsing.Referenced;
import com.dafrito.rfe.script.parsing.ScriptElement;
import com.dafrito.rfe.script.values.ScriptValue;
import com.dafrito.rfe.script.values.ScriptValueType;

public class ScriptExecutable_AssignValue extends ScriptElement implements ScriptExecutable, ScriptValue, Nodeable {
	private ScriptValue variable;
	private ScriptValue value;

	public ScriptExecutable_AssignValue(Referenced ref, ScriptValue lhs, ScriptValue rhs) {
		super(ref);
		this.variable = lhs;
		this.value = rhs;
	}

	@Override
	public ScriptValue castToType(Referenced ref, ScriptValueType type) throws ScriptException {
		return this.variable.castToType(ref, type);
	}

	// ScriptExecutable implementation
	@Override
	public ScriptValue execute() throws ScriptException {
		assert Logs.openNode("Value-Assignment Expressions", "Assigning Value");
		assert Logs.addSnapNode("Left variable", this.variable);
		assert Logs.openNode("Retrieving value");
		assert Logs.addSnapNode("Current value", this.value);
		ScriptValue value = this.value.getValue();
		assert Logs.closeNode();
		assert Logs.addSnapNode("Right value", value);
		value = this.variable.setValue(this, value);
		assert Logs.closeNode();
		return value;
	}

	public ScriptValue getLeft() {
		return this.variable;
	}

	// ScriptValue_Abstract implementation
	@Override
	public ScriptValueType getType() {
		return this.variable.getType();
	}

	@Override
	public ScriptValue getValue() throws ScriptException {
		return this.execute();
	}

	@Override
	public boolean isConvertibleTo(ScriptValueType type) {
		return this.variable.isConvertibleTo(type);
	}

	// Nodeable implementation
	@Override
	public void nodificate() {
		assert Logs.openNode("Assignment Script Expression");
		assert Logs.addSnapNode("Left variable", this.variable);
		assert Logs.addSnapNode("Right value", this.value);
		assert Logs.closeNode();
	}

	@Override
	public ScriptValue setValue(Referenced ref, ScriptValue value) throws ScriptException {
		assert Logs.openNode("Value Assignments", "Setting assigment-expression's right-side value");
		assert Logs.addSnapNode("Former value", this.value);
		this.value = value.castToType(this, this.getType());
		assert Logs.addSnapNode("New value", this.value);
		ScriptValue returning = this.execute();
		assert Logs.closeNode();
		return returning;
	}

	@Override
	public int valuesCompare(Referenced ref, ScriptValue rhs) throws ScriptException {
		return this.variable.valuesCompare(ref, rhs);
	}

	@Override
	public boolean valuesEqual(Referenced ref, ScriptValue rhs) throws ScriptException {
		return this.variable.valuesEqual(ref, rhs);
	}
}
