/**
 * 
 */
package com.dafrito.rfe.script.exceptions;

import java.util.List;

import com.dafrito.rfe.gui.debug.Debugger;
import com.dafrito.rfe.script.ScriptEnvironment;
import com.dafrito.rfe.script.parsing.Referenced;
import com.dafrito.rfe.script.values.ScriptFunction;

public class Exception_Nodeable_FunctionNotFound extends Exception_Nodeable {
	private static final long serialVersionUID = 4051248649703169850L;
	private String name;
	private List<?> params;

	public Exception_Nodeable_FunctionNotFound(Object ref, String name, List<?> params) {
		this(((Referenced) ref).getEnvironment(), ref, name, params);
	}

	public Exception_Nodeable_FunctionNotFound(ScriptEnvironment env, Object ref, String name, List<?> params) {
		super(env, ref);
		this.name = name;
		this.params = params;
	}

	public Exception_Nodeable_FunctionNotFound(ScriptEnvironment env, String name, List<?> params) {
		super(env);
		this.name = name;
		this.params = params;
	}

	@Override
	public void getExtendedInformation() {
		assert Debugger.addNode("The function, " + ScriptFunction.getDisplayableFunctionName(this.name) + ", was not found");
	}

	@Override
	public String getName() {
		return "Function not found (" + ScriptFunction.getDisplayableFunctionName(this.name) + ")";
	}

	public List<?> getParams() {
		return this.params;
	}
}