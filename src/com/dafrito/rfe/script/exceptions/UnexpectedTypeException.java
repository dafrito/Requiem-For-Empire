/**
 * 
 */
package com.dafrito.rfe.script.exceptions;

import com.dafrito.rfe.logging.Logs;
import com.dafrito.rfe.script.ScriptEnvironment;
import com.dafrito.rfe.script.parsing.Referenced;

public class UnexpectedTypeException extends ScriptException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7758477287327463222L;
	private String expectedType;
	private Object providedType;

	public UnexpectedTypeException(Referenced ref, Object provided, String exp) {
		super(ref);
		this.providedType = provided;
		this.expectedType = exp;
	}

	public UnexpectedTypeException(Referenced provided, String expectedType) {
		super(provided.getEnvironment(), provided);
		this.expectedType = expectedType;
		this.providedType = provided;
	}

	public UnexpectedTypeException(ScriptEnvironment env, Object provided, String exp) {
		super(env);
		this.providedType = provided;
		this.expectedType = exp;
	}

	@Override
	public void getExtendedInformation() {
		assert Logs.addNode("The type or keyword, " + this.providedType + ", is unexpected here (" + this.expectedType + " is expected)");
	}

	@Override
	public String getName() {
		return "Unexpected Type (" + this.providedType + ")";
	}
}