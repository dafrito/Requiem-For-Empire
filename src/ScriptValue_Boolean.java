public class ScriptValue_Boolean implements ScriptConvertible, ScriptValue_Abstract, Nodeable {
	private boolean m_value;
	private final ScriptEnvironment m_environment;

	public ScriptValue_Boolean(ScriptEnvironment env, boolean value) {
		m_environment = env;
		m_value = value;
	}

	@Override
	public ScriptValue_Abstract castToType(Referenced ref, ScriptValueType type) throws Exception_Nodeable {
		assert Debugger.addNode("Type Casting", "Casting (" + getType() + " to " + type + ")");
		if (getType().equals(type)) {
			return this;
		}
		throw new Exception_Nodeable_ClassCast(ref, this, type);
	}

	// Overloaded and miscellaneous functions
	@Override
	public Object convert() {
		return new Boolean(m_value);
	}

	public boolean getBooleanValue() {
		return m_value;
	}

	// Abstract-value implementation
	@Override
	public ScriptEnvironment getEnvironment() {
		return m_environment;
	}

	@Override
	public ScriptValueType getType() {
		return ScriptValueType.BOOLEAN;
	}

	@Override
	public ScriptValue_Abstract getValue() throws Exception_Nodeable {
		return this;
	}

	@Override
	public boolean isConvertibleTo(ScriptValueType type) {
		return getType().equals(type);
	}

	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Boolean Script Value (" + getBooleanValue() + ")");
		assert Debugger.addNode("Reference: " + this);
		assert Debugger.closeNode();
		return true;
	}

	@Override
	public ScriptValue_Abstract setValue(Referenced ref, ScriptValue_Abstract value) throws Exception_Nodeable {
		assert Debugger.openNode("Value Assignments", "Setting Boolean Value");
		assert Debugger.addSnapNode("Former value", this);
		m_value = ((ScriptValue_Boolean) value.castToType(ref, getType())).getBooleanValue();
		assert Debugger.closeNode("New value", this);
		return this;
	}

	@Override
	public int valuesCompare(Referenced ref, ScriptValue_Abstract rhs) throws Exception_Nodeable {
		throw new Exception_Nodeable_IncomparableObjects(ref, this, rhs);
	}

	@Override
	public boolean valuesEqual(Referenced ref, ScriptValue_Abstract rhs) throws Exception_Nodeable {
		return ((ScriptValue_Boolean) getValue()).getBooleanValue() == ((ScriptValue_Boolean) rhs.castToType(ref, getType())).getBooleanValue();
	}
}
