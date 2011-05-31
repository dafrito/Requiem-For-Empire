package com.dafrito.rfe.script.proxies;

import java.util.LinkedList;
import java.util.List;

import com.dafrito.rfe.debug.Debugger;
import com.dafrito.rfe.inspect.Nodeable;
import com.dafrito.rfe.script.Parser;
import com.dafrito.rfe.script.Referenced;
import com.dafrito.rfe.script.ScriptEnvironment;
import com.dafrito.rfe.script.ScriptExecutable_CallFunction;
import com.dafrito.rfe.script.ScriptFunction;
import com.dafrito.rfe.script.ScriptKeywordType;
import com.dafrito.rfe.script.ScriptTemplate_Abstract;
import com.dafrito.rfe.script.ScriptValue;
import com.dafrito.rfe.script.ScriptValueType;
import com.dafrito.rfe.script.ScriptValue_Faux;
import com.dafrito.rfe.script.exceptions.Exception_Nodeable;
import com.dafrito.rfe.style.Stylesheets;

public class FauxTemplate_RiffDali extends FauxTemplate implements Nodeable {
	public static final String RIFFDALISTRING = "RiffDali";

	public FauxTemplate_RiffDali(ScriptEnvironment env) {
		super(env, ScriptValueType.createType(env, RIFFDALISTRING), ScriptValueType.getObjectType(env), new LinkedList<ScriptValueType>(), true);
	}

	// Function bodies are contained via a series of if statements in execute
	// Template will be null if the object is exactly of this type and is constructing, and thus must be created then
	@Override
	public ScriptValue execute(Referenced ref, String name, List<ScriptValue> params, ScriptTemplate_Abstract rawTemplate) throws Exception_Nodeable {
		assert Debugger.openNode("Faux Template Executions", "Executing RiffDali Faux Template Function (" + ScriptFunction.getDisplayableFunctionName(name) + ")");
		ScriptValue returning = null;
		assert Debugger.addSnapNode("Template provided", rawTemplate);
		assert Debugger.addSnapNode("Parameters provided", params);
		if (name.equals("parseColor")) {
			returning = Parser.getRiffColor(this.getEnvironment(), Stylesheets.getColor(Parser.getString(params.get(0))));
		} else if (name.equals("paintPanel")) {
			List<ScriptValue> list = Parser.getList(params.get(1));
			List<ScriptValue> paramList = new LinkedList<ScriptValue>();
			for (ScriptValue value : list) {
				paramList.clear();
				paramList.add(value);
				ScriptExecutable_CallFunction.callFunction(this.getEnvironment(), ref, params.get(0), "drawRegion", paramList);
			}
		} else {
			returning = this.getExtendedFauxClass().execute(ref, name, params, rawTemplate);
		}
		assert Debugger.closeNode();
		return returning;
	}

	// addFauxFunction(name,ScriptValueType type,List<ScriptValue_Abstract>params,ScriptKeywordType permission,boolean isAbstract)
	// All functions must be defined here. All function bodies are defined in 'execute'.
	@Override
	public void initialize() throws Exception_Nodeable {
		assert Debugger.openNode("Faux Template Initializations", "Initializing RiffDali faux template");
		this.disableFullCreation();
		this.getExtendedClass().initialize();
		List<ScriptValue> fxnParams = FauxTemplate.createEmptyParamList();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.STRING));
		this.addFauxFunction("parseColor", ScriptValueType.createType(this.getEnvironment(), FauxTemplate_Color.COLORSTRING), fxnParams, ScriptKeywordType.PUBLIC, false, true);
		fxnParams = FauxTemplate.createEmptyParamList();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.createType(this.getEnvironment(), FauxTemplate_Panel.PANELSTRING)));
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.createType(this.getEnvironment(), FauxTemplate_List.LISTSTRING)));
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.createType(this.getEnvironment(), FauxTemplate_List.LISTSTRING)));
		this.addFauxFunction("paintPanel", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		assert Debugger.closeNode();
	}

	@Override
	public void nodificate() {
		assert Debugger.openNode("RiffDali Faux Template");
		super.nodificate();
		assert Debugger.closeNode();
	}
}
