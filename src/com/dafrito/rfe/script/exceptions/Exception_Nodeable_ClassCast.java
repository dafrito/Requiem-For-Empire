/**
 * 
 */
package com.dafrito.rfe.script.exceptions;

import com.dafrito.rfe.script.parsing.Referenced;
import com.dafrito.rfe.script.values.ScriptValue;
import com.dafrito.rfe.script.values.ScriptValueType;

public class Exception_Nodeable_ClassCast extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 375067664989244754L;
	private String value, castingValue;

	public Exception_Nodeable_ClassCast(Referenced ref, ScriptValue value, ScriptValue castValue) throws Exception_Nodeable {
		this(ref, value.getType().getName(), castValue.getType().getName());
	}

	public Exception_Nodeable_ClassCast(Referenced ref, ScriptValue castingValue, ScriptValueType type) throws Exception_Nodeable {
		this(ref, type.getName(), type.getName());
	}

	public Exception_Nodeable_ClassCast(Referenced ref, String type, String castType) {
		super(ref.getEnvironment(), ref);
		this.value = type;
		this.castingValue = castType;
	}

	@Override
	public String getName() {
		return "Casting Exception - Invalid conversion: " + this.castingValue + " --> " + this.value;
	}
}