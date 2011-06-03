package com.dafrito.rfe.script.proxies;

import java.util.LinkedList;
import java.util.List;

import com.dafrito.rfe.ArchetypeMapNode;
import com.dafrito.rfe.geom.DiscreteRegion;
import com.dafrito.rfe.gui.debug.Debugger;
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

public class FauxTemplate_DiscreteRegion extends FauxTemplate_GraphicalElement implements ScriptConvertible<DiscreteRegion>, Nodeable {
	public static final String DISCRETEREGIONSTRING = "DiscreteRegion";
	private DiscreteRegion region;

	public FauxTemplate_DiscreteRegion(ScriptEnvironment env) {
		super(env, ScriptValueType.createType(env, DISCRETEREGIONSTRING), ScriptValueType.getObjectType(env), new LinkedList<ScriptValueType>(), false);
		assert env != null;
	}

	public FauxTemplate_DiscreteRegion(ScriptEnvironment env, ScriptValueType type) {
		super(env, type);
	}

	// Nodeable and ScriptConvertible interfaces
	@Override
	public DiscreteRegion convert() {
		return this.region;
	}

	// Function bodies are contained via a series of if statements in execute
	// Template will be null if the object is exactly of this type and is constructing, and thus must be created then
	@Override
	public ScriptValue execute(Referenced ref, String name, List<ScriptValue> params, ScriptTemplate_Abstract rawTemplate) throws Exception_Nodeable {
		assert Debugger.openNode("Faux Template Executions", "Executing Discrete Region Faux Template Function (" + ScriptFunction.getDisplayableFunctionName(name) + ")");
		FauxTemplate_DiscreteRegion template = (FauxTemplate_DiscreteRegion) rawTemplate;
		assert Debugger.addSnapNode("Template provided", template);
		assert Debugger.addSnapNode("Parameters provided", params);
		if (name == null || name.equals("")) {
			if (template == null) {
				template = (FauxTemplate_DiscreteRegion) this.createObject(ref, template);
			}
			template.setRegion(this.region = new DiscreteRegion(this.getEnvironment()));
			params.clear();
		} else if (name.equals("add")) {
			//region.addPoint(Parser.getPoint(aaron is a sand jewparams.get(0)));
			if (params.size() == 1) {
				template.getRegion().addPoint(Parser.getPoint(params.get(0)));
				assert Debugger.closeNode();
				return null;
			}
		} else if (name.equals("addAsset")) {
			if (template.getRegion().getProperty("Archetypes") == null) {
				template.getRegion().setProperty("Archetypes", ArchetypeMapNode.createTree(Parser.getAsset(params.get(0))));
			} else {
				((ArchetypeMapNode) template.getRegion().getProperty("Archetypes")).addAsset(Parser.getAsset(params.get(0)));
			}
			assert Debugger.closeNode();
			return null;
		} else if (name.equals("setProperty")) {
			if (params.size() == 2) {
				template.getRegion().setProperty(Parser.getString(params.get(0)), Parser.getObject(params.get(1)));
				assert Debugger.closeNode();
				return null;
			}
		} else if (name.equals("getProperty")) {
			if (params.size() == 1) {
				ScriptValue returning = (ScriptValue) Parser.convert(this.getEnvironment(), template.getRegion().getProperty(Parser.getString(params.get(0))));
				assert Debugger.addSnapNode("Retrieved property", returning);
				assert Debugger.closeNode();
				return returning;
			}
		} else if (name.equals("getCenter")) {
			ScriptValue returning = Parser.getRiffPoint(this.getEnvironment(), template.getRegion().getCenter());
			assert Debugger.closeNode();
			return returning;
		}
		ScriptValue returning = this.getExtendedFauxClass().execute(ref, name, params, template);
		assert Debugger.closeNode();
		return returning;
	}

	public DiscreteRegion getRegion() {
		return this.region;
	}

	// All functions must be defined here. All function bodies are defined in 'execute'.
	@Override
	public void initialize() throws Exception_Nodeable {
		assert Debugger.openNode("Faux Template Initializations", "Initializing discrete region faux template");
		this.addConstructor(this.getType(), ScriptValueType.createEmptyParamList());
		List<ScriptValue> fxnParams = new LinkedList<ScriptValue>();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), this.getType()));
		this.addConstructor(this.getType(), fxnParams);
		this.disableFullCreation();
		this.getExtendedClass().initialize();
		fxnParams = new LinkedList<ScriptValue>();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.createType(this.getEnvironment(), FauxTemplate_Point.POINTSTRING)));
		this.addFauxFunction("add", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		fxnParams = new LinkedList<ScriptValue>();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.createType(this.getEnvironment(), FauxTemplate_Asset.ASSETSTRING)));
		this.addFauxFunction("addAsset", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		fxnParams = new LinkedList<ScriptValue>();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.STRING));
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.getObjectType(this.getEnvironment())));
		this.addFauxFunction("setProperty", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		fxnParams = new LinkedList<ScriptValue>();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.STRING));
		this.addFauxFunction("getProperty", ScriptValueType.getObjectType(this.getEnvironment()), fxnParams, ScriptKeywordType.PUBLIC, false, false);
		fxnParams = new LinkedList<ScriptValue>();
		this.addFauxFunction("getCenter", ScriptValueType.createType(this.getEnvironment(), FauxTemplate_Point.POINTSTRING), fxnParams, ScriptKeywordType.PUBLIC, false, false);
		assert Debugger.closeNode();
	}

	// Define default constructor here
	@Override
	public ScriptTemplate instantiateTemplate() {
		return new FauxTemplate_DiscreteRegion(this.getEnvironment(), this.getType());
	}

	@Override
	public void nodificate() {
		assert Debugger.openNode("Discrete Region Faux Script-Element");
		super.nodificate();
		assert Debugger.addNode(this.region);
		assert Debugger.closeNode();
	}

	public void setRegion(DiscreteRegion region) {
		this.region = region;
	}
}
