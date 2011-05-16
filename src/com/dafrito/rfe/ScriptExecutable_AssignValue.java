package com.dafrito.rfe;

public class ScriptExecutable_AssignValue extends ScriptElement implements ScriptExecutable, ScriptValue, Nodeable {
	private ScriptValue variable;
	private ScriptValue value;

	public ScriptExecutable_AssignValue(Referenced ref, ScriptValue lhs, ScriptValue rhs) {
		super(ref);
		this.variable = lhs;
		this.value = rhs;
	}

	@Override
	public ScriptValue castToType(Referenced ref, ScriptValueType type) throws Exception_Nodeable {
		return this.variable.castToType(ref, type);
	}

	// ScriptExecutable implementation
	@Override
	public ScriptValue execute() throws Exception_Nodeable {
		assert Debugger.openNode("Value-Assignment Expressions", "Assigning Value");
		assert Debugger.addSnapNode("Left variable", this.variable);
		assert Debugger.openNode("Retrieving value");
		assert Debugger.addSnapNode("Current value", this.value);
		ScriptValue value = this.value.getValue();
		assert Debugger.closeNode();
		assert Debugger.addSnapNode("Right value", value);
		value = this.variable.setValue(this, value);
		assert Debugger.closeNode();
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
	public ScriptValue getValue() throws Exception_Nodeable {
		return this.execute();
	}

	@Override
	public boolean isConvertibleTo(ScriptValueType type) {
		return this.variable.isConvertibleTo(type);
	}

	// Nodeable implementation
	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Assignment Script Expression");
		assert super.nodificate();
		assert Debugger.addSnapNode("Left variable", this.variable);
		assert Debugger.addSnapNode("Right value", this.value);
		assert Debugger.closeNode();
		return true;
	}

	@Override
	public ScriptValue setValue(Referenced ref, ScriptValue value) throws Exception_Nodeable {
		assert Debugger.openNode("Value Assignments", "Setting assigment-expression's right-side value");
		assert Debugger.addSnapNode("Former value", this.value);
		this.value = value.castToType(this, this.getType());
		assert Debugger.addSnapNode("New value", this.value);
		ScriptValue returning = this.execute();
		assert Debugger.closeNode();
		return returning;
	}

	@Override
	public int valuesCompare(Referenced ref, ScriptValue rhs) throws Exception_Nodeable {
		return this.variable.valuesCompare(ref, rhs);
	}

	@Override
	public boolean valuesEqual(Referenced ref, ScriptValue rhs) throws Exception_Nodeable {
		return this.variable.valuesEqual(ref, rhs);
	}
}
