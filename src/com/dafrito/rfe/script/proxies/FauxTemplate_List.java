package com.dafrito.rfe.script.proxies;

import java.util.LinkedList;
import java.util.List;

import com.dafrito.rfe.debug.DebugString;
import com.dafrito.rfe.debug.Debugger;
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

public class FauxTemplate_List extends FauxTemplate implements ScriptConvertible, Nodeable {
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
	public Object convert() {
		return this.list;
	}

	// Function bodies are contained via a series of if statements in execute
	// Template will be null if the object is exactly of this type and is constructing, and thus must be created then
	@Override
	public ScriptValue execute(Referenced ref, String name, List<ScriptValue> params, ScriptTemplate_Abstract rawTemplate) throws Exception_Nodeable {
		assert Debugger.openNode("Faux Template Executions", "Executing List Faux Template Function (" + ScriptFunction.getDisplayableFunctionName(name) + ")");
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
			template.getList().addAll(Parser.getList(params.get(0)));
			assert Debugger.closeNode();
			return null;
		} else if (name.equals("get")) {
			assert Debugger.closeNode();
			return template.getList().get(Parser.getInteger(params.get(0)));
		} else if (name.equals("size")) {
			assert Debugger.closeNode();
			return Parser.getRiffInt(this.getEnvironment(), template.getList().size());
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
		this.addConstructor(this.getType(), ScriptValueType.createEmptyParamList());
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
		assert Debugger.addSnapNode(DebugString.ELEMENTS, this.list);
		assert Debugger.closeNode();
	}

	public void setList(List<ScriptValue> list) {
		this.list = list;
	}
}
