/**
 * 
 */
package com.dafrito.rfe.script.exceptions;

import com.dafrito.rfe.logging.Logs;
import com.dafrito.rfe.script.parsing.Referenced;
import com.dafrito.rfe.script.values.ScriptValue;

public class Exception_Nodeable_IncomparableObjects extends ScriptException {
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
		assert Logs.addNode("The following two objects/primitives are incomparable.");
		assert Logs.addNode(this.lhs);
		assert Logs.addNode(this.rhs);
	}

	@Override
	public String getName() {
		return "Incomparable Objects Exception";
	}
}