public class ScriptExecutable_EvaluateComboBoolean extends ScriptElement implements ScriptValue_Abstract, ScriptExecutable, Nodeable {
	private ScriptValue_Abstract m_lhs, m_rhs;
	private ScriptOperatorType m_operator;

	public ScriptExecutable_EvaluateComboBoolean(Referenced ref, ScriptValue_Abstract lhs, ScriptValue_Abstract rhs, ScriptOperatorType operator) {
		super(ref);
		this.m_lhs = lhs;
		this.m_rhs = rhs;
		this.m_operator = operator;
	}

	@Override
	public ScriptValue_Abstract castToType(Referenced ref, ScriptValueType type) throws Exception_Nodeable {
		return this.getValue().castToType(ref, type);
	}

	// ScriptExecutable implementation
	@Override
	public ScriptValue_Abstract execute() throws Exception_Nodeable {
		assert Debugger.openNode("Combo-Boolean Evaluations", "Evaluating Combo-Boolean Expression (" + ScriptOperator.getName(this.m_operator) + ")");
		assert Debugger.addNode(this);
		if (this.m_lhs.isConvertibleTo(ScriptValueType.BOOLEAN)) {
			throw new Exception_Nodeable_ClassCast(this, this.m_lhs, ScriptValueType.BOOLEAN);
		}
		if (this.m_rhs.isConvertibleTo(ScriptValueType.BOOLEAN)) {
			throw new Exception_Nodeable_ClassCast(this, this.m_rhs, ScriptValueType.BOOLEAN);
		}
		ScriptValue_Boolean lhs = (ScriptValue_Boolean) this.m_lhs.getValue();
		ScriptValue_Boolean rhs = (ScriptValue_Boolean) this.m_rhs.getValue();
		switch (this.m_operator) {
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
		return this.m_lhs.getType();
	}

	@Override
	public ScriptValue_Abstract getValue() throws Exception_Nodeable {
		return this.execute();
	}

	@Override
	public boolean isConvertibleTo(ScriptValueType type) {
		return ScriptValueType.isConvertibleTo(this.getEnvironment(), this.getType(), type);
	}

	// Nodeable implementation
	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Combo Boolean Expression");
		assert super.nodificate();
		assert Debugger.addSnapNode("Left side", this.m_lhs);
		assert Debugger.addSnapNode("Right side", this.m_rhs);
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
