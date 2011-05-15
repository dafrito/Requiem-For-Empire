public class ScriptValue_Faux implements Nodeable, ScriptValue_Abstract {
	private final ScriptEnvironment m_environment;
	private final ScriptValueType m_type;

	public ScriptValue_Faux(ScriptEnvironment env, ScriptValueType type) {
		m_environment = env;
		m_type = type;
	}

	@Override
	public ScriptValue_Abstract castToType(Referenced ref, ScriptValueType type) throws Exception_Nodeable {
		throw new Exception_InternalError(getEnvironment(), "Invalid call in ScriptValue_Faux");
	}

	@Override
	public ScriptEnvironment getEnvironment() {
		return m_environment;
	}

	@Override
	public ScriptValueType getType() {
		return m_type;
	}

	@Override
	public ScriptValue_Abstract getValue() throws Exception_Nodeable {
		throw new Exception_InternalError(getEnvironment(), "Invalid call in ScriptValue_Faux");
	}

	@Override
	public boolean isConvertibleTo(ScriptValueType type) {
		return ScriptValueType.isConvertibleTo(getEnvironment(), getType(), type);
	}

	@Override
	public boolean nodificate() {
		assert Debugger.addNode("Faux Script-Value (" + getType() + ")");
		return true;
	}

	protected void setType(ScriptValueType type) {
		throw new Exception_InternalError(getEnvironment(), "Invalid call in ScriptValue_Faux");
	}

	@Override
	public ScriptValue_Abstract setValue(Referenced ref, ScriptValue_Abstract value) throws Exception_Nodeable {
		throw new Exception_InternalError(getEnvironment(), "Invalid call in ScriptValue_Faux");
	}

	@Override
	public int valuesCompare(Referenced ref, ScriptValue_Abstract rhs) throws Exception_Nodeable {
		throw new Exception_InternalError(getEnvironment(), "Invalid call in ScriptValue_Faux");
	}

	@Override
	public boolean valuesEqual(Referenced ref, ScriptValue_Abstract rhs) throws Exception_Nodeable {
		throw new Exception_InternalError(getEnvironment(), "Invalid call in ScriptValue_Faux");
	}
}
