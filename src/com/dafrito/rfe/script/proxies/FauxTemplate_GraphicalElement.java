package com.dafrito.rfe.script.proxies;

import java.util.LinkedList;
import java.util.List;

import com.dafrito.rfe.gui.debug.Debugger;
import com.dafrito.rfe.script.ScriptEnvironment;
import com.dafrito.rfe.script.exceptions.ScriptException;
import com.dafrito.rfe.script.parsing.Referenced;
import com.dafrito.rfe.script.values.RiffScriptFunction;
import com.dafrito.rfe.script.values.ScriptTemplate;
import com.dafrito.rfe.script.values.ScriptTemplate_Abstract;
import com.dafrito.rfe.script.values.ScriptValue;
import com.dafrito.rfe.script.values.ScriptValueType;

public class FauxTemplate_GraphicalElement extends FauxTemplate {
	public static final String GRAPHICALELEMENTSTRING = "GraphicalElement";

	public FauxTemplate_GraphicalElement(ScriptEnvironment env) {
		super(env, ScriptValueType.createType(env, GRAPHICALELEMENTSTRING), ScriptValueType.getObjectType(env), new LinkedList<ScriptValueType>(), true);
	}

	public FauxTemplate_GraphicalElement(ScriptEnvironment env, ScriptValueType type) {
		super(env, type);
	}

	public FauxTemplate_GraphicalElement(ScriptEnvironment env, ScriptValueType type, ScriptValueType extended, List<ScriptValueType> implemented, boolean isAbstract) {
		super(env, type, extended, implemented, isAbstract);
	}

	// Function bodies are contained via a series of if statements in execute
	// Template will be null if the object is exactly of this type and is constructing, and thus must be created then
	@Override
	public ScriptValue execute(Referenced ref, String name, List<ScriptValue> params, ScriptTemplate_Abstract rawTemplate) throws ScriptException {
		assert Debugger.openNode("Faux Template Executions", "Executing Graphical Element Faux Template Function (" + RiffScriptFunction.getDisplayableFunctionName(name) + ")");
		FauxTemplate_GraphicalElement template = (FauxTemplate_GraphicalElement) rawTemplate;
		ScriptValue returning;
		assert Debugger.addSnapNode("Template provided", template);
		assert Debugger.addSnapNode("Parameters provided", params);
		returning = this.getExtendedFauxClass().execute(ref, name, params, template);
		assert Debugger.closeNode();
		return returning;
	}

	// All functions must be defined here. All function bodies are defined in 'execute'.
	@Override
	public void initialize() throws ScriptException {
		assert Debugger.openNode("Faux Template Initializations", "Initializing graphical element faux template");
		this.addConstructor(this.getType());
		this.disableFullCreation();
		this.getExtendedClass().initialize();
		assert Debugger.closeNode();
	}

	// Define default constructor here
	@Override
	public ScriptTemplate instantiateTemplate() {
		return new FauxTemplate_GraphicalElement(this.getEnvironment(), this.getType());
	}
}
