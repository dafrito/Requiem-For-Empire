package com.dafrito.rfe.script;

import com.dafrito.rfe.debug.Debugger;
import com.dafrito.rfe.inspect.Nodeable;
import com.dafrito.rfe.script.exceptions.Exception_Nodeable;

public class ScriptExecutable_RetrieveCurrentObject extends ScriptValue_Variable implements ScriptExecutable, ScriptValue, Nodeable, Referenced {
	private ScriptElement reference;

	public ScriptExecutable_RetrieveCurrentObject(Referenced ref, ScriptValueType type) throws Exception_Nodeable {
		super(ref.getEnvironment(), type, null);
		this.reference = ref.getDebugReference();
	}

	@Override
	public ScriptValue castToType(Referenced ref, ScriptValueType type) throws Exception_Nodeable {
		return this.getVariable().castToType(ref, type);
	}

	// ScriptExecutable implementation
	@Override
	public ScriptValue execute() throws Exception_Nodeable {
		return this.getValue();
	}

	// Referenced implementation
	@Override
	public ScriptElement getDebugReference() {
		return this.reference;
	}

	@Override
	public ScriptEnvironment getEnvironment() {
		return this.getDebugReference().getEnvironment();
	}

	// Overloaded ScriptValue_Variable functions
	@Override
	public ScriptKeywordType getPermission() throws Exception_Nodeable {
		return ScriptKeywordType.PRIVATE;
	}

	@Override
	public ScriptValue getValue() throws Exception_Nodeable {
		return this.getVariable().getValue();
	}

	public ScriptValue_Variable getVariable() throws Exception_Nodeable {
		assert Debugger.addNode("Executing Current Object Retrieval");
		return new ScriptValue_Variable(this.getEnvironment(), this.getType(), this.getEnvironment().getCurrentObject(), this.getPermission());
	}

	// Abstract-value implementation
	@Override
	public boolean isConvertibleTo(ScriptValueType type) {
		return ScriptValueType.isConvertibleTo(this.getEnvironment(), this.getType(), type);
	}

	@Override
	public void nodificate() {
		assert Debugger.openNode("Current Object Placeholder");
		super.nodificate();
		assert Debugger.closeNode();
	}

	@Override
	public ScriptValue setReference(Referenced ref, ScriptValue value) throws Exception_Nodeable {
		return this.getVariable().setReference(ref, value);
	}

	@Override
	public ScriptValue setValue(Referenced ref, ScriptValue value) throws Exception_Nodeable {
		return this.getVariable().setValue(ref, value);
	}

	@Override
	public int valuesCompare(Referenced ref, ScriptValue rhs) throws Exception_Nodeable {
		return this.getValue().valuesCompare(ref, rhs);
	}

	@Override
	public boolean valuesEqual(Referenced ref, ScriptValue rhs) throws Exception_Nodeable {
		return this.getValue().valuesEqual(ref, rhs);
	}
}
