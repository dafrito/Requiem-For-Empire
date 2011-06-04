package com.dafrito.rfe.script.parsing;

import com.dafrito.rfe.inspect.Inspectable;
import com.dafrito.rfe.script.exceptions.Exception_Nodeable;
import com.dafrito.rfe.script.values.ScriptValueType;

@Inspectable
public class ScriptKeyword extends ScriptElement {
	private final ScriptKeywordType type;

	public ScriptKeyword(final Referenced ref, final ScriptKeywordType type) throws Exception_Nodeable {
		super(ref);
		this.type = type;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ScriptValueType) {
			return ((ScriptValueType) o) == this.getValueType();
		} else if (o instanceof ScriptKeywordType) {
			return ((ScriptKeywordType) o) == this.getType();
		} else {
			return ((ScriptKeyword) o).getType() == this.type;
		}
	}

	@Inspectable
	public ScriptKeywordType getType() {
		return this.type;
	}

	public ScriptValueType getValueType() {
		return this.getType().getValueType();
	}

	@Override
	public String toString() {
		return this.getType().toString();
	}
}
