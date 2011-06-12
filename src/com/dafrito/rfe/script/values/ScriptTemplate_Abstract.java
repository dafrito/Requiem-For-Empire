package com.dafrito.rfe.script.values;

import java.util.Collections;
import java.util.List;

import com.dafrito.rfe.gui.debug.Debugger;
import com.dafrito.rfe.inspect.Nodeable;
import com.dafrito.rfe.script.ScriptEnvironment;
import com.dafrito.rfe.script.exceptions.Exception_Nodeable_ClassCast;
import com.dafrito.rfe.script.exceptions.ScriptException;
import com.dafrito.rfe.script.operations.ScriptExecutable;
import com.dafrito.rfe.script.parsing.Referenced;

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

	public abstract void addFunction(Referenced ref, String name, ScriptFunction function) throws ScriptException;

	public abstract void addPreconstructorExpression(ScriptExecutable exec) throws ScriptException;

	public abstract void addTemplatePreconstructorExpression(ScriptExecutable exec) throws ScriptException;

	public abstract ScriptValue_Variable addVariable(Referenced ref, String name, ScriptValue_Variable value) throws ScriptException;

	@Override
	public ScriptValue castToType(Referenced ref, ScriptValueType type) throws ScriptException {
		if (this.isConvertibleTo(type)) {
			return this;
		}
		throw new Exception_Nodeable_ClassCast(ref, this, type);
	}

	public abstract ScriptTemplate_Abstract createObject(Referenced ref, ScriptTemplate_Abstract object) throws ScriptException;

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

	public abstract ScriptFunction getFunction(String name, List<ScriptValue> params);

	public abstract List<ScriptFunction> getFunctions();

	public abstract ScriptTemplate_Abstract getFunctionTemplate(ScriptFunction fxn);

	public List<ScriptValueType> getInterfaces() {
		if (this.getEnvironment().getTemplate(this.getType()) != null && this.getEnvironment().getTemplate(this.getType()) != this) {
			return this.getEnvironment().getTemplate(this.getType()).getInterfaces();
		}
		return Collections.unmodifiableList(this.interfaces);
	}

	public abstract ScriptValue_Variable getStaticReference() throws ScriptException;

	@Override
	public ScriptValueType getType() {
		return this.type;
	}

	@Override
	public ScriptValue getValue() throws ScriptException {
		return this;
	}

	public abstract ScriptValue_Variable getVariable(String name) throws ScriptException;

	public abstract void initialize() throws ScriptException;

	public abstract void initializeFunctions(Referenced ref) throws ScriptException;

	public abstract ScriptTemplate instantiateTemplate();

	public abstract boolean isAbstract() throws ScriptException;

	public abstract boolean isConstructing() throws ScriptException;

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

	public abstract void setConstructing(boolean constructing) throws ScriptException;

	@Override
	public ScriptValue setValue(Referenced ref, ScriptValue value) throws ScriptException {
		throw new UnsupportedOperationException("Templates have no inherent value, and thus their value cannot be set directly.");
	}

	@Override
	public int valuesCompare(Referenced ref, ScriptValue rhs) throws ScriptException {
		throw new UnsupportedOperationException("Templates have no inherent value, and thus cannot be compared.");
	}

	@Override
	public boolean valuesEqual(Referenced ref, ScriptValue rhs) throws ScriptException {
		return (this == rhs);
	}
}
