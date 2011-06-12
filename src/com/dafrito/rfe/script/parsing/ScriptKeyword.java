package com.dafrito.rfe.script.parsing;

import com.dafrito.rfe.inspect.Inspectable;
import com.dafrito.rfe.script.exceptions.ScriptException;
import com.dafrito.rfe.script.values.ScriptValueType;

@Inspectable
public class ScriptKeyword extends ScriptElement {
	private final ScriptKeywordType type;

	public ScriptKeyword(final Referenced ref, final ScriptKeywordType type) throws ScriptException {
		super(ref);
		this.type = type;
	}

	@Inspectable
	public ScriptKeywordType getType() {
		return this.type;
	}

	public ScriptValueType getValueType() {
		return this.getType().getValueType();
	}

	// XXX This equals implementation is buggy since it's not transitive. 
	// I'm not sure how to fix it yet, so I'm just leaving it as-is.
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof ScriptValueType) {
			return this.getValueType().equals(obj);
		}
		if (obj instanceof ScriptKeywordType) {
			return this.getType().equals(obj);
		}
		if (!(obj instanceof ScriptKeyword)) {
			return false;
		}
		return ((ScriptKeyword) obj).getType().equals(this.type);
	}

	@Override
	public int hashCode() {
		return this.getType().hashCode();
	}

	@Override
	public String toString() {
		return this.getType().toString();
	}
}
