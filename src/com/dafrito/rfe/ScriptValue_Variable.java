package com.dafrito.rfe;

public class ScriptValue_Variable implements ScriptValue_Abstract, Nodeable {
	private ScriptValue_Abstract value;
	private final ScriptKeywordType permission;
	private final ScriptEnvironment environment;
	private final ScriptValueType type;

	public ScriptValue_Variable(ScriptEnvironment env, ScriptValue_Abstract value, ScriptKeywordType permission) throws Exception_Nodeable {
		this(env, value.getType(), value, permission);
	}

	public ScriptValue_Variable(ScriptEnvironment env, ScriptValueType type, ScriptKeywordType permission) throws Exception_Nodeable {
		this(env, type, null, permission);
	}

	public ScriptValue_Variable(ScriptEnvironment env, ScriptValueType type, ScriptValue_Abstract value, ScriptKeywordType permission) throws Exception_Nodeable {
		this.environment = env;
		this.permission = permission;
		this.type = type;
		if (value == null) {
			this.value = ScriptValue.createUninitializedObject(env, type);
		} else {
			this.value = value;
		}
	}

	@Override
	public ScriptValue_Abstract castToType(Referenced ref, ScriptValueType type) throws Exception_Nodeable {
		if (this.isConvertibleTo(type)) {
			return new ScriptValue_Variable(this.getEnvironment(), this.getType(), this.getValue(), this.getPermission());
		}
		throw new Exception_Nodeable_ClassCast(ref, this, type);
	}

	// Abstract-value implementation
	@Override
	public ScriptEnvironment getEnvironment() {
		return this.environment;
	}

	public ScriptKeywordType getPermission() throws Exception_Nodeable {
		return this.permission;
	}

	@Override
	public ScriptValueType getType() {
		return this.type;
	}

	@Override
	public ScriptValue_Abstract getValue() throws Exception_Nodeable {
		assert Debugger.openNode("Variable Value Retrievals", "Retrieving Variable's Value");
		assert Debugger.addNode(this);
		ScriptValue_Abstract returning;
		if (this.value != null) {
			returning = this.value.getValue();
		} else {
			returning = null;
		}
		assert Debugger.addSnapNode("Value", returning);
		assert Debugger.closeNode();
		return returning;
	}

	@Override
	public boolean isConvertibleTo(ScriptValueType type) {
		return ScriptValueType.isConvertibleTo(this.getEnvironment(), this.getType(), type);
	}

	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Script Variable (" + this.getType() + ")");
		if (this.value != null) {
			assert Debugger.addSnapNode("Referenced element (" + this.value.getType() + ")", this.value);
		} else {
			assert Debugger.addNode(DebugString.REFERENCEDELEMENTNULL);
		}
		if (this.permission == null) {
			Debugger.addNode(DebugString.PERMISSIONNULL);
		} else {
			switch (this.permission) {
			case PRIVATE:
				assert Debugger.addNode(DebugString.PERMISSIONPRIVATE);
				break;
			case PROTECTED:
				Debugger.addNode(DebugString.PERMISSIONPROTECTED);
			case PUBLIC:
				Debugger.addNode(DebugString.PERMISSIONPUBLIC);
			}
		}
		assert Debugger.closeNode();
		return true;
	}

	public ScriptValue_Abstract setReference(Referenced ref, ScriptValue_Abstract value) throws Exception_Nodeable {
		assert Debugger.openNode("Reference Assignments", "Setting Variable Reference");
		if (!ScriptValueType.isPrimitiveType(this.getType())) {
			assert Debugger.addNode("Assigning reference");
			assert Debugger.addSnapNode("Variable", this);
			assert Debugger.openNode("Retrieving value");
			value = value.getValue();
			assert Debugger.closeNode("Value", value);
			if (value == null) {
				this.value = null;
			} else {
				this.value = value.castToType(ref, this.getType());
			}
			assert Debugger.closeNode("Reference assignment operation completed", this);
			return this.value;
		}
		assert Debugger.openNode("Assigning value...");
		this.value = this.value.setValue(ref, value.castToType(ref, this.getType()));
		assert Debugger.closeNode();
		assert Debugger.closeNode("Value assignment operation completed", this);
		return this.value;
	}

	@Override
	public ScriptValue_Abstract setValue(Referenced ref, ScriptValue_Abstract value) throws Exception_Nodeable {
		return this.setReference(ref, value);
	}

	@Override
	public int valuesCompare(Referenced ref, ScriptValue_Abstract rhs) throws Exception_Nodeable {
		return this.getValue().valuesCompare(ref, rhs);
	}

	@Override
	public boolean valuesEqual(Referenced ref, ScriptValue_Abstract rhs) throws Exception_Nodeable {
		if (this.value == null || this.value.getValue() == null || this.value.getValue() instanceof ScriptValue_Null) {
			return (rhs == null || rhs instanceof ScriptValue_Null);
		}
		return this.getValue().valuesEqual(ref, rhs);
	}
}
