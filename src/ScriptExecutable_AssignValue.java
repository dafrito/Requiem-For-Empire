public class ScriptExecutable_AssignValue extends ScriptElement implements ScriptExecutable, ScriptValue_Abstract, Nodeable {
	private ScriptValue_Abstract m_variable;
	private ScriptValue_Abstract m_value;

	public ScriptExecutable_AssignValue(Referenced ref, ScriptValue_Abstract lhs, ScriptValue_Abstract rhs) {
		super(ref);
		this.m_variable = lhs;
		this.m_value = rhs;
	}

	@Override
	public ScriptValue_Abstract castToType(Referenced ref, ScriptValueType type) throws Exception_Nodeable {
		return this.m_variable.castToType(ref, type);
	}

	// ScriptExecutable implementation
	@Override
	public ScriptValue_Abstract execute() throws Exception_Nodeable {
		assert Debugger.openNode("Value-Assignment Expressions", "Assigning Value");
		assert Debugger.addSnapNode("Left variable", this.m_variable);
		assert Debugger.openNode("Retrieving value");
		assert Debugger.addSnapNode("Current value", this.m_value);
		ScriptValue_Abstract value = this.m_value.getValue();
		assert Debugger.closeNode();
		assert Debugger.addSnapNode("Right value", value);
		value = this.m_variable.setValue(this, value);
		assert Debugger.closeNode();
		return value;
	}

	public ScriptValue_Abstract getLeft() {
		return this.m_variable;
	}

	// ScriptValue_Abstract implementation
	@Override
	public ScriptValueType getType() {
		return this.m_variable.getType();
	}

	@Override
	public ScriptValue_Abstract getValue() throws Exception_Nodeable {
		return this.execute();
	}

	@Override
	public boolean isConvertibleTo(ScriptValueType type) {
		return this.m_variable.isConvertibleTo(type);
	}

	// Nodeable implementation
	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Assignment Script Expression");
		assert super.nodificate();
		assert Debugger.addSnapNode("Left variable", this.m_variable);
		assert Debugger.addSnapNode("Right value", this.m_value);
		assert Debugger.closeNode();
		return true;
	}

	@Override
	public ScriptValue_Abstract setValue(Referenced ref, ScriptValue_Abstract value) throws Exception_Nodeable {
		assert Debugger.openNode("Value Assignments", "Setting assigment-expression's right-side value");
		assert Debugger.addSnapNode("Former value", this.m_value);
		this.m_value = value.castToType(this, this.getType());
		assert Debugger.addSnapNode("New value", this.m_value);
		ScriptValue_Abstract returning = this.execute();
		assert Debugger.closeNode();
		return returning;
	}

	@Override
	public int valuesCompare(Referenced ref, ScriptValue_Abstract rhs) throws Exception_Nodeable {
		return this.m_variable.valuesCompare(ref, rhs);
	}

	@Override
	public boolean valuesEqual(Referenced ref, ScriptValue_Abstract rhs) throws Exception_Nodeable {
		return this.m_variable.valuesEqual(ref, rhs);
	}
}
