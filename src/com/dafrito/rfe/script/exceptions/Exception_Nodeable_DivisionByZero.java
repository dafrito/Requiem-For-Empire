/**
 * 
 */
package com.dafrito.rfe.script.exceptions;

import com.dafrito.rfe.debug.Debugger;
import com.dafrito.rfe.script.Referenced;

public class Exception_Nodeable_DivisionByZero extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3401425743716487200L;

	public Exception_Nodeable_DivisionByZero(Referenced ref) {
		super(ref.getEnvironment(), ref);
	}

	@Override
	public void getExtendedInformation() {
		assert Debugger.addNode("Illegal mindfucking division by zero was encountered.");
	}

	@Override
	public String getName() {
		return "Division by Zero";
	}
}