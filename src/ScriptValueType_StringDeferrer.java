public class ScriptValueType_StringDeferrer extends ScriptValueType {
	private final String m_typeString;
	private Referenced m_reference;

	public ScriptValueType_StringDeferrer(Referenced ref, String string) {
		super(ref.getEnvironment());
		assert ref.getEnvironment() != null : "String is null";
		assert string != null : "Environment is null";
		this.m_reference = ref;
		this.m_typeString = string;
	}

	public ScriptValueType_StringDeferrer(ScriptEnvironment env, String string) {
		super(env);
		assert env != null : "Environment is null";
		assert string != null : "String is null";
		this.m_typeString = string;
	}

	@Override
	public ScriptValueType getBaseType() throws Exception_Nodeable {
		ScriptValueType kw = this.getEnvironment().getType(this.m_typeString);
		if (kw == null) {
			if (this.m_reference == null) {
				throw new Exception_Nodeable_VariableTypeNotFound((ScriptEnvironment) null, this.m_typeString);
			} else {
				throw new Exception_Nodeable_VariableTypeNotFound(this.m_reference, this.m_typeString);
			}
		}
		return kw;
	}
}
