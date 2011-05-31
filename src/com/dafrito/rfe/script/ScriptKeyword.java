package com.dafrito.rfe.script;

import com.dafrito.rfe.debug.Debugger;
import com.dafrito.rfe.inspect.Nodeable;
import com.dafrito.rfe.script.exceptions.Exception_Nodeable;

public class ScriptKeyword extends ScriptElement implements Nodeable {
	// String<-->Keyword functions
	public static ScriptKeywordType getType(String string) {
		if ("unique".equals(string)) {
			return ScriptKeywordType.UNIQUE;
		}
		if ("Stylesheet".equals(string)) {
			return ScriptKeywordType.STYLESHEET;
		}
		if ("new".equals(string)) {
			return ScriptKeywordType.NEW;
		}
		if ("if".equals(string)) {
			return ScriptKeywordType.IF;
		}
		if ("public".equals(string)) {
			return ScriptKeywordType.PUBLIC;
		}
		if ("class".equals(string)) {
			return ScriptKeywordType.CLASS;
		}
		if ("extends".equals(string)) {
			return ScriptKeywordType.EXTENDS;
		}
		if ("implements".equals(string)) {
			return ScriptKeywordType.IMPLEMENTS;
		}
		if ("short".equals(string)) {
			return ScriptKeywordType.SHORT;
		}
		if ("int".equals(string)) {
			return ScriptKeywordType.INT;
		}
		if ("long".equals(string)) {
			return ScriptKeywordType.LONG;
		}
		if ("float".equals(string)) {
			return ScriptKeywordType.FLOAT;
		}
		if ("double".equals(string)) {
			return ScriptKeywordType.DOUBLE;
		}
		if ("return".equals(string)) {
			return ScriptKeywordType.RETURN;
		}
		if ("static".equals(string)) {
			return ScriptKeywordType.STATIC;
		}
		if ("void".equals(string)) {
			return ScriptKeywordType.VOID;
		}
		if ("string".equals(string)) {
			return ScriptKeywordType.STRING;
		}
		if ("private".equals(string)) {
			return ScriptKeywordType.PRIVATE;
		}
		if ("protected".equals(string)) {
			return ScriptKeywordType.PROTECTED;
		}
		if ("else".equals(string)) {
			return ScriptKeywordType.ELSE;
		}
		if ("for".equals(string)) {
			return ScriptKeywordType.FOR;
		}
		if ("abstract".equals(string)) {
			return ScriptKeywordType.ABSTRACT;
		}
		if ("null".equals(string)) {
			return ScriptKeywordType.NULL;
		}
		if ("dotted".equals(string)) {
			return ScriptKeywordType.dotted;
		}
		if ("solid".equals(string)) {
			return ScriptKeywordType.solid;
		}
		if ("none".equals(string)) {
			return ScriptKeywordType.none;
		}
		if ("this".equals(string)) {
			return ScriptKeywordType.THIS;
		}
		if ("super".equals(string)) {
			return ScriptKeywordType.SUPER;
		}
		return ScriptKeywordType.UNKNOWN;
	}

	public static ScriptValueType getValueType(ScriptKeywordType type) {
		switch (type) {
		case VOID:
			return ScriptValueType.VOID;
		case BOOLEAN:
			return ScriptValueType.BOOLEAN;
		case SHORT:
			return ScriptValueType.SHORT;
		case INT:
			return ScriptValueType.INT;
		case LONG:
			return ScriptValueType.LONG;
		case FLOAT:
			return ScriptValueType.FLOAT;
		case DOUBLE:
			return ScriptValueType.DOUBLE;
		case STRING:
			return ScriptValueType.STRING;
		default:
			return null;
		}
	}

	private final ScriptKeywordType type;

	// Constructors
	public ScriptKeyword(Referenced ref, ScriptKeywordType type) throws Exception_Nodeable {
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

	public ScriptKeywordType getType() {
		return this.type;
	}

	public ScriptValueType getValueType() {
		return getValueType(this.getType());
	}

	@Override
	public void nodificate() {
		assert Debugger.openNode("ScriptKeyword (" + this.getType() + ")");
		super.nodificate();
		assert Debugger.closeNode();
	}

	@Override
	public String toString() {
		return this.getType().toString();
	}
}
