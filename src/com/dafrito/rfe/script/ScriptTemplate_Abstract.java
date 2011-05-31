package com.dafrito.rfe.script;
import java.util.Collections;
import java.util.List;

import com.dafrito.rfe.Debugger;
import com.dafrito.rfe.Exception_InternalError;
import com.dafrito.rfe.inspect.Nodeable;
import com.dafrito.rfe.script.exceptions.Exception_Nodeable;
import com.dafrito.rfe.script.exceptions.Exception_Nodeable_ClassCast;


public abstract class ScriptTemplate_Abstract implements ScriptValue, Nodeable {
	private final ScriptEnvironment environment;
	private final ScriptValueType type;
	private ScriptValueType extended;
	private List<ScriptValueType> interfaces;

	public ScriptTemplate_Abstract(ScriptEnvironment env, ScriptValueType type) {
		this.environment = env;
		this.type = type;
	}

	public ScriptTemplate_Abstract(ScriptEnvironment env, ScriptValueType type, ScriptValueType extended, List<ScriptValueType> interfaces) {
		this.environment = env;
		this.type = type;
		this.extended = extended;
		this.interfaces = interfaces;
	}

	public abstract void addFunction(Referenced ref, String name, ScriptFunction_Abstract function) throws Exception_Nodeable;

	public abstract void addPreconstructorExpression(ScriptExecutable exec) throws Exception_Nodeable;

	public abstract void addTemplatePreconstructorExpression(ScriptExecutable exec) throws Exception_Nodeable;

	public abstract ScriptValue_Variable addVariable(Referenced ref, String name, ScriptValue_Variable value) throws Exception_Nodeable;

	@Override
	public ScriptValue castToType(Referenced ref, ScriptValueType type) throws Exception_Nodeable {
		if (this.isConvertibleTo(type)) {
			return this;
		}
		throw new Exception_Nodeable_ClassCast(ref, this, type);
	}

	public abstract ScriptTemplate_Abstract createObject(Referenced ref, ScriptTemplate_Abstract object) throws Exception_Nodeable;

	public abstract void disableFullCreation();

	// Abstract-value implementation
	@Override
	public ScriptEnvironment getEnvironment() {
		return this.environment;
	}

	public ScriptTemplate_Abstract getExtendedClass() {
		assert this.getEnvironment() != null : "Environment is null: " + this;
		if (this.getEnvironment().getTemplate(this.getType()) != null && this.getEnvironment().getTemplate(this.getType()) != this) {
			return this.getEnvironment().getTemplate(this.getType()).getExtendedClass();
		}
		if (this.extended == null) {
			return null;
		}
		assert this.getEnvironment().getTemplate(this.extended) != null : "Extended class is null!";
		return this.getEnvironment().getTemplate(this.extended);
	}

	public abstract ScriptFunction_Abstract getFunction(String name, List<ScriptValue> params);

	public abstract List<ScriptFunction_Abstract> getFunctions();

	public abstract ScriptTemplate_Abstract getFunctionTemplate(ScriptFunction_Abstract fxn);

	public List<ScriptValueType> getInterfaces() {
		if (this.getEnvironment().getTemplate(this.getType()) != null && this.getEnvironment().getTemplate(this.getType()) != this) {
			return this.getEnvironment().getTemplate(this.getType()).getInterfaces();
		}
		return Collections.unmodifiableList(this.interfaces);
	}

	public abstract ScriptValue_Variable getStaticReference() throws Exception_Nodeable;

	@Override
	public ScriptValueType getType() {
		return this.type;
	}

	@Override
	public ScriptValue getValue() throws Exception_Nodeable {
		return this;
	}

	public abstract ScriptValue_Variable getVariable(String name) throws Exception_Nodeable;

	public abstract void initialize() throws Exception_Nodeable;

	public abstract void initializeFunctions(Referenced ref) throws Exception_Nodeable;

	public abstract ScriptTemplate instantiateTemplate();

	public abstract boolean isAbstract() throws Exception_Nodeable;

	public abstract boolean isConstructing() throws Exception_Nodeable;

	@Override
	public boolean isConvertibleTo(ScriptValueType type) {
		if (this.getEnvironment().getTemplate(this.getType()) != null && this.getEnvironment().getTemplate(this.getType()) != this) {
			return this.getEnvironment().getTemplate(this.getType()).isConvertibleTo(type);
		}
		assert Debugger.openNode("Value Type Match Test", "Checking for Type-Match (" + this.getType() + " to " + type + ")");
		assert Debugger.addNode(this);
		if (this.getType().equals(type)) {
			assert Debugger.closeNode("Direct match.");
			return true;
		}
		if (this.getInterfaces() != null) {
			for (ScriptValueType scriptInterface : this.getInterfaces()) {
				if (ScriptValueType.isConvertibleTo(this.getEnvironment(), scriptInterface, type)) {
					assert Debugger.closeNode("Interface type match.");
					return true;
				}
			}
		}
		assert Debugger.openNode("No type match, checking extended classes for match.");
		if (this.getExtendedClass() != null && this.getExtendedClass().isConvertibleTo(type)) {
			assert Debugger.closeNode();
			assert Debugger.closeNode();
			return true;
		}
		assert Debugger.closeNode();
		assert Debugger.closeNode();
		return false;
	}

	// Abstract-template implementation
	public abstract boolean isFullCreation();

	public abstract boolean isObject();

	// Remaining unimplemented ScriptValue_Abstract functions
	@Override
	public abstract void nodificate();

	public abstract void setConstructing(boolean constructing) throws Exception_Nodeable;

	@Override
	public ScriptValue setValue(Referenced ref, ScriptValue value) throws Exception_Nodeable {
		throw new Exception_InternalError("Templates have no inherent value, and thus their value cannot be set directly.");
	}

	@Override
	public int valuesCompare(Referenced ref, ScriptValue rhs) throws Exception_Nodeable {
		throw new Exception_InternalError("Templates have no inherent value, and thus cannot be compared.");
	}

	@Override
	public boolean valuesEqual(Referenced ref, ScriptValue rhs) throws Exception_Nodeable {
		return (this == rhs);
	}
}
