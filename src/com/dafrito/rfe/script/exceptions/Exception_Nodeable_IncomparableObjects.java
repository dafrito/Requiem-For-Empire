/**
 * 
 */
package com.dafrito.rfe.script.exceptions;

import com.dafrito.rfe.gui.debug.Debugger;
import com.dafrito.rfe.script.parsing.Referenced;
import com.dafrito.rfe.script.values.ScriptValue;

public class Exception_Nodeable_IncomparableObjects extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1863802775557161344L;
	private ScriptValue lhs, rhs;

	public Exception_Nodeable_IncomparableObjects(Referenced ref, ScriptValue lhs, ScriptValue rhs) {
		super(ref);
		this.lhs = lhs;
		this.rhs = rhs;
	}

	@Override
	public void getExtendedInformation() {
		assert Debugger.addNode("The following two objects/primitives are incomparable.");
		assert Debugger.addNode(this.lhs);
		assert Debugger.addNode(this.rhs);
	}

	@Override
	public String getName() {
		return "Incomparable Objects Exception";
	}
}