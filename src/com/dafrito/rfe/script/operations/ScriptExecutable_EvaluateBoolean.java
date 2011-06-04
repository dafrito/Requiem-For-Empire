package com.dafrito.rfe.script.operations;

import com.dafrito.rfe.gui.debug.Debugger;
import com.dafrito.rfe.inspect.Nodeable;
import com.dafrito.rfe.script.exceptions.Exception_InternalError;
import com.dafrito.rfe.script.exceptions.Exception_Nodeable;
import com.dafrito.rfe.script.exceptions.Exception_Nodeable_ClassCast;
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
	public ScriptValue castToType(Referenced ref, ScriptValueType type) throws Exception_Nodeable {
		assert Debugger.addNode("Type Casting", "Casting (" + this.getType() + " to " + type + ")");
		if (this.getType().equals(type)) {
			return this;
		}
		throw new Exception_Nodeable_ClassCast(ref, this, type);
	}

	// ScriptExecutable implementation
	@Override
	public ScriptValue execute() throws Exception_Nodeable {
		assert Debugger.openNode("Boolean Evaluations", "Executing Boolean Evaluation");
		assert Debugger.addNode(this);
		ScriptValue returning = null;
		assert Debugger.openNode("Getting left value");
		assert Debugger.addSnapNode("Left before resolution", this.lhs);
		ScriptValue lhs = this.lhs.getValue();
		assert Debugger.addSnapNode("Left", lhs);
		assert Debugger.closeNode();
		assert Debugger.openNode("Getting right value");
		assert Debugger.addSnapNode("Right before resolution", this.rhs);
		ScriptValue rhs = this.rhs.getValue();
		assert Debugger.addSnapNode("Right", rhs);
		assert Debugger.closeNode();
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
			throw new Exception_InternalError("Invalid default");
		}
		assert Debugger.closeNode("Returned value", returning);
		return returning;
	}

	// ScriptValue_Abstract implementation
	@Override
	public ScriptValueType getType() {
		return ScriptValueType.BOOLEAN;
	}

	@Override
	public ScriptValue getValue() throws Exception_Nodeable {
		return this.execute();
	}

	@Override
	public boolean isConvertibleTo(ScriptValueType type) {
		return this.getType().equals(type);
	}

	@Override
	public void nodificate() {
		assert Debugger.openNode("Boolean Expression (" + this.comparison + ")");
		assert Debugger.addSnapNode("Left hand", this.lhs);
		assert Debugger.addSnapNode("Right hand", this.rhs);
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
