package com.dafrito.rfe.script.proxies;

import java.util.LinkedList;
import java.util.List;

import com.dafrito.rfe.gui.debug.Debugger;
import com.dafrito.rfe.script.ScriptEnvironment;
import com.dafrito.rfe.script.exceptions.Exception_InternalError;
import com.dafrito.rfe.script.exceptions.Exception_Nodeable;
import com.dafrito.rfe.script.operations.ScriptExecutable;
import com.dafrito.rfe.script.parsing.Referenced;
import com.dafrito.rfe.script.parsing.ScriptKeywordType;
import com.dafrito.rfe.script.values.ScriptFunction;
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

	public void addConstructor(ScriptValueType returnType, List<ScriptValue> params) throws Exception_Nodeable {
		this.addFauxFunction("", returnType, params, ScriptKeywordType.PUBLIC, false, true);
	}

	public void addFauxFunction(String name, ScriptValueType returnType, List<ScriptValue> params, ScriptKeywordType permission, boolean isAbstract, boolean isStatic) throws Exception_Nodeable {
		this.addFunction(null, name, new ScriptFunction_Faux(this, name, returnType, params, permission, isAbstract, isStatic));
	}

	@Override
	public void addTemplatePreconstructorExpression(ScriptExecutable exec) throws Exception_Nodeable {
		throw new Exception_InternalError(this.getEnvironment(), "Invalid call in FauxTemplate");
	}

	// Function bodies are contained via a series of if statements in execute
	// Template will be null if the object is exactly of this type and is constructing, and thus must be created then
	public ScriptValue execute(Referenced ref, String name, List<ScriptValue> params, ScriptTemplate_Abstract rawTemplate) throws Exception_Nodeable {
		assert Debugger.openNode("Faux Template Executions", "Executing INSERTFAUXTEMPLATENAMEHERE Faux Template Function (" + ScriptFunction.getDisplayableFunctionName(name) + ")");
		FauxTemplate_InterfaceElement template = (FauxTemplate_InterfaceElement) rawTemplate;
		assert Debugger.addSnapNode("Template provided", template);
		assert Debugger.addSnapNode("Parameters provided", params);
		throw new Exception_InternalError("Invalid default in FauxTemplate:execute");
	}

	public FauxTemplate getExtendedFauxClass() {
		return (FauxTemplate) this.getExtendedClass();
	}

	// addFauxFunction(name,ScriptValueType type,List<ScriptValue_Abstract>params,ScriptKeywordType permission,boolean isAbstract,boolean isStatic)
	// All functions must be defined here. All function bodies are defined in 'execute'.
	@Override
	public void initialize() throws Exception_Nodeable {

	}

	public void initializeFunctions() throws Exception_Nodeable {
	}

	// Define default constructor here
	@Override
	public ScriptTemplate instantiateTemplate() {
		return null;
	}

	@Override
	public ScriptValue setValue(Referenced ref, ScriptValue value) throws Exception_Nodeable {
		throw new Exception_InternalError(this.getEnvironment(), "Invalid call in FauxTemplate");
	}

	@Override
	public int valuesCompare(Referenced ref, ScriptValue rhs) throws Exception_Nodeable {
		throw new Exception_InternalError(this.getEnvironment(), "Invalid call in FauxTemplate");
	}
}
