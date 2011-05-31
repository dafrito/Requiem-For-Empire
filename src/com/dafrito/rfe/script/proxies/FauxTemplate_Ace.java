package com.dafrito.rfe.script.proxies;

import java.util.LinkedList;
import java.util.List;

import com.dafrito.rfe.Ace;
import com.dafrito.rfe.Debugger;
import com.dafrito.rfe.inspect.Nodeable;
import com.dafrito.rfe.script.Parser;
import com.dafrito.rfe.script.Referenced;
import com.dafrito.rfe.script.ScriptConvertible;
import com.dafrito.rfe.script.ScriptEnvironment;
import com.dafrito.rfe.script.ScriptFunction;
import com.dafrito.rfe.script.ScriptKeywordType;
import com.dafrito.rfe.script.ScriptTemplate;
import com.dafrito.rfe.script.ScriptTemplate_Abstract;
import com.dafrito.rfe.script.ScriptValue;
import com.dafrito.rfe.script.ScriptValueType;
import com.dafrito.rfe.script.ScriptValue_Faux;
import com.dafrito.rfe.script.exceptions.Exception_Nodeable;

public class FauxTemplate_Ace extends FauxTemplate implements ScriptConvertible, Nodeable {
	public static final String ACESTRING = "Ace";
	private Ace ace;

	public FauxTemplate_Ace(ScriptEnvironment env) {
		super(env, ScriptValueType.createType(env, ACESTRING), ScriptValueType.getObjectType(env), new LinkedList<ScriptValueType>(), false);
	}

	public FauxTemplate_Ace(ScriptEnvironment env, ScriptValueType type) {
		super(env, type);
	}

	// Nodeable and ScriptConvertible interfaces
	@Override
	public Object convert() {
		return this.ace;
	}

	// Function bodies are contained via a series of if statements in execute
	// Template will be null if the object is exactly of this type and is constructing, and thus must be created then
	@Override
	public ScriptValue execute(Referenced ref, String name, List<ScriptValue> params, ScriptTemplate_Abstract rawTemplate) throws Exception_Nodeable {
		assert Debugger.openNode("Faux Template Executions", "Executing ace faux template function (" + ScriptFunction.getDisplayableFunctionName(name) + ")");
		FauxTemplate_Ace template = (FauxTemplate_Ace) rawTemplate;
		assert Debugger.addSnapNode("Template provided", template);
		assert Debugger.addSnapNode("Parameters provided", params);
		if (name == null || name.equals("")) {
			if (template == null) {
				template = (FauxTemplate_Ace) this.createObject(ref, template);
			}
			template.setAce(new Ace(this.getEnvironment(), Parser.getArchetype(params.get(0)), Parser.getDouble(params.get(1))));
			params.clear();
		} else if (name.equals("setEfficiency")) {
			template.getAce().setEfficiency(Parser.getDouble(params.get(0)));
			assert Debugger.closeNode();
			return null;
		} else if (name.equals("getEfficiency")) {
			ScriptValue returning = Parser.getRiffDouble(this.getEnvironment(), template.getAce().getEfficiency());
			assert Debugger.closeNode();
			return returning;
		} else if (name.equals("getArchetype")) {
			ScriptValue returning = Parser.getRiffArchetype(template.getAce().getArchetype());
			assert Debugger.closeNode();
			return returning;
		}
		ScriptValue returning = this.getExtendedFauxClass().execute(ref, name, params, template);
		assert Debugger.closeNode();
		return returning;
	}

	public Ace getAce() {
		return this.ace;
	}

	// All functions must be defined here. All function bodies are defined in 'execute'.
	@Override
	public void initialize() throws Exception_Nodeable {
		assert Debugger.openNode("Faux Template Initializations", "Initializing ace faux template");
		List<ScriptValue> fxnParams = new LinkedList<ScriptValue>();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.createType(this.getEnvironment(), FauxTemplate_Archetype.ARCHETYPESTRING)));
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.DOUBLE));
		this.addConstructor(this.getType(), fxnParams);
		this.disableFullCreation();
		this.getExtendedClass().initialize();
		fxnParams = new LinkedList<ScriptValue>();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.DOUBLE));
		this.addFauxFunction("setEfficiency", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		fxnParams = new LinkedList<ScriptValue>();
		this.addFauxFunction("getEfficiency", ScriptValueType.DOUBLE, new LinkedList<ScriptValue>(), ScriptKeywordType.PUBLIC, false, false);
		this.addFauxFunction("getArchetype", ScriptValueType.createType(this.getEnvironment(), FauxTemplate_Archetype.ARCHETYPESTRING), fxnParams, ScriptKeywordType.PUBLIC, false, false);
		assert Debugger.closeNode();
	}

	// Define default constructor here
	@Override
	public ScriptTemplate instantiateTemplate() {
		return new FauxTemplate_Ace(this.getEnvironment(), this.getType());
	}

	@Override
	public void nodificate() {
		assert Debugger.openNode("Ace Faux Script-Element");
		super.nodificate();
		assert Debugger.addNode(this.ace);
		assert Debugger.closeNode();
	}

	public void setAce(Ace ace) {
		this.ace = ace;
	}
}
