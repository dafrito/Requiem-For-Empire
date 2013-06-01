package com.dafrito.rfe.script.operations;

import com.dafrito.rfe.inspect.Nodeable;
import com.dafrito.rfe.logging.Logs;
import com.dafrito.rfe.script.exceptions.ClassCastScriptException;
import com.dafrito.rfe.script.exceptions.ScriptException;
import com.dafrito.rfe.script.parsing.Referenced;
import com.dafrito.rfe.script.parsing.ScriptElement;
import com.dafrito.rfe.script.parsing.ScriptOperatorType;
import com.dafrito.rfe.script.values.ScriptValue;
import com.dafrito.rfe.script.values.ScriptValueType;
import com.dafrito.rfe.script.values.ScriptValue_Boolean;

public class ScriptExecutable_EvaluateBoolean extends ScriptElement implements ScriptExecutable, ScriptValue, Nodeable {
	private ScriptValue lhs, rhs;
	private ScriptOperatorType comparison;

	public ScriptExecutable_EvaluateBoolean(Referenced ref, ScriptValue lhs, ScriptValue rhs, ScriptOperatorType comparison) {
		super(ref);
		this.lhs = lhs;
		this.rhs = rhs;
		this.comparison = comparison;
	}

	@Override
	public ScriptValue castToType(Referenced ref, ScriptValueType type) throws ScriptException {
		assert Logs.addNode("Type Casting", "Casting (" + this.getType() + " to " + type + ")");
		if (this.getType().equals(type)) {
			return this;
		}
		throw new ClassCastScriptException(ref, this, type);
	}

	// ScriptExecutable implementation
	@Override
	public ScriptValue execute() throws ScriptException {
		assert Logs.openNode("Boolean Evaluations", "Executing Boolean Evaluation");
		assert Logs.addNode(this);
		ScriptValue returning = null;
		assert Logs.openNode("Getting left value");
		assert Logs.addSnapNode("Left before resolution", this.lhs);
		ScriptValue lhs = this.lhs.getValue();
		assert Logs.addSnapNode("Left", lhs);
		assert Logs.closeNode();
		assert Logs.openNode("Getting right value");
		assert Logs.addSnapNode("Right before resolution", this.rhs);
		ScriptValue rhs = this.rhs.getValue();
		assert Logs.addSnapNode("Right", rhs);
		assert Logs.closeNode();
		switch (this.comparison) {
		case NONEQUIVALENCY:
			returning = new ScriptValue_Boolean(this.getEnvironment(), !lhs.valuesEqual(this, rhs));
			break;
		case LESS:
			returning = new ScriptValue_Boolean(this.getEnvironment(), (lhs.valuesCompare(this, rhs) < 0));
			break;
		case LESSEQUALS:
			returning = new ScriptValue_Boolean(this.getEnvironment(), (lhs.valuesCompare(this, rhs) <= 0));
			break;
		case EQUIVALENCY:
			returning = new ScriptValue_Boolean(this.getEnvironment(), lhs.valuesEqual(this, rhs));
			break;
		case GREATEREQUALS:
			returning = new ScriptValue_Boolean(this.getEnvironment(), (lhs.valuesCompare(this, rhs) >= 0));
			break;
		case GREATER:
			returning = new ScriptValue_Boolean(this.getEnvironment(), (lhs.valuesCompare(this, rhs) > 0));
			break;
		default:
			throw new AssertionError("Invalid default");
		}
		assert Logs.closeNode("Returned value", returning);
		return returning;
	}

	// ScriptValue_Abstract implementation
	@Override
	public ScriptValueType getType() {
		return ScriptValueType.BOOLEAN;
	}

	@Override
	public ScriptValue getValue() throws ScriptException {
		return this.execute();
	}

	@Override
	public boolean isConvertibleTo(ScriptValueType type) {
		return this.getType().equals(type);
	}

	@Override
	public void nodificate() {
		assert Logs.openNode("Boolean Expression (" + this.comparison + ")");
		assert Logs.addSnapNode("Left hand", this.lhs);
		assert Logs.addSnapNode("Right hand", this.rhs);
		assert Logs.closeNode();
	}

	@Override
	public ScriptValue setValue(Referenced ref, ScriptValue value) throws ScriptException {
		return this.getValue().setValue(ref, value);
	}

	@Override
	public int valuesCompare(Referenced ref, ScriptValue rhs) throws ScriptException {
		return this.getValue().valuesCompare(ref, rhs);
	}

	@Override
	public boolean valuesEqual(Referenced ref, ScriptValue rhs) throws ScriptException {
		return this.getValue().valuesEqual(ref, rhs);
	}
}
