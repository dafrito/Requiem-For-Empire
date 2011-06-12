/**
 * 
 */
package com.dafrito.rfe.script.exceptions;

import com.dafrito.rfe.gui.debug.Debugger;
import com.dafrito.rfe.script.parsing.Referenced;
import com.dafrito.rfe.script.values.ScriptTemplate_Abstract;

class Exception_Nodeable_InvalidAbstractFunctionCall extends ScriptException {
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