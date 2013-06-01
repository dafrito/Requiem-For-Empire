package com.dafrito.rfe.script.operations;

import com.dafrito.rfe.inspect.Nodeable;
import com.dafrito.rfe.logging.Logs;
import com.dafrito.rfe.script.exceptions.ScriptException;
import com.dafrito.rfe.script.parsing.Referenced;
import com.dafrito.rfe.script.parsing.ScriptElement;
import com.dafrito.rfe.script.parsing.ScriptOperatorType;
import com.dafrito.rfe.script.values.ScriptValue;
import com.dafrito.rfe.script.values.ScriptValueType;
import com.dafrito.rfe.script.values.ScriptValue_Numeric;

public class ScriptExecutable_AutoMathematicator extends ScriptElement implements ScriptExecutable, ScriptValue, Nodeable {
	private ScriptValue value;
	private ScriptOperatorType operator;
	private boolean isPost;

	public ScriptExecutable_AutoMathematicator(Referenced ref, ScriptValue value, ScriptOperatorType operator, boolean isPost) {
		super(ref);
		this.value = value;
		this.operator = operator;
		this.isPost = isPost;
	}

	@Override
	public ScriptValue castToType(Referenced ref, ScriptValueType type) throws ScriptException {
		return this.value = this.value.castToType(ref, type);
	}

	// ScriptExecutable implementation
	@Override
	public ScriptValue execute() throws ScriptException {
		assert Logs.openNode("Auto-Mathematicator Executions", "Executing Auto-Mathematicator");
		ScriptValue returning;
		if (this.operator == ScriptOperatorType.INCREMENT) {
			returning = ((ScriptValue_Numeric) this.value.getValue()).setNumericValue(((ScriptValue_Numeric) this.value.getValue()).increment(this));
		} else {
			returning = ((ScriptValue_Numeric) this.value.getValue()).setNumericValue(((ScriptValue_Numeric) this.value.getValue()).decrement(this));
		}
		assert Logs.closeNode();
		return returning;
	}

	// ScriptValue_Abstract implementation
	@Override
	public ScriptValueType getType() {
		return this.value.getType();
	}

	@Override
	public ScriptValue getValue() throws ScriptException {
		assert this.value.getValue() instanceof ScriptValue_Numeric : "Should be a ScriptValue_Numeric: " + this.value.getValue();
		ScriptValue_Numeric value = new ScriptValue_Numeric(this.getEnvironment(), ((ScriptValue_Numeric) this.value.getValue()).getNumericValue());
		ScriptValue_Numeric otherValue = (ScriptValue_Numeric) this.execute().getValue();
		if (this.isPost) {
			return value;
		} else {
			return otherValue;
		}
	}

	@Override
	public boolean isConvertibleTo(ScriptValueType type) {
		return this.value.isConvertibleTo(type);
	}

	// Nodeable implementation
	@Override
	public void nodificate() {
		if (this.operator == ScriptOperatorType.INCREMENT) {
			if (this.isPost) {
				assert Logs.openNode("Auto-Mathematicators", "Auto-Mathematicator(Post-Incrementing)");
			} else {
				assert Logs.openNode("Auto-Mathematicators", "Auto-Mathematicator(Pre-Incrementing)");
			}
		} else {
			if (this.isPost) {
				assert Logs.openNode("Auto-Mathematicators", "Auto-Mathematicator(Post-Decrementing)");
			} else {
				assert Logs.openNode("Auto-Mathematicators", "Auto-Mathematicator(Pre-Decrementing)");
			}
		}
		assert Logs.addSnapNode("Value", this.value);
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
