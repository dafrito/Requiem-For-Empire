package com.dafrito.rfe.script;

import com.dafrito.rfe.script.exceptions.Exception_Nodeable;


public class ScriptValueType_ObjectDeferrer extends ScriptValueType {
	private ScriptValue template;
	private String name;

	public ScriptValueType_ObjectDeferrer(ScriptEnvironment env, ScriptValue template, String name) {
		super(env);
		assert name != null;
		this.template = template;
		this.name = name;
	}

	public ScriptValueType_ObjectDeferrer(ScriptValue template, String name) {
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