package com.dafrito.rfe.script;

import com.dafrito.rfe.Debugger;
import com.dafrito.rfe.inspect.Nodeable;
import com.dafrito.rfe.script.exceptions.Exception_Nodeable;

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
	public ScriptValue castToType(Referenced ref, ScriptValueType type) throws Exception_Nodeable {
		return this.value = this.value.castToType(ref, type);
	}

	// ScriptExecutable implementation
	@Override
	public ScriptValue execute() throws Exception_Nodeable {
		assert Debugger.openNode("Auto-Mathematicator Executions", "Executing Auto-Mathematicator");
		ScriptValue returning;
		if (this.operator == ScriptOperatorType.INCREMENT) {
			returning = ((ScriptValue_Numeric) this.value.getValue()).setNumericValue(((ScriptValue_Numeric) this.value.getValue()).increment(this));
		} else {
			returning = ((ScriptValue_Numeric) this.value.getValue()).setNumericValue(((ScriptValue_Numeric) this.value.getValue()).decrement(this));
		}
		assert Debugger.closeNode();
		return returning;
	}

	// ScriptValue_Abstract implementation
	@Override
	public ScriptValueType getType() {
		return this.value.getType();
	}

	@Override
	public ScriptValue getValue() throws Exception_Nodeable {
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
				assert Debugger.openNode("Auto-Mathematicators", "Auto-Mathematicator(Post-Incrementing)");
			} else {
				assert Debugger.openNode("Auto-Mathematicators", "Auto-Mathematicator(Pre-Incrementing)");
			}
		} else {
			if (this.isPost) {
				assert Debugger.openNode("Auto-Mathematicators", "Auto-Mathematicator(Post-Decrementing)");
			} else {
				assert Debugger.openNode("Auto-Mathematicators", "Auto-Mathematicator(Pre-Decrementing)");
			}
		}
		assert Debugger.addSnapNode("Value", this.value);
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
