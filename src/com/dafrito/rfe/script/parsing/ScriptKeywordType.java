/**
 * 
 */
package com.dafrito.rfe.script.parsing;

import com.dafrito.rfe.inspect.Inspectable;
import com.dafrito.rfe.script.values.ScriptValueType;

@Inspectable
public enum ScriptKeywordType {
	NULL,
	VOID(ScriptValueType.VOID),
	BOOLEAN(ScriptValueType.BOOLEAN),
	SHORT(ScriptValueType.SHORT),
	INT(ScriptValueType.INT),
	LONG(ScriptValueType.LONG),
	FLOAT(ScriptValueType.FLOAT),
	DOUBLE(ScriptValueType.DOUBLE),
	STRING(ScriptValueType.STRING),
	STYLESHEET("Stylesheet"),
	NEW, IF, FOR, ELSE, RETURN, THIS, SUPER,
	CLASS, EXTENDS, IMPLEMENTS,
	PRIVATE, PROTECTED, PUBLIC, STATIC, ABSTRACT,
	dotted, solid, none;

	private final String canonical;
	private final ScriptValueType valueType;

	private ScriptKeywordType() {
		this.canonical = this.name().toLowerCase();
		this.valueType = null;
	}

	private ScriptKeywordType(final String canonical) {
		this.canonical = canonical;
		this.valueType = null;
	}

	private ScriptKeywordType(final ScriptValueType valueType) {
		this.canonical = this.name().toLowerCase();
		this.valueType = valueType;
	}

	private ScriptKeywordType(final ScriptValueType valueType, final String canonical) {
		this.canonical = canonical;
		this.valueType = valueType;
	}

	/**
	 * Returns the name in the correct case, as it would appear in code.
	 * 
	 * @return the exact capitalization of this keyword
	 */
	@Inspectable
	public String canonical() {
		return this.canonical;
	}

	/**
	 * Returns the value type that this keyword represents.
	 * 
	 * @return the value type of this keyword, or {@code null} if this keyword
	 *         is not a type
	 */
	@Inspectable
	public ScriptValueType getValueType() {
		return this.valueType;
	}

	/**
	 * Retrieves a keyword using the case-sensitive canonical spelling.
	 * 
	 * @param canonical
	 *            the canonical spelling of the keyword
	 * @return a {@link ScriptKeywordType} that matches the canonical string,
	 *         otherwise {@code null}
	 */
	public static ScriptKeywordType fromCanonical(String canonical) {
		for (ScriptKeywordType type : ScriptKeywordType.values()) {
			if (type.canonical().equals(canonical)) {
				return type;
			}
		}
		return null;
	}

}