public class ScriptExecutable_AutoMathematicator extends ScriptElement implements ScriptExecutable, ScriptValue_Abstract, Nodeable {
	private ScriptValue_Abstract m_value;
	private ScriptOperatorType m_operator;
	private boolean m_isPost;

	public ScriptExecutable_AutoMathematicator(Referenced ref, ScriptValue_Abstract value, ScriptOperatorType operator, boolean isPost) {
		super(ref);
		this.m_value = value;
		this.m_operator = operator;
		this.m_isPost = isPost;
	}

	@Override
	public ScriptValue_Abstract castToType(Referenced ref, ScriptValueType type) throws Exception_Nodeable {
		return this.m_value = this.m_value.castToType(ref, type);
	}

	// ScriptExecutable implementation
	@Override
	public ScriptValue_Abstract execute() throws Exception_Nodeable {
		assert Debugger.openNode("Auto-Mathematicator Executions", "Executing Auto-Mathematicator");
		ScriptValue_Abstract returning;
		if (this.m_operator == ScriptOperatorType.INCREMENT) {
			returning = ((ScriptValue_Numeric) this.m_value.getValue()).setNumericValue(((ScriptValue_Numeric) this.m_value.getValue()).increment(this));
		} else {
			returning = ((ScriptValue_Numeric) this.m_value.getValue()).setNumericValue(((ScriptValue_Numeric) this.m_value.getValue()).decrement(this));
		}
		assert Debugger.closeNode();
		return returning;
	}

	// ScriptValue_Abstract implementation
	@Override
	public ScriptValueType getType() {
		return this.m_value.getType();
	}

	@Override
	public ScriptValue_Abstract getValue() throws Exception_Nodeable {
		assert this.m_value.getValue() instanceof ScriptValue_Numeric : "Should be a ScriptValue_Numeric: " + this.m_value.getValue();
		ScriptValue_Numeric value = new ScriptValue_Numeric(this.getEnvironment(), ((ScriptValue_Numeric) this.m_value.getValue()).getNumericValue());
		ScriptValue_Numeric otherValue = (ScriptValue_Numeric) this.execute().getValue();
		if (this.m_isPost) {
			return value;
		} else {
			return otherValue;
		}
	}

	@Override
	public boolean isConvertibleTo(ScriptValueType type) {
		return this.m_value.isConvertibleTo(type);
	}

	// Nodeable implementation
	@Override
	public boolean nodificate() {
		if (this.m_operator == ScriptOperatorType.INCREMENT) {
			if (this.m_isPost) {
				assert Debugger.openNode("Auto-Mathematicators", "Auto-Mathematicator(Post-Incrementing)");
			} else {
				assert Debugger.openNode("Auto-Mathematicators", "Auto-Mathematicator(Pre-Incrementing)");
			}
		} else {
			if (this.m_isPost) {
				assert Debugger.openNode("Auto-Mathematicators", "Auto-Mathematicator(Post-Decrementing)");
			} else {
				assert Debugger.openNode("Auto-Mathematicators", "Auto-Mathematicator(Pre-Decrementing)");
			}
		}
		assert Debugger.addSnapNode("Value", this.m_value);
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
