package com.dafrito.rfe;

public class ScriptExecutable_AutoMathematicator extends ScriptElement implements ScriptExecutable, ScriptValue_Abstract, Nodeable {
	private ScriptValue_Abstract value;
	private ScriptOperatorType operator;
	private boolean isPost;

	public ScriptExecutable_AutoMathematicator(Referenced ref, ScriptValue_Abstract value, ScriptOperatorType operator, boolean isPost) {
		super(ref);
		this.value = value;
		this.operator = operator;
		this.isPost = isPost;
	}

	@Override
	public ScriptValue_Abstract castToType(Referenced ref, ScriptValueType type) throws Exception_Nodeable {
		return this.value = this.value.castToType(ref, type);
	}

	// ScriptExecutable implementation
	@Override
	public ScriptValue_Abstract execute() throws Exception_Nodeable {
		assert Debugger.openNode("Auto-Mathematicator Executions", "Executing Auto-Mathematicator");
		ScriptValue_Abstract returning;
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
	public ScriptValue_Abstract getValue() throws Exception_Nodeable {
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
	public boolean nodificate() {
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
		return true;
	}

	@Override
	public ScriptValue_Abstract setValue(Referenced ref, ScriptValue_Abstract value) throws Exception_Nodeable {
		return this.getValue().setValue(ref, value);
	}

	@Override
	public int valuesCompare(Referenced ref, ScriptValue_Abstract rhs) throws Exception_Nodeable {
		return this.getValue().valuesCompare(ref, rhs);
	}

	@Override
	public boolean valuesEqual(Referenced ref, ScriptValue_Abstract rhs) throws Exception_Nodeable {
		return this.getValue().valuesEqual(ref, rhs);
	}
}
