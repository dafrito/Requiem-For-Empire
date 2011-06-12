/**
 * 
 */
package com.dafrito.rfe.script.exceptions;

import com.dafrito.rfe.script.ScriptEnvironment;

class Exception_Nodeable_FileNotFound extends ScriptException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2126754108417225024L;
	private String name;

	public Exception_Nodeable_FileNotFound(ScriptEnvironment env, String name) {
		super(env);
		this.name = name;
	}

	@Override
	public String getName() {
		return "File Not Found (" + this.name + ")";
	}
}