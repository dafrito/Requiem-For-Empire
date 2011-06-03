/**
 * 
 */
package com.dafrito.rfe.script.exceptions;

import com.dafrito.rfe.script.parsing.Referenced;

public class Exception_Nodeable_UnenclosedBracket extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4450711851110352808L;

	public Exception_Nodeable_UnenclosedBracket(Referenced elem) {
		super(elem);
	}

	@Override
	public String getName() {
		return "Unenclosed Bracket";
	}
}