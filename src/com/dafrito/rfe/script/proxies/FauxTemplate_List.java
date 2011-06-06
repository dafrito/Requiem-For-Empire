package com.dafrito.rfe.script.proxies;

import java.util.LinkedList;
import java.util.List;

import com.dafrito.rfe.gui.debug.CommonString;
import com.dafrito.rfe.gui.debug.Debugger;
import com.dafrito.rfe.inspect.Nodeable;
import com.dafrito.rfe.script.Conversions;
import com.dafrito.rfe.script.ScriptConvertible;
import com.dafrito.rfe.script.ScriptEnvironment;
import com.dafrito.rfe.script.exceptions.Exception_Nodeable;
import com.dafrito.rfe.script.parsing.Referenced;
import com.dafrito.rfe.script.parsing.ScriptKeywordType;
import com.dafrito.rfe.script.values.RiffScriptFunction;
import com.dafrito.rfe.script.values.ScriptTemplate;
import com.dafrito.rfe.script.values.ScriptTemplate_Abstract;
import com.dafrito.rfe.script.values.ScriptValue;
import com.dafrito.rfe.script.values.ScriptValueType;
import com.dafrito.rfe.script.values.ScriptValue_Faux;

public class FauxTemplate_List extends FauxTemplate implements ScriptConvertible<List<ScriptValue>>, Nodeable {
	public static final String LISTSTRING = "List";
	private List<ScriptValue> list = new LinkedList<ScriptValue>();

	public FauxTemplate_List(ScriptEnvironment env) {
		super(env, ScriptValueType.createType(env, LISTSTRING), ScriptValueType.getObjectType(env), new LinkedList<ScriptValueType>(), false);
	}

	public FauxTemplate_List(ScriptEnvironment env, ScriptValueType type) {
		super(env, type);
	}

	// Convertible and Nodeable implementations
	@Override
	public List<ScriptValue> convert(ScriptEnvironment env) {
		return this.list;
	}

	// Function bodies are contained via a series of if statements in execute
	// Template will be null if the object is exactly of this type and is constructing, and thus must be created then
	@Override
	public ScriptValue execute(Referenced ref, String name, List<ScriptValue> params, ScriptTemplate_Abstract rawTemplate) throws Exception_Nodeable {
		assert Debugger.openNode("Faux Template Executions", "Executing List Faux Template Function (" + RiffScriptFunction.getDisplayableFunctionName(name) + ")");
		FauxTemplate_List template = (FauxTemplate_List) rawTemplate;
		ScriptValue returning;
		assert Debugger.addSnapNode("Template provided", template);
		assert Debugger.addSnapNode("Parameters provided", params);
		if (name == null || name.equals("")) {
			if (template == null) {
				template = (FauxTemplate_List) this.createObject(ref, template);
			}
			params.clear();
		} else if (name.equals("add")) {
			template.getList().add(params.get(0).getValue());
			assert Debugger.closeNode();
			return null;
		} else if (name.equals("addAll")) {
			template.getList().addAll(Conversions.getList(params.get(0)));
			assert Debugger.closeNode();
			return null;
		} else if (name.equals("get")) {
			assert Debugger.closeNode();
			return template.getList().get(Conversions.getInteger(this.getEnvironment(), params.get(0)));
		} else if (name.equals("size")) {
			assert Debugger.closeNode();
			return Conversions.getRiffInt(this.getEnvironment(), template.getList().size());
		}
		returning = this.getExtendedFauxClass().execute(ref, name, params, template);
		assert Debugger.closeNode();
		return returning;
	}

	public List<ScriptValue> getList() {
		return this.list;
	}

	// addFauxFunction(name,ScriptValueType type,List<ScriptValue_Abstract>params,ScriptKeywordType permission,boolean isAbstract)
	// All functions must be defined here. All function bodies are defined in 'execute'.
	@Override
	public void initialize() throws Exception_Nodeable {
		assert Debugger.openNode("Faux Template Initializations", "Initializing list faux template");
		this.addConstructor(this.getType());
		this.disableFullCreation();
		this.getExtendedClass().initialize();
		List<ScriptValue> fxnParams = FauxTemplate.createEmptyParamList();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.getObjectType(this.getEnvironment())));
		this.addFauxFunction("add", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		fxnParams = FauxTemplate.createEmptyParamList();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), this.getType()));
		this.addFauxFunction("addAll", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		fxnParams = FauxTemplate.createEmptyParamList();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.INT));
		this.addFauxFunction("get", ScriptValueType.getObjectType(this.getEnvironment()), fxnParams, ScriptKeywordType.PUBLIC, false, false);
		fxnParams = FauxTemplate.createEmptyParamList();
		this.addFauxFunction("size", ScriptValueType.INT, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		assert Debugger.closeNode();
	}

	// Define default constructor here
	@Override
	public ScriptTemplate instantiateTemplate() {
		return new FauxTemplate_List(this.getEnvironment(), this.getType());
	}

	@Override
	public void nodificate() {
		if (this.list == null) {
			assert Debugger.openNode("List Faux Template (0 element(s))");
		} else {
			assert Debugger.openNode("List Faux Template (" + this.list.size() + " element(s))");
		}
		super.nodificate();
		assert Debugger.addSnapNode(CommonString.ELEMENTS, this.list);
		assert Debugger.closeNode();
	}

	public void setList(List<ScriptValue> list) {
		this.list = list;
	}
}
