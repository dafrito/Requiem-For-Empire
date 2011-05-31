package com.dafrito.rfe.script;

import com.dafrito.rfe.debug.Debugger;
import com.dafrito.rfe.inspect.Nodeable;
import com.dafrito.rfe.script.exceptions.Exception_Nodeable;

public class ScriptExecutable_CreateVariable extends ScriptValue_Variable implements ScriptValue, ScriptExecutable, Nodeable, Referenced {
	private ScriptKeywordType permission;
	private String name;
	private ScriptElement reference;

	public ScriptExecutable_CreateVariable(Referenced ref, ScriptValueType type, String name, ScriptKeywordType permission) throws Exception_Nodeable {
		super(ref.getEnvironment(), type, null);
		this.reference = ref.getDebugReference();
		this.name = name;
		this.permission = permission;
	}

	@Override
	public ScriptValue castToType(Referenced ref, ScriptValueType type) throws Exception_Nodeable {
		return this.getValue().castToType(ref, type);
	}

	// ScriptExecutable implementation
	@Override
	public ScriptValue execute() throws Exception_Nodeable {
		assert Debugger.openNode("Creating Variable (" + this.name + ")");
		ScriptValue_Variable value;
		this.getEnvironment().getCurrentObject().addVariable(this, this.name, value = new ScriptValue_Variable(this.getEnvironment(), this.getType(), this.getPermission()));
		assert Debugger.addSnapNode("Variable Created", value);
		assert Debugger.closeNode();
		return value;
	}

	// Referenced implementation
	@Override
	public ScriptElement getDebugReference() {
		return this.reference;
	}

	// ScriptValue_Abstract implementation
	@Override
	public ScriptEnvironment getEnvironment() {
		return this.getDebugReference().getEnvironment();
	}

	public String getName() {
		return this.name;
	}

	// Overloaded ScriptValue_Variable functions
	@Override
	public ScriptKeywordType getPermission() throws Exception_Nodeable {
		return this.permission;
	}

	@Override
	public ScriptValue getValue() throws Exception_Nodeable {
		return this.execute().getValue();
	}

	@Override
	public boolean isConvertibleTo(ScriptValueType type) {
		return ScriptValueType.isConvertibleTo(this.getEnvironment(), this.getType(), type);
	}

	// Nodeable implementation
	@Override
	public void nodificate() {
		assert Debugger.openNode("Variable-Creation Script-Element (" + this.name + ")");
		super.nodificate();
		assert Debugger.closeNode();
	}

	@Override
	public ScriptValue setReference(Referenced ref, ScriptValue value) throws Exception_Nodeable {
		return ((ScriptValue_Variable) this.execute()).setReference(ref, value);
	}

	@Override
	public ScriptValue setValue(Referenced ref, ScriptValue value) throws Exception_Nodeable {
		return this.execute().setValue(ref, value);
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
