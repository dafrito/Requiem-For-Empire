/**
 * 
 */
package com.dafrito.rfe.script.exceptions;

import com.dafrito.rfe.logging.Logs;
import com.dafrito.rfe.script.parsing.Referenced;

public class UnparseableElementException extends ScriptException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7815806790954972711L;
	private String source;

	public UnparseableElementException(Referenced ref, String thrownFrom) {
		super(ref);
		this.source = thrownFrom;
	}

	@Override
	public void getExtendedInformation() {
		assert Logs.addNode("A syntax error has occurred here, or near here, and the script is unparseable (Thrown from: " + this.source + ")");
	}

	@Override
	public String getName() {
		return "Unparseable Element";
	}
}