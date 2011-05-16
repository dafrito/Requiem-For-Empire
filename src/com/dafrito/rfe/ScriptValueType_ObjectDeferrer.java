package com.dafrito.rfe;
public class ScriptValueType_ObjectDeferrer extends ScriptValueType {
	private ScriptValue_Abstract template;
	private String name;

	public ScriptValueType_ObjectDeferrer(ScriptEnvironment env, ScriptValue_Abstract template, String name) {
		super(env);
		assert name != null;
		this.template = template;
		this.name = name;
	}

	public ScriptValueType_ObjectDeferrer(ScriptValue_Abstract template, String name) {
		this(template.getEnvironment(), template, name);
	}

	@Override
	public ScriptValueType getBaseType() throws Exception_Nodeable {
		if (this.template != null) {
			return this.getEnvironment().getTemplate(this.template.getType()).getVariable(this.name).getType();
		} else {
			return this.getEnvironment().retrieveVariable(this.name).getType();
		}
	}
}
