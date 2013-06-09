package com.dafrito.rfe.script.values;

import com.dafrito.rfe.script.ScriptEnvironment;
import com.dafrito.rfe.script.exceptions.VariableTypeNotFoundException;
import com.dafrito.rfe.script.exceptions.ScriptException;
import com.dafrito.rfe.script.parsing.Referenced;

public class ScriptValueType_StringDeferrer extends ScriptValueType {
	private final String typeString;
	private Referenced reference;

	public ScriptValueType_StringDeferrer(Referenced ref, String string) {
		super(ref.getEnvironment());
		assert ref.getEnvironment() != null : "String is null";
		assert string != null : "Environment is null";
		this.reference = ref;
		this.typeString = string;
	}

	public ScriptValueType_StringDeferrer(ScriptEnvironment env, String string) {
		super(env);
		assert env != null : "Environment is null";
		assert string != null : "String is null";
		this.typeString = string;
	}

	@Override
	public ScriptValueType getBaseType() throws ScriptException {
		ScriptValueType kw = this.getEnvironment().getType(this.typeString);
		if (kw == null) {
			if (this.reference == null) {
				throw new VariableTypeNotFoundException((ScriptEnvironment) null, this.typeString);
			} else {
				throw new VariableTypeNotFoundException(this.reference, this.typeString);
			}
		}
		return kw;
	}
}
