package com.dafrito.rfe.script.values;

import com.dafrito.rfe.script.ScriptEnvironment;
import com.dafrito.rfe.script.exceptions.ScriptException;


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
	public ScriptValueType getBaseType() throws ScriptException {
		if (this.template != null) {
			return this.getEnvironment().getTemplate(this.template.getType()).getVariable(this.name).getType();
		} else {
			return this.getEnvironment().retrieveVariable(this.name).getType();
		}
	}
}
