public class ScriptExecutable_CastExpression extends ScriptElement implements ScriptExecutable, ScriptValue_Abstract, Nodeable {
	private ScriptExecutable m_castExpression;
	private ScriptValueType m_type;

	public ScriptExecutable_CastExpression(Referenced ref, ScriptValueType type, ScriptExecutable exec) {
		super(ref);
		this.m_castExpression = exec;
		this.m_type = type;
	}

	@Override
	public ScriptValue_Abstract castToType(Referenced ref, ScriptValueType type) throws Exception_Nodeable {
		return this.getValue().castToType(ref, type);
	}

	// ScriptExecutable implementation
	@Override
	public ScriptValue_Abstract execute() throws Exception_Nodeable {
		ScriptValue_Abstract left = this.m_castExpression.execute().getValue();
		assert Debugger.openNode("Cast Expression Executions", "Executing cast to " + this.getType());
		assert Debugger.addSnapNode("Value", left);
		ScriptValue_Abstract value = this.m_castExpression.execute().castToType(this, this.getType());
		assert Debugger.closeNode();
		return value;
	}

	// Abstract-value implementation
	@Override
	public ScriptValueType getType() {
		return this.m_type;
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
		assert Debugger.openNode("Script Cast Expression (To type: " + this.getType() + ")");
		assert super.nodificate();
		assert Debugger.addSnapNode("Cast Expression", this.m_castExpression);
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
