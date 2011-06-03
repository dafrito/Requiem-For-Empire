/**
 * 
 */
package com.dafrito.rfe.script.exceptions;

import com.dafrito.rfe.gui.debug.Debugger;
import com.dafrito.rfe.script.Referenced;
import com.dafrito.rfe.script.ScriptEnvironment;

public class Exception_Nodeable_UnexpectedType extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7758477287327463222L;
	private String expectedType;
	private Object providedType;

	public Exception_Nodeable_UnexpectedType(Referenced ref, Object provided, String exp) {
		super(ref);
		this.providedType = provided;
		this.expectedType = exp;
	}

	public Exception_Nodeable_UnexpectedType(Referenced provided, String expectedType) {
		super(provided.getEnvironment(), provided);
		this.expectedType = expectedType;
		this.providedType = provided;
	}

	public Exception_Nodeable_UnexpectedType(ScriptEnvironment env, Object provided, String exp) {
		super(env);
		this.providedType = provided;
		this.expectedType = exp;
	}

	@Override
	public void getExtendedInformation() {
		assert Debugger.addNode("The type or keyword, " + this.providedType + ", is unexpected here (" + this.expectedType + " is expected)");
	}

	@Override
	public String getName() {
		return "Unexpected Type (" + this.providedType + ")";
	}
}