/**
 * 
 */
package com.dafrito.rfe.script.parsing;

public enum ScriptKeywordType {
	UNKNOWN, NULL,
	VOID, BOOLEAN, SHORT, INT, LONG, FLOAT, DOUBLE, STRING, STYLESHEET,
	NEW, IF, FOR, ELSE, RETURN, THIS, SUPER,
	CLASS, EXTENDS, IMPLEMENTS,
	PRIVATE, PROTECTED, PUBLIC, STATIC, ABSTRACT,
	dotted, solid, none
}