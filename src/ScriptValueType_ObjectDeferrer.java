public class ScriptValueType_ObjectDeferrer extends ScriptValueType {
	private ScriptValue_Abstract m_template;
	private String m_name;

	public ScriptValueType_ObjectDeferrer(ScriptEnvironment env, ScriptValue_Abstract template, String name) {
		super(env);
		assert name != null;
		this.m_template = template;
		this.m_name = name;
	}

	public ScriptValueType_ObjectDeferrer(ScriptValue_Abstract template, String name) {
		this(template.getEnvironment(), template, name);
	}

	@Override
	public ScriptValueType getBaseType() throws Exception_Nodeable {
		if (this.m_template != null) {
			return this.getEnvironment().getTemplate(this.m_template.getType()).getVariable(this.m_name).getType();
		} else {
			return this.getEnvironment().retrieveVariable(this.m_name).getType();
		}
	}
}
