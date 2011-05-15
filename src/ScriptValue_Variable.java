public class ScriptValue_Variable implements ScriptValue_Abstract, Nodeable {
	private ScriptValue_Abstract m_value;
	private final ScriptKeywordType m_permission;
	private final ScriptEnvironment m_environment;
	private final ScriptValueType m_type;

	public ScriptValue_Variable(ScriptEnvironment env, ScriptValue_Abstract value, ScriptKeywordType permission) throws Exception_Nodeable {
		this(env, value.getType(), value, permission);
	}

	public ScriptValue_Variable(ScriptEnvironment env, ScriptValueType type, ScriptKeywordType permission) throws Exception_Nodeable {
		this(env, type, null, permission);
	}

	public ScriptValue_Variable(ScriptEnvironment env, ScriptValueType type, ScriptValue_Abstract value, ScriptKeywordType permission) throws Exception_Nodeable {
		this.m_environment = env;
		this.m_permission = permission;
		this.m_type = type;
		if (value == null) {
			this.m_value = ScriptValue.createUninitializedObject(env, type);
		} else {
			this.m_value = value;
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
		return this.m_environment;
	}

	public ScriptKeywordType getPermission() throws Exception_Nodeable {
		return this.m_permission;
	}

	@Override
	public ScriptValueType getType() {
		return this.m_type;
	}

	@Override
	public ScriptValue_Abstract getValue() throws Exception_Nodeable {
		assert Debugger.openNode("Variable Value Retrievals", "Retrieving Variable's Value");
		assert Debugger.addNode(this);
		ScriptValue_Abstract returning;
		if (this.m_value != null) {
			returning = this.m_value.getValue();
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
		if (this.m_value != null) {
			assert Debugger.addSnapNode("Referenced element (" + this.m_value.getType() + ")", this.m_value);
		} else {
			assert Debugger.addNode(DebugString.REFERENCEDELEMENTNULL);
		}
		if (this.m_permission == null) {
			Debugger.addNode(DebugString.PERMISSIONNULL);
		} else {
			switch (this.m_permission) {
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
				this.m_value = null;
			} else {
				this.m_value = value.castToType(ref, this.getType());
			}
			assert Debugger.closeNode("Reference assignment operation completed", this);
			return this.m_value;
		}
		assert Debugger.openNode("Assigning value...");
		this.m_value = this.m_value.setValue(ref, value.castToType(ref, this.getType()));
		assert Debugger.closeNode();
		assert Debugger.closeNode("Value assignment operation completed", this);
		return this.m_value;
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
		if (this.m_value == null || this.m_value.getValue() == null || this.m_value.getValue() instanceof ScriptValue_Null) {
			return (rhs == null || rhs instanceof ScriptValue_Null);
		}
		return this.getValue().valuesEqual(ref, rhs);
	}
}
