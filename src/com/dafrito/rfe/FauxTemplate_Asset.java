package com.dafrito.rfe;

import java.util.LinkedList;
import java.util.List;

import com.dafrito.rfe.inspect.Nodeable;
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

public class FauxTemplate_Asset extends FauxTemplate implements ScriptConvertible, Nodeable {
	public static final String ASSETSTRING = "Asset";
	private Asset asset;

	public FauxTemplate_Asset(ScriptEnvironment env) {
		super(env, ScriptValueType.createType(env, ASSETSTRING), ScriptValueType.getObjectType(env), new LinkedList<ScriptValueType>(), false);
	}

	public FauxTemplate_Asset(ScriptEnvironment env, ScriptValueType type) {
		super(env, type);
		this.asset = new Asset(env);
	}

	// Nodeable and ScriptConvertible interfaces
	@Override
	public Object convert() {
		return this.asset;
	}

	// Function bodies are contained via a series of if statements in execute
	// Template will be null if the object is exactly of this type and is constructing, and thus must be created then
	@Override
	public ScriptValue execute(Referenced ref, String name, List<ScriptValue> params, ScriptTemplate_Abstract rawTemplate) throws Exception_Nodeable {
		assert Debugger.openNode("Faux Template Executions", "Executing Asset Faux Template Function (" + ScriptFunction.getDisplayableFunctionName(name) + ")");
		FauxTemplate_Asset template = (FauxTemplate_Asset) rawTemplate;
		assert Debugger.addSnapNode("Template provided", template);
		assert Debugger.addSnapNode("Parameters provided", params);
		if (name == null || name.equals("")) {
			if (template == null) {
				template = (FauxTemplate_Asset) this.createObject(ref, template);
			}
			template.getAsset().setLocation(Parser.getPoint(params.get(0)));
			params.clear();
		} else if (name.equals("setProperty")) {
			if (params.size() == 2) {
				template.getAsset().setProperty(Parser.getString(params.get(0)), params.get(1).getValue());
				assert Debugger.closeNode();
				return null;
			}
		} else if (name.equals("getProperty")) {
			if (params.size() == 1) {
				ScriptValue value = (ScriptValue) ((ScriptConvertible) template.getAsset().getProperty(Parser.getString(params.get(0)))).convert();
				assert Debugger.closeNode();
				return value;
			}
		} else if (name.equals("addAce")) {
			template.getAsset().addAce(Parser.getAce(params.get(0)));
			assert Debugger.closeNode();
			return null;
		} else if (name.equals("getAces")) {
			List<ScriptValue> list = new LinkedList<ScriptValue>();
			for (Ace ace : template.getAsset().getAces()) {
				list.add(Parser.getRiffAce(ace));
			}
			ScriptValue returning = Parser.getRiffList(this.getEnvironment(), list);
			assert Debugger.closeNode();
			return returning;
		} else if (name.equals("getLocation")) {
			assert template.getAsset().getLocation() != null : "Asset location is null!";
			ScriptValue returning = Parser.getRiffPoint(this.getEnvironment(), template.getAsset().getLocation());
			assert Debugger.closeNode();
			return returning;
		} else if (name.equals("setLocation")) {
			template.getAsset().setLocation(Parser.getPoint(params.get(0)));
			assert Debugger.closeNode();
			return null;
		}
		ScriptValue returning = this.getExtendedFauxClass().execute(ref, name, params, template);
		assert Debugger.closeNode();
		return returning;
	}

	public Asset getAsset() {
		return this.asset;
	}

	// All functions must be defined here. All function bodies are defined in 'execute'.
	@Override
	public void initialize() throws Exception_Nodeable {
		assert Debugger.openNode("Faux Template Initializations", "Initializing asset faux template");
		List<ScriptValue> fxnParams = new LinkedList<ScriptValue>();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.createType(this.getEnvironment(), FauxTemplate_Point.POINTSTRING)));
		this.addConstructor(this.getType(), fxnParams);
		this.disableFullCreation();
		this.getExtendedClass().initialize();
		fxnParams = new LinkedList<ScriptValue>();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.STRING));
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.getObjectType(this.getEnvironment())));
		this.addFauxFunction("setProperty", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		fxnParams = new LinkedList<ScriptValue>();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.STRING));
		this.addFauxFunction("getProperty", ScriptValueType.getObjectType(this.getEnvironment()), fxnParams, ScriptKeywordType.PUBLIC, false, false);
		fxnParams = new LinkedList<ScriptValue>();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.createType(this.getEnvironment(), FauxTemplate_Ace.ACESTRING)));
		this.addFauxFunction("addAce", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		fxnParams = new LinkedList<ScriptValue>();
		this.addFauxFunction("getAces", ScriptValueType.createType(this.getEnvironment(), FauxTemplate_List.LISTSTRING), fxnParams, ScriptKeywordType.PUBLIC, false, false);
		fxnParams = new LinkedList<ScriptValue>();
		this.addFauxFunction("getLocation", ScriptValueType.createType(this.getEnvironment(), FauxTemplate_Point.POINTSTRING), fxnParams, ScriptKeywordType.PUBLIC, false, false);
		fxnParams = new LinkedList<ScriptValue>();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.createType(this.getEnvironment(), FauxTemplate_Point.POINTSTRING)));
		this.addFauxFunction("setLocation", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		assert Debugger.closeNode();
	}

	// Define default constructor here
	@Override
	public ScriptTemplate instantiateTemplate() {
		return new FauxTemplate_Asset(this.getEnvironment(), this.getType());
	}

	@Override
	public void nodificate() {
		assert Debugger.openNode("Asset Faux Script-Element");
		super.nodificate();
		assert Debugger.addNode(this.asset);
		assert Debugger.closeNode();
	}

	public void setAsset(Asset asset) {
		this.asset = asset;
	}
}
