/**
 * 
 */
package com.dafrito.rfe.script.exceptions;

import com.dafrito.rfe.Debugger;
import com.dafrito.rfe.Referenced;
import com.dafrito.rfe.script.ScriptTemplate_Abstract;

class Exception_Nodeable_InvalidAbstractFunctionCall extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7118648667674833605L;
	private ScriptTemplate_Abstract template;

	public Exception_Nodeable_InvalidAbstractFunctionCall(Referenced ref, ScriptTemplate_Abstract template) {
		super(ref);
		this.template = template;
	}

	@Override
	public void getExtendedInformation() {
		assert Debugger.addSnapNode("A call was made to an abstract function in this template", this.template);
	}

	@Override
	public String getName() {
		return "Invalid Abstract Function Call";
	}
}