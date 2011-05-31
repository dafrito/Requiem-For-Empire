/**
 * 
 */
package com.dafrito.rfe.script.exceptions;

import com.dafrito.rfe.Debugger;
import com.dafrito.rfe.script.Referenced;

public class Exception_Nodeable_IllegalAbstractObjectCreation extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2947890390494988260L;

	public Exception_Nodeable_IllegalAbstractObjectCreation(Referenced ref) {
		super(ref);
	}

	@Override
	public void getExtendedInformation() {
		assert Debugger.addNode("An abstract object is trying to be instantiated.");
	}

	@Override
	public String getName() {
		return "Illegal Abstract Object Creation";
	}
}