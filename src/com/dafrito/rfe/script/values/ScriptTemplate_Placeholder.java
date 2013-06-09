package com.dafrito.rfe.script.values;

import java.util.List;

import com.dafrito.rfe.inspect.Nodeable;
import com.dafrito.rfe.logging.Logs;
import com.dafrito.rfe.script.ScriptEnvironment;
import com.dafrito.rfe.script.exceptions.InternalException;
import com.dafrito.rfe.script.exceptions.ScriptException;
import com.dafrito.rfe.script.operations.ScriptExecutable;
import com.dafrito.rfe.script.parsing.Referenced;

public class ScriptTemplate_Placeholder extends ScriptTemplate_Abstract implements ScriptValue, Nodeable {
	private String name;

	public ScriptTemplate_Placeholder(ScriptEnvironment env, String name) {
		super(env, null, null, null);
		this.name = name;
	}

	@Override
	public void addFunction(Referenced ref, String name, ScriptFunction function) throws ScriptException {
		this.getTemplate().addFunction(ref, name, function);
	}

	@Override
	public void addPreconstructorExpression(ScriptExecutable exec) throws ScriptException {
		this.getTemplate().addPreconstructorExpression(exec);
	}

	@Override
	public void addTemplatePreconstructorExpression(ScriptExecutable exec) throws ScriptException {
		this.getTemplate().addTemplatePreconstructorExpression(exec);
	}

	@Override
	public ScriptValue_Variable addVariable(Referenced ref, String name, ScriptValue_Variable value) throws ScriptException {
		return this.getTemplate().addVariable(ref, name, value);
	}

	@Override
	public ScriptValue castToType(Referenced ref, ScriptValueType type) throws ScriptException {
		return this.getTemplate().castToType(ref, type);
	}

	@Override
	public ScriptTemplate_Abstract createObject(Referenced ref, ScriptTemplate_Abstract object) throws ScriptException {
		return this.getTemplate().createObject(ref, object);
	}

	@Override
	public void disableFullCreation() {
		this.getTemplate().disableFullCreation();
	}

	// Overloaded ScriptTemplate_Abstract fxns
	@Override
	public ScriptTemplate_Abstract getExtendedClass() {
		return this.getTemplate().getExtendedClass();
	}

	@Override
	public ScriptFunction getFunction(String name, List<ScriptValue> params) {
		return this.getTemplate().getFunction(name, params);
	}

	@Override
	public List<ScriptFunction> getFunctions() {
		return this.getTemplate().getFunctions();
	}

	@Override
	public ScriptTemplate_Abstract getFunctionTemplate(ScriptFunction fxn) {
		return this.getTemplate().getFunctionTemplate(fxn);
	}

	@Override
	public List<ScriptValueType> getInterfaces() {
		return this.getTemplate().getInterfaces();
	}

	@Override
	public ScriptValue_Variable getStaticReference() throws ScriptException {
		return this.getTemplate().getStaticReference();
	}

	private ScriptTemplate_Abstract getTemplate() {
		try {
			ScriptTemplate_Abstract template = (ScriptTemplate_Abstract) this.getEnvironment().retrieveVariable(this.name).getValue();
			assert template != null : "Template could not be retrieved (" + this.name + ")";
			return template;
		} catch (ScriptException ex) {
			throw new InternalException("Exception occurred while retrieving template: " + ex);
		}
	}

	// Abstract-value implementation
	@Override
	public ScriptValueType getType() {
		return this.getTemplate().getType();
	}

	@Override
	public ScriptValue getValue() throws ScriptException {
		return this.getTemplate().getValue();
	}

	@Override
	public ScriptValue_Variable getVariable(String name) throws ScriptException {
		return this.getTemplate().getVariable(name);
	}

	@Override
	public void initialize() throws ScriptException {
		this.getTemplate().initialize();
	}

	@Override
	public void initializeFunctions(Referenced ref) throws ScriptException {
		this.getTemplate().initializeFunctions(ref);
	}

	@Override
	public ScriptTemplate instantiateTemplate() {
		return this.getTemplate().instantiateTemplate();
	}

	@Override
	public boolean isAbstract() throws ScriptException {
		return this.getTemplate().isAbstract();
	}

	@Override
	public boolean isConstructing() throws ScriptException {
		return this.getTemplate().isConstructing();
	}

	@Override
	public boolean isConvertibleTo(ScriptValueType type) {
		return this.getTemplate().isConvertibleTo(type);
	}

	// Abstract-template implementation
	@Override
	public boolean isFullCreation() {
		return this.getTemplate().isFullCreation();
	}

	@Override
	public boolean isObject() {
		return this.getTemplate().isObject();
	}

	@Override
	public void nodificate() {
		assert Logs.openNode("Template Placeholder (" + this.name + ")");
		assert Logs.addSnapNode("Referenced Template", this.getTemplate());
		assert Logs.closeNode();
	}

	@Override
	public void setConstructing(boolean constructing) throws ScriptException {
		this.getTemplate().setConstructing(constructing);
	}

	@Override
	public ScriptValue setValue(Referenced ref, ScriptValue value) throws ScriptException {
		return this.getTemplate().setValue(ref, value);
	}

	@Override
	public int valuesCompare(Referenced ref, ScriptValue rhs) throws ScriptException {
		return this.getTemplate().valuesCompare(ref, rhs);
	}

	@Override
	public boolean valuesEqual(Referenced ref, ScriptValue rhs) throws ScriptException {
		return this.getTemplate().valuesEqual(ref, rhs);
	}
}
