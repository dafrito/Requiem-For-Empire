/**
 * 
 */
package com.dafrito.rfe.script.exceptions;

import com.dafrito.rfe.Debugger;
import com.dafrito.rfe.Referenced;

public class Exception_Nodeable_UnparseableElement extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7815806790954972711L;
	private String source;

	public Exception_Nodeable_UnparseableElement(Referenced ref, String thrownFrom) {
		super(ref);
		this.source = thrownFrom;
	}

	@Override
	public void getExtendedInformation() {
		assert Debugger.addNode("A syntax error has occurred here, or near here, and the script is unparseable (Thrown from: " + this.source + ")");
	}

	@Override
	public String getName() {
		return "Unparseable Element";
	}
}