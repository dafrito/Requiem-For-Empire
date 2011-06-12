/**
 * 
 */
package com.dafrito.rfe.script.exceptions;

import com.dafrito.rfe.script.parsing.Referenced;

public class DivisionByZeroScriptException extends ScriptException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3401425743716487200L;

	public DivisionByZeroScriptException(Referenced ref) {
		super(ref.getEnvironment(), ref);
	}

	@Override
	public String getName() {
		return "Division by Zero";
	}
}