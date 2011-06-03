package com.dafrito.rfe.script.values;

import com.dafrito.rfe.script.ScriptEnvironment;
import com.dafrito.rfe.script.exceptions.Exception_Nodeable;
import com.dafrito.rfe.script.parsing.Referenced;

public interface ScriptValue {
	public ScriptValue castToType(Referenced ref, ScriptValueType type) throws Exception_Nodeable;

	// ScriptValue_Abstract implementation
	public ScriptEnvironment getEnvironment();

	public ScriptValueType getType();

	public ScriptValue getValue() throws Exception_Nodeable;

	public boolean isConvertibleTo(ScriptValueType type);

	public ScriptValue setValue(Referenced ref, ScriptValue value) throws Exception_Nodeable;

	public int valuesCompare(Referenced ref, ScriptValue rhs) throws Exception_Nodeable;

	public boolean valuesEqual(Referenced ref, ScriptValue rhs) throws Exception_Nodeable;
}
