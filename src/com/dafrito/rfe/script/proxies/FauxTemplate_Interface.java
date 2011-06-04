package com.dafrito.rfe.script.proxies;

import java.util.LinkedList;
import java.util.List;

import com.dafrito.rfe.gui.Interface;
import com.dafrito.rfe.gui.debug.Debugger;
import com.dafrito.rfe.inspect.Nodeable;
import com.dafrito.rfe.script.ScriptEnvironment;
import com.dafrito.rfe.script.exceptions.Exception_Nodeable;
import com.dafrito.rfe.script.parsing.Parser;
import com.dafrito.rfe.script.parsing.Referenced;
import com.dafrito.rfe.script.parsing.ScriptKeywordType;
import com.dafrito.rfe.script.values.ScriptFunction;
import com.dafrito.rfe.script.values.ScriptTemplate;
import com.dafrito.rfe.script.values.ScriptTemplate_Abstract;
import com.dafrito.rfe.script.values.ScriptValue;
import com.dafrito.rfe.script.values.ScriptValueType;
import com.dafrito.rfe.script.values.ScriptValue_Faux;

public class FauxTemplate_Interface extends FauxTemplate implements Nodeable {
	public static final String INTERFACESTRING = "Interface";
	private Interface riffInterface;

	public FauxTemplate_Interface(ScriptEnvironment env) {
		super(env, ScriptValueType.createType(env, INTERFACESTRING), ScriptValueType.getObjectType(env), new LinkedList<ScriptValueType>(), false);
	}

	public FauxTemplate_Interface(ScriptEnvironment env, ScriptValueType type) {
		super(env, type);
		this.riffInterface = new Interface(env);
	}

	// Function bodies are contained via a series of if statements in execute
	// Template will be null if the object is exactly of this type and is constructing, and thus must be created then
	@Override
	public ScriptValue execute(Referenced ref, String name, List<ScriptValue> params, ScriptTemplate_Abstract rawTemplate) throws Exception_Nodeable {
		assert Debugger.openNode("Faux Template Executions", "Executing interface faux template function (" + ScriptFunction.getDisplayableFunctionName(name) + ")");
		FauxTemplate_Interface template = (FauxTemplate_Interface) rawTemplate;
		ScriptValue returning;
		assert Debugger.addSnapNode("Template provided", template);
		assert Debugger.addSnapNode("Parameters provided", params);
		if (name == null || name.equals("")) {
			if (template == null) {
				template = (FauxTemplate_Interface) this.createObject(ref, template);
			}
			assert Debugger.closeNode();
			return template;
		} else if (name.equals("add")) {
			template.getInterface().getRoot().add(Parser.getElement(params.get(0)));
			assert Debugger.closeNode();
			return null;
		} else if (name.equals("getRoot")) {
			returning = Parser.getRiffElement(this.getEnvironment(), template.getInterface().getRoot());
			assert Debugger.closeNode();
			return returning;
		}
		returning = this.getExtendedFauxClass().execute(ref, name, params, template);
		assert Debugger.closeNode();
		return returning;
	}

	public Interface getInterface() {
		return this.riffInterface;
	}

	// addFauxFunction(name,ScriptValueType type,List<ScriptValue_Abstract>params,ScriptKeywordType permission,boolean isAbstract)
	// All functions must be defined here. All function bodies are defined in 'execute'.
	@Override
	public void initialize() throws Exception_Nodeable {
		assert Debugger.openNode("Faux Template Initializations", "Initializing interface faux template");
		this.addConstructor(this.getType());
		this.disableFullCreation();
		this.getExtendedClass().initialize();
		List<ScriptValue> fxnParams = FauxTemplate.createEmptyParamList();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.createType(this.getEnvironment(), FauxTemplate_InterfaceElement.INTERFACEELEMENTSTRING)));
		this.addFauxFunction("add", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		fxnParams = new LinkedList<ScriptValue>();
		this.addFauxFunction("getRoot", ScriptValueType.createType(this.getEnvironment(), FauxTemplate_InterfaceElement.INTERFACEELEMENTSTRING), fxnParams, ScriptKeywordType.PUBLIC, false, false);
		assert Debugger.closeNode();
	}

	// Define default constructor here
	@Override
	public ScriptTemplate instantiateTemplate() {
		return new FauxTemplate_Interface(this.getEnvironment(), this.getType());
	}

	@Override
	public void nodificate() {
		assert Debugger.openNode("Interface Faux Template");
		super.nodificate();
		assert Debugger.addNode(this.riffInterface);
		assert Debugger.closeNode();
	}
}
