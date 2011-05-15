public class ScriptValue_String implements ScriptValue_Abstract, ScriptConvertible, Nodeable {
	private String m_value;
	private final ScriptEnvironment m_environment;

	public ScriptValue_String(ScriptEnvironment env, String string) {
		this.m_environment = env;
		this.m_value = string;
	}

	@Override
	public ScriptValue_Abstract castToType(Referenced ref, ScriptValueType type) throws Exception_Nodeable {
		assert Debugger.addNode("Type Casting", "Casting (" + this.getType() + " to " + type + ")");
		if (this.getType().equals(type)) {
			return this;
		}
		throw new Exception_Nodeable_ClassCast(ref, this, type);
	}

	// Interface implementations
	@Override
	public Object convert() {
		return new String(this.m_value);
	}

	// Abstract-value implementation
	@Override
	public ScriptEnvironment getEnvironment() {
		return this.m_environment;
	}

	public String getStringValue() {
		return this.m_value;
	}

	@Override
	public ScriptValueType getType() {
		return ScriptValueType.STRING;
	}

	@Override
	public ScriptValue_Abstract getValue() throws Exception_Nodeable {
		return this;
	}

	@Override
	public boolean isConvertibleTo(ScriptValueType type) {
		return this.getType().equals(type);
	}

	@Override
	public boolean nodificate() {
		assert Debugger.openNode("String Script-Value (" + this.getStringValue().length() + " character(s): " + this.getStringValue());
		assert Debugger.addSnapNode("Reference", super.toString());
		assert Debugger.closeNode();
		return true;
	}

	@Override
	public ScriptValue_Abstract setValue(Referenced ref, ScriptValue_Abstract value) throws Exception_Nodeable {
		assert Debugger.openNode("Value Assignments", "Setting String Value");
		assert Debugger.addSnapNode("Former value", this);
		this.m_value = ((ScriptValue_String) value.castToType(ref, this.getType())).getStringValue();
		assert Debugger.closeNode("New value", this);
		return this;
	}

	@Override
	public int valuesCompare(Referenced ref, ScriptValue_Abstract rhs) throws Exception_Nodeable {
		return this.getStringValue().compareTo(((ScriptValue_String) rhs.castToType(ref, this.getType())).getStringValue());
	}

	@Override
	public boolean valuesEqual(Referenced ref, ScriptValue_Abstract rhs) throws Exception_Nodeable {
		return this.getStringValue().equals(((ScriptValue_String) rhs.castToType(ref, this.getType())).getStringValue());
	}
}
