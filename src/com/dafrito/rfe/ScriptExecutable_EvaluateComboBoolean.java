package com.dafrito.rfe;

import com.dafrito.rfe.inspect.Nodeable;

public class ScriptExecutable_EvaluateComboBoolean extends ScriptElement implements ScriptValue, ScriptExecutable, Nodeable {
	private ScriptValue lhs, rhs;
	private ScriptOperatorType operator;

	public ScriptExecutable_EvaluateComboBoolean(Referenced ref, ScriptValue lhs, ScriptValue rhs, ScriptOperatorType operator) {
		super(ref);
		this.lhs = lhs;
		this.rhs = rhs;
		this.operator = operator;
	}

	@Override
	public ScriptValue castToType(Referenced ref, ScriptValueType type) throws Exception_Nodeable {
		return this.getValue().castToType(ref, type);
	}

	// ScriptExecutable implementation
	@Override
	public ScriptValue execute() throws Exception_Nodeable {
		assert Debugger.openNode("Combo-Boolean Evaluations", "Evaluating Combo-Boolean Expression (" + this.operator + ")");
		assert Debugger.addNode(this);
		if (this.lhs.isConvertibleTo(ScriptValueType.BOOLEAN)) {
			throw new Exception_Nodeable_ClassCast(this, this.lhs, ScriptValueType.BOOLEAN);
		}
		if (this.rhs.isConvertibleTo(ScriptValueType.BOOLEAN)) {
			throw new Exception_Nodeable_ClassCast(this, this.rhs, ScriptValueType.BOOLEAN);
		}
		ScriptValue_Boolean lhs = (ScriptValue_Boolean) this.lhs.getValue();
		ScriptValue_Boolean rhs = (ScriptValue_Boolean) this.rhs.getValue();
		switch (this.operator) {
		case AND:
			return new ScriptValue_Boolean(this.getEnvironment(), (lhs.getBooleanValue() && rhs.getBooleanValue()));
		case OR:
			return new ScriptValue_Boolean(this.getEnvironment(), (lhs.getBooleanValue() || rhs.getBooleanValue()));
		}
		throw new Exception_InternalError("Invalid default");
	}

	// ScriptValue_Abstract implementation
	@Override
	public ScriptValueType getType() {
		return this.lhs.getType();
	}

	@Override
	public ScriptValue getValue() throws Exception_Nodeable {
		return this.execute();
	}

	@Override
	public boolean isConvertibleTo(ScriptValueType type) {
		return ScriptValueType.isConvertibleTo(this.getEnvironment(), this.getType(), type);
	}

	@Override
	public void nodificate() {
		assert Debugger.openNode("Combo Boolean Expression");
		super.nodificate();
		assert Debugger.addSnapNode("Left side", this.lhs);
		assert Debugger.addSnapNode("Right side", this.rhs);
		assert Debugger.closeNode();
	}

	@Override
	public ScriptValue setValue(Referenced ref, ScriptValue value) throws Exception_Nodeable {
		return this.getValue().setValue(ref, value);
	}

	@Override
	public int valuesCompare(Referenced ref, ScriptValue rhs) throws Exception_Nodeable {
		return this.getValue().valuesCompare(ref, rhs);
	}

	@Override
	public boolean valuesEqual(Referenced ref, ScriptValue rhs) throws Exception_Nodeable {
		return this.getValue().valuesEqual(ref, rhs);
	}
}
