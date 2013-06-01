package com.dafrito.rfe.script.proxies;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.dafrito.rfe.logging.Logs;
import com.dafrito.rfe.script.ScriptEnvironment;
import com.dafrito.rfe.script.exceptions.Exception_InternalError;
import com.dafrito.rfe.script.exceptions.ScriptException;
import com.dafrito.rfe.script.operations.ScriptExecutable;
import com.dafrito.rfe.script.parsing.Referenced;
import com.dafrito.rfe.script.parsing.ScriptKeywordType;
import com.dafrito.rfe.script.values.RiffScriptFunction;
import com.dafrito.rfe.script.values.ScriptFunction_Faux;
import com.dafrito.rfe.script.values.ScriptTemplate;
import com.dafrito.rfe.script.values.ScriptTemplate_Abstract;
import com.dafrito.rfe.script.values.ScriptValue;
import com.dafrito.rfe.script.values.ScriptValueType;

public abstract class FauxTemplate extends ScriptTemplate implements ScriptValue {
	public static List<ScriptValue> createEmptyParamList() {
		return new LinkedList<ScriptValue>();
	}

	public FauxTemplate(ScriptEnvironment env, ScriptValueType type) {
		super(env, type);
	}

	public FauxTemplate(ScriptEnvironment env, ScriptValueType type, ScriptValueType extended, List<ScriptValueType> implemented, boolean isAbstract) {
		super(env, type, extended, implemented, isAbstract);
	}

	public void addConstructor(ScriptValueType returnType, ScriptValue... params) throws ScriptException {
		this.addConstructor(returnType, Arrays.asList(params));
	}

	public void addConstructor(ScriptValueType returnType, List<ScriptValue> params) throws ScriptException {
		this.addFauxFunction("", returnType, params, ScriptKeywordType.PUBLIC, false, true);
	}

	public void addFauxFunction(String name, ScriptValueType returnType, List<ScriptValue> params, ScriptKeywordType permission, boolean isAbstract, boolean isStatic) throws ScriptException {
		this.addFunction(null, name, new ScriptFunction_Faux(this, name, returnType, params, permission, isAbstract, isStatic));
	}

	@Override
	public void addTemplatePreconstructorExpression(ScriptExecutable exec) throws ScriptException {
		throw new Exception_InternalError(this.getEnvironment(), "Invalid call in FauxTemplate");
	}

	// Function bodies are contained via a series of if statements in execute
	// Template will be null if the object is exactly of this type and is constructing, and thus must be created then
	public ScriptValue execute(Referenced ref, String name, List<ScriptValue> params, ScriptTemplate_Abstract rawTemplate) throws ScriptException {
		assert Logs.openNode("Faux Template Executions", "Executing INSERTFAUXTEMPLATENAMEHERE Faux Template Function (" + RiffScriptFunction.getDisplayableFunctionName(name) + ")");
		FauxTemplate_InterfaceElement template = (FauxTemplate_InterfaceElement) rawTemplate;
		assert Logs.addSnapNode("Template provided", template);
		assert Logs.addSnapNode("Parameters provided", params);
		throw new Exception_InternalError("Invalid default in FauxTemplate:execute");
	}

	public FauxTemplate getExtendedFauxClass() {
		return (FauxTemplate) this.getExtendedClass();
	}

	// addFauxFunction(name,ScriptValueType type,List<ScriptValue_Abstract>params,ScriptKeywordType permission,boolean isAbstract,boolean isStatic)
	// All functions must be defined here. All function bodies are defined in 'execute'.
	@Override
	public void initialize() throws ScriptException {

	}

	public void initializeFunctions() throws ScriptException {
	}

	// Define default constructor here
	@Override
	public ScriptTemplate instantiateTemplate() {
		return null;
	}

	@Override
	public ScriptValue setValue(Referenced ref, ScriptValue value) throws ScriptException {
		throw new Exception_InternalError(this.getEnvironment(), "Invalid call in FauxTemplate");
	}

	@Override
	public int valuesCompare(Referenced ref, ScriptValue rhs) throws ScriptException {
		throw new Exception_InternalError(this.getEnvironment(), "Invalid call in FauxTemplate");
	}
}
