package com.dafrito.rfe.script;

import com.dafrito.rfe.Debugger;
import com.dafrito.rfe.inspect.Nodeable;
import com.dafrito.rfe.script.exceptions.Exception_InternalError;
import com.dafrito.rfe.script.exceptions.Exception_Nodeable;
import com.dafrito.rfe.script.exceptions.Exception_Nodeable_IncomparableObjects;

public class ScriptValue_Null extends ScriptElement implements ScriptExecutable, ScriptValue, Nodeable {
	public ScriptValue_Null(Referenced ref) {
		super(ref);
	}

	@Override
	public ScriptValue castToType(Referenced ref, ScriptValueType type) throws Exception_Nodeable {
		return this;
	}

	@Override
	public ScriptValue execute() throws Exception_Nodeable {
		return this;
	}

	// ScriptValue_Abstract implementation
	@Override
	public ScriptValueType getType() {
		return null;
	}

	@Override
	public ScriptValue getValue() throws Exception_Nodeable {
		return this;
	}

	@Override
	public boolean isConvertibleTo(ScriptValueType type) {
		return true;
	}

	@Override
	public void nodificate() {
		assert Debugger.addNode("Null Script-Value");
	}

	@Override
	public ScriptValue setValue(Referenced ref, ScriptValue value) throws Exception_Nodeable {
		throw new Exception_InternalError("Set Value");
	}

	@Override
	public int valuesCompare(Referenced ref, ScriptValue rhs) throws Exception_Nodeable {
		throw new Exception_Nodeable_IncomparableObjects(ref, this, rhs);
	}

	@Override
	public boolean valuesEqual(Referenced ref, ScriptValue rhs) throws Exception_Nodeable {
		return (rhs == null || rhs.getValue() == null || rhs.getValue() instanceof ScriptValue_Null);
	}
}
