public class ScriptValueType_ValueDeferrer extends ScriptValueType {
	private ScriptValue_Abstract m_value;

	public ScriptValueType_ValueDeferrer(ScriptValue_Abstract value) {
		super(value.getEnvironment());
		assert value != null;
		this.m_value = value;
	}

	@Override
	public ScriptValueType getBaseType() throws Exception_Nodeable {
		return this.m_value.getType();
	}
}
