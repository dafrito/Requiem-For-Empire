package com.dafrito.rfe.script.values;

import com.dafrito.rfe.script.ScriptEnvironment;
import com.dafrito.rfe.script.exceptions.ScriptException;
import com.dafrito.rfe.script.parsing.Referenced;

public interface ScriptValue {
	public ScriptValue castToType(Referenced ref, ScriptValueType type) throws ScriptException;

	public ScriptEnvironment getEnvironment();

	public ScriptValueType getType();

	public ScriptValue getValue() throws ScriptException;

	public boolean isConvertibleTo(ScriptValueType type);

	public ScriptValue setValue(Referenced ref, ScriptValue value) throws ScriptException;

	public int valuesCompare(Referenced ref, ScriptValue rhs) throws ScriptException;

	public boolean valuesEqual(Referenced ref, ScriptValue rhs) throws ScriptException;
}
