package com.dafrito.rfe.script.values;

import com.dafrito.rfe.gui.debug.CommonString;
import com.dafrito.rfe.gui.debug.Debugger;
import com.dafrito.rfe.inspect.Nodeable;
import com.dafrito.rfe.script.ScriptEnvironment;
import com.dafrito.rfe.script.exceptions.ScriptException;
import com.dafrito.rfe.script.exceptions.Exception_Nodeable_ClassCast;
import com.dafrito.rfe.script.parsing.Referenced;
import com.dafrito.rfe.script.parsing.ScriptKeywordType;

public class ScriptValue_Variable implements ScriptValue, Nodeable {

	public static ScriptValue createUninitializedObject(ScriptEnvironment env, ScriptValueType type) {
		if (type == null) {
			return null;
		}
		if (type.equals(ScriptKeywordType.BOOLEAN)) {
			return new ScriptValue_Boolean(env, false);
		} else if (type.equals(ScriptKeywordType.SHORT)) {
			return new ScriptValue_Numeric(env, (short) 0);
		} else if (type.equals(ScriptKeywordType.INT)) {
			return new ScriptValue_Numeric(env, 0);
		} else if (type.equals(ScriptKeywordType.LONG)) {
			return new ScriptValue_Numeric(env, (long) 0);
		} else if (type.equals(ScriptKeywordType.FLOAT)) {
			return new ScriptValue_Numeric(env, 0.0f);
		} else if (type.equals(ScriptKeywordType.DOUBLE)) {
			return new ScriptValue_Numeric(env, 0.0d);
		} else if (type.equals(ScriptKeywordType.STRING)) {
			return new ScriptValue_String(env, "");
		} else {
			return null;
		}
	}

	private ScriptValue value;
	private final ScriptKeywordType permission;
	private final ScriptEnvironment environment;
	private final ScriptValueType type;

	public ScriptValue_Variable(ScriptEnvironment env, ScriptValue value, ScriptKeywordType permission) throws ScriptException {
		this(env, value.getType(), value, permission);
	}

	public ScriptValue_Variable(ScriptEnvironment env, ScriptValueType type, ScriptKeywordType permission) throws ScriptException {
		this(env, type, null, permission);
	}

	public ScriptValue_Variable(ScriptEnvironment env, ScriptValueType type, ScriptValue value, ScriptKeywordType permission) throws ScriptException {
		this.environment = env;
		this.permission = permission;
		if (this.permission == null) {
			throw new NullPointerException("permission must not be null");
		}
		switch (this.permission) {
		case PRIVATE:
		case PROTECTED:
		case PUBLIC:
			break;
		default:
			throw new IllegalArgumentException("Permission must be PUBLIC, PROTECTED, or PRIVATE");
		}
		this.type = type;
		if (value == null) {
			this.value = createUninitializedObject(env, type);
		} else {
			this.value = value;
		}
	}

	@Override
	public ScriptValue castToType(Referenced ref, ScriptValueType type) throws ScriptException {
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

	public ScriptKeywordType getPermission() throws ScriptException {
		return this.permission;
	}

	@Override
	public ScriptValueType getType() {
		return this.type;
	}

	@Override
	public ScriptValue getValue() throws ScriptException {
		assert Debugger.openNode("Variable Value Retrievals", "Retrieving Variable's Value");
		assert Debugger.addNode(this);
		ScriptValue returning;
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
	public void nodificate() {
		assert Debugger.openNode("Script Variable (" + this.getType() + ")");
		if (this.value != null) {
			assert Debugger.addSnapNode("Referenced element (" + this.value.getType() + ")", this.value);
		} else {
			assert Debugger.addNode(CommonString.REFERENCEDELEMENTNULL);
		}
		if (this.permission == null) {
			Debugger.addNode(CommonString.PERMISSIONNULL);
		} else {
			switch (this.permission) {
			case PRIVATE:
				assert Debugger.addNode(CommonString.PERMISSIONPRIVATE);
				break;
			case PROTECTED:
				Debugger.addNode(CommonString.PERMISSIONPROTECTED);
				break;
			case PUBLIC:
				Debugger.addNode(CommonString.PERMISSIONPUBLIC);
				break;
			default:
				throw new AssertionError("Unexpected permission");
			}
		}
		assert Debugger.closeNode();
	}

	public ScriptValue setReference(Referenced ref, ScriptValue value) throws ScriptException {
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
	public ScriptValue setValue(Referenced ref, ScriptValue value) throws ScriptException {
		return this.setReference(ref, value);
	}

	@Override
	public int valuesCompare(Referenced ref, ScriptValue rhs) throws ScriptException {
		return this.getValue().valuesCompare(ref, rhs);
	}

	@Override
	public boolean valuesEqual(Referenced ref, ScriptValue rhs) throws ScriptException {
		if (this.value == null || this.value.getValue() == null || this.value.getValue() instanceof ScriptValue_Null) {
			return (rhs == null || rhs instanceof ScriptValue_Null);
		}
		return this.getValue().valuesEqual(ref, rhs);
	}
}
