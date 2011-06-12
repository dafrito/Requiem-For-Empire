/**
 * 
 */
package com.dafrito.rfe.script.parsing;

import com.dafrito.rfe.inspect.Inspectable;

/**
 * An assortment of common character groupings.
 * 
 * @author Aaron Faanes
 * 
 */
@Inspectable
public enum CharacterGroup {
	BLOCK_COMMENT("/*", "*/", false),
	CURLY_BRACES("{", "}"),
	PARENTHESES("(", ")"),
	SQUARE_BRACKETS("[", "]"),
	ANGLE_BRACKETS("<", ">"),
	XML_COMMENT("<!--", "-->"),
	DOUBLE_QUOTES("\""),
	SINGLE_QUOTES("'"),
	BACKQUOTES("`");

	private final String start, end;
	private final boolean recursive;

	private CharacterGroup(final String both) {
		this(both, both, false);
	}

	private CharacterGroup(final String start, final String end) {
		this(start, end, true);
	}

	private CharacterGroup(final String start, final String end, final boolean recursive) {
		this.start = start;
		this.end = end;
		this.recursive = recursive;
	}

	@Inspectable
	public String getStart() {
		return this.start;
	}

	@Inspectable
	public String getEnd() {
		return this.end;
	}

	@Inspectable
	public boolean isRecursive() {
		return this.recursive;
	}

	@Override
	public String toString() {
		return String.format("CharacterGroup%s: %s %s", this.isRecursive() ? "[recursive]" : "", this.getStart(), this.getEnd());
	}
}