/**
 * 
 */
package com.dafrito.rfe.script.exceptions;

import com.dafrito.rfe.logging.Logs;
import com.dafrito.rfe.script.parsing.Referenced;
import com.dafrito.rfe.script.values.ScriptFunction;
import com.dafrito.rfe.script.values.ScriptTemplate_Abstract;

public class AbstractFunctionNotImplementedScriptException extends ScriptException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8937024716489715221L;
	private ScriptTemplate_Abstract object;
	private ScriptFunction function;

	public AbstractFunctionNotImplementedScriptException(Referenced ref, ScriptTemplate_Abstract object, ScriptFunction function) {
		super(ref);
		this.object = object;
		this.function = function;
	}

	@Override
	public void getExtendedInformation() {
		assert Logs.addSnapNode("The template is not abstract and does not implement the following function", this.object);
		assert Logs.addNode(this.function);
	}

	@Override
	public String getName() {
		return "Abstract Function Not Implememented";
	}
}