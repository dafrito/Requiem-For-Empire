/**
 * 
 */
package com.dafrito.rfe.script.exceptions;

import com.dafrito.rfe.script.parsing.Referenced;

public class Exception_Nodeable_UnenclosedStringLiteral extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7575185880647475669L;

	public Exception_Nodeable_UnenclosedStringLiteral(Referenced elem) {
		super(elem);
	}

	@Override
	public String getName() {
		return "Unenclosed String";
	}
}