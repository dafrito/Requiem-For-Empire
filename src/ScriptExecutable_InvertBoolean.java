public class ScriptExecutable_InvertBoolean extends ScriptElement implements ScriptExecutable, ScriptValue_Abstract {
	private ScriptExecutable m_value;

	public ScriptExecutable_InvertBoolean(Referenced ref, ScriptExecutable value) {
		super(ref);
		m_value = value;
	}

	@Override
	public ScriptValue_Abstract castToType(Referenced ref, ScriptValueType type) throws Exception_Nodeable {
		return getValue().castToType(ref, type);
	}

	// ScriptExecutable implementation
	@Override
	public ScriptValue_Abstract execute() throws Exception_Nodeable {
		return new ScriptValue_Boolean(getEnvironment(), !((ScriptValue_Boolean) m_value.execute()).getBooleanValue());
	}

	// ScriptValue_Abstract implementation
	@Override
	public ScriptValueType getType() {
		return ScriptValueType.BOOLEAN;
	}

	@Override
	public ScriptValue_Abstract getValue() throws Exception_Nodeable {
		return execute();
	}

	@Override
	public boolean isConvertibleTo(ScriptValueType type) {
		return getType().equals(type);
	}

	// Nodeable implementation
	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Boolean Inverter");
		assert super.nodificate();
		assert Debugger.addSnapNode("Value", m_value);
		assert Debugger.closeNode();
		return true;
	}

	@Override
	public ScriptValue_Abstract setValue(Referenced ref, ScriptValue_Abstract value) throws Exception_Nodeable {
		return getValue().setValue(ref, value);
	}

	@Override
	public int valuesCompare(Referenced ref, ScriptValue_Abstract rhs) throws Exception_Nodeable {
		return getValue().valuesCompare(ref, rhs);
	}

	@Override
	public boolean valuesEqual(Referenced ref, ScriptValue_Abstract rhs) throws Exception_Nodeable {
		return getValue().valuesEqual(ref, rhs);
	}
}
