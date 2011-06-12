package com.dafrito.rfe.script.proxies;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.dafrito.rfe.Scenario;
import com.dafrito.rfe.Terrestrial;
import com.dafrito.rfe.gui.debug.Debugger;
import com.dafrito.rfe.inspect.Nodeable;
import com.dafrito.rfe.script.Conversions;
import com.dafrito.rfe.script.ScriptConvertible;
import com.dafrito.rfe.script.ScriptEnvironment;
import com.dafrito.rfe.script.exceptions.ScriptException;
import com.dafrito.rfe.script.parsing.Referenced;
import com.dafrito.rfe.script.parsing.ScriptKeywordType;
import com.dafrito.rfe.script.values.RiffScriptFunction;
import com.dafrito.rfe.script.values.ScriptTemplate;
import com.dafrito.rfe.script.values.ScriptTemplate_Abstract;
import com.dafrito.rfe.script.values.ScriptValue;
import com.dafrito.rfe.script.values.ScriptValueType;
import com.dafrito.rfe.script.values.ScriptValue_Faux;

public class FauxTemplate_Scenario extends FauxTemplate implements Nodeable, ScriptConvertible<Scenario> {
	public static final String SCENARIOSTRING = "Scenario";
	private Scenario scenario;

	public FauxTemplate_Scenario(ScriptEnvironment env) {
		super(env, ScriptValueType.createType(env, SCENARIOSTRING), ScriptValueType.getObjectType(env), new LinkedList<ScriptValueType>(), false);
	}

	public FauxTemplate_Scenario(ScriptEnvironment env, ScriptValueType type) {
		super(env, type);
		this.scenario = new Scenario(env, new Terrestrial(1), "Scenario");
	}

	// Nodeable implementation
	@Override
	public Scenario convert(ScriptEnvironment env) {
		return this.scenario;
	}

	// Function bodies are contained via a series of if statements in execute
	// Template will be null if the object is exactly of this type and is constructing, and thus must be created then
	@Override
	public ScriptValue execute(Referenced ref, String name, List<ScriptValue> params, ScriptTemplate_Abstract rawTemplate) throws ScriptException {
		assert Debugger.openNode("Faux Template Executions", "Executing scenario faux template function (" + RiffScriptFunction.getDisplayableFunctionName(name) + ")");
		FauxTemplate_Scenario template = (FauxTemplate_Scenario) rawTemplate;
		ScriptValue returning;
		assert Debugger.addSnapNode("Template provided", template);
		assert Debugger.addSnapNode("Parameters provided", params);
		if (name == null || name.equals("")) {
			if (template == null) {
				template = (FauxTemplate_Scenario) this.createObject(ref, template);
			}
			switch (params.size()) {
			case 2:
				template.getScenario().setTerrestrial(Conversions.getTerrestrial(this.getEnvironment(), params.get(0)));
				template.getScenario().setName(Conversions.getString(this.getEnvironment(), params.get(1)));
			case 0:
				assert Debugger.closeNode();
				return template;
			}
		} else if (name.equals("getName")) {
			returning = Conversions.wrapString(ref.getEnvironment(), template.getScenario().getName());
			assert Debugger.closeNode();
			return returning;
		} else if (name.equals("setName")) {
			template.getScenario().setName(Conversions.getString(this.getEnvironment(), params.get(0)));
			assert Debugger.closeNode();
			return null;
		} else if (name.equals("getTerrestrial")) {
			returning = Conversions.wrapTerrestrial(ref.getEnvironment(), template.getScenario().getTerrestrial());
			assert Debugger.closeNode();
			return returning;
		} else if (name.equals("setTerrestrial")) {
			template.getScenario().setTerrestrial(Conversions.getTerrestrial(this.getEnvironment(), params.get(0)));
			assert Debugger.closeNode();
			return null;
		} else if (name.equals("getScheduler")) {
			returning = Conversions.wrapScheduler(this.getEnvironment(), template.getScenario().getScheduler());
			assert Debugger.closeNode();
			return returning;
		}
		returning = this.getExtendedFauxClass().execute(ref, name, params, template);
		assert Debugger.closeNode();
		return returning;
	}

	public Scenario getScenario() {
		return this.scenario;
	}

	// addFauxFunction(name,ScriptValueType type,List<ScriptValue_Abstract>params,ScriptKeywordType permission,boolean isAbstract)
	// All functions must be defined here. All function bodies are defined in 'execute'.
	@Override
	public void initialize() throws ScriptException {
		assert Debugger.openNode("Faux Template Initializations", "Initializing scenario faux template");
		this.addConstructor(this.getType());
		List<ScriptValue> fxnParams = new LinkedList<ScriptValue>();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.createType(this.getEnvironment(), FauxTemplate_Terrestrial.TERRESTRIALSTRING)));
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.STRING));
		this.addConstructor(this.getType(), fxnParams);
		this.disableFullCreation();
		this.getExtendedClass().initialize();
		this.addFauxFunction("getName", ScriptValueType.STRING, Collections.<ScriptValue> emptyList(), ScriptKeywordType.PUBLIC, false, false);
		fxnParams = new LinkedList<ScriptValue>();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.STRING));
		this.addFauxFunction("setName", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		this.addFauxFunction("getTerrestrial", ScriptValueType.createType(this.getEnvironment(), FauxTemplate_Terrestrial.TERRESTRIALSTRING), Collections.<ScriptValue> emptyList(), ScriptKeywordType.PUBLIC, false, false);
		fxnParams = new LinkedList<ScriptValue>();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.createType(this.getEnvironment(), FauxTemplate_Terrestrial.TERRESTRIALSTRING)));
		this.addFauxFunction("setTerrestrial", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		this.addFauxFunction("getScheduler", ScriptValueType.createType(this.getEnvironment(), FauxTemplate_Scheduler.SCHEDULERSTRING), Collections.<ScriptValue> emptyList(), ScriptKeywordType.PUBLIC, false, false);
		assert Debugger.closeNode();
	}

	// Define default constructor here
	@Override
	public ScriptTemplate instantiateTemplate() {
		return new FauxTemplate_Scenario(this.getEnvironment(), this.getType());
	}

	@Override
	public void nodificate() {
		assert Debugger.openNode("Scenario Faux Template");
		super.nodificate();
		assert Debugger.addNode(this.scenario);
		assert Debugger.closeNode();
	}

	public void setScenario(Scenario scenario) {
		this.scenario = scenario;
	}
}
