package com.dafrito.rfe.script;

import com.dafrito.rfe.debug.Debugger;
import com.dafrito.rfe.inspect.Nodeable;
import com.dafrito.rfe.script.exceptions.Exception_Nodeable;

public class ScriptExecutable_CastExpression extends ScriptElement implements ScriptExecutable, ScriptValue, Nodeable {
	private ScriptExecutable castExpression;
	private ScriptValueType type;

	public ScriptExecutable_CastExpression(Referenced ref, ScriptValueType type, ScriptExecutable exec) {
		super(ref);
		this.castExpression = exec;
		this.type = type;
	}

	@Override
	public ScriptValue castToType(Referenced ref, ScriptValueType type) throws Exception_Nodeable {
		return this.getValue().castToType(ref, type);
	}

	// ScriptExecutable implementation
	@Override
	public ScriptValue execute() throws Exception_Nodeable {
		ScriptValue left = this.castExpression.execute().getValue();
		assert Debugger.openNode("Cast Expression Executions", "Executing cast to " + this.getType());
		assert Debugger.addSnapNode("Value", left);
		ScriptValue value = this.castExpression.execute().castToType(this, this.getType());
		assert Debugger.closeNode();
		return value;
	}

	// Abstract-value implementation
	@Override
	public ScriptValueType getType() {
		return this.type;
	}

	@Override
	public ScriptValue getValue() throws Exception_Nodeable {
		return this.execute();
	}

	@Override
	public boolean isConvertibleTo(ScriptValueType type) {
		return ScriptValueType.isConvertibleTo(this.getEnvironment(), this.getType(), type);
	}

	// Nodeable implementation
	@Override
	public void nodificate() {
		assert Debugger.openNode("Script Cast Expression (To type: " + this.getType() + ")");
		super.nodificate();
		assert Debugger.addSnapNode("Cast Expression", this.castExpression);
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
