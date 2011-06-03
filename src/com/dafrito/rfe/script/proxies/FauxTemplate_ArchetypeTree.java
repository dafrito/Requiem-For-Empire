package com.dafrito.rfe.script.proxies;

import java.util.LinkedList;
import java.util.List;

import com.dafrito.rfe.ArchetypeMapNode;
import com.dafrito.rfe.Asset;
import com.dafrito.rfe.gui.debug.Debugger;
import com.dafrito.rfe.inspect.Nodeable;
import com.dafrito.rfe.script.ScriptConvertible;
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

public class FauxTemplate_ArchetypeTree extends FauxTemplate implements ScriptConvertible<ArchetypeMapNode>, Nodeable {
	public static final String ARCHETYPETREESTRING = "ArchetypeTree";
	private ArchetypeMapNode tree;

	public FauxTemplate_ArchetypeTree(ScriptEnvironment env) {
		super(env, ScriptValueType.createType(env, ARCHETYPETREESTRING), ScriptValueType.getObjectType(env), new LinkedList<ScriptValueType>(), false);
	}

	public FauxTemplate_ArchetypeTree(ScriptEnvironment env, ScriptValueType type) {
		super(env, type);
	}

	// Nodeable and ScriptConvertible interfaces
	@Override
	public ArchetypeMapNode convert() {
		return this.tree;
	}

	// Function bodies are contained via a series of if statements in execute
	// Template will be null if the object is exactly of this type and is constructing, and thus must be created then
	@Override
	public ScriptValue execute(Referenced ref, String name, List<ScriptValue> params, ScriptTemplate_Abstract rawTemplate) throws Exception_Nodeable {
		assert Debugger.openNode("Faux Template Executions", "Executing archetype tree faux template function (" + ScriptFunction.getDisplayableFunctionName(name) + ")");
		FauxTemplate_ArchetypeTree template = (FauxTemplate_ArchetypeTree) rawTemplate;
		assert Debugger.addSnapNode("Template provided", template);
		assert Debugger.addSnapNode("Parameters provided", params);
		if (name == null || name.equals("")) {
			if (template == null) {
				template = (FauxTemplate_ArchetypeTree) this.createObject(ref, template);
			}
			template.setTree(new ArchetypeMapNode(Parser.getArchetype(params.get(0))));
			params.clear();
		} else if (name.equals("addAsset")) {
			template.getTree().addAsset(Parser.getAsset(params.get(0)));
			assert Debugger.closeNode();
			return null;
		} else if (name.equals("getAssetsOfType")) {
			List<ScriptValue> assets = new LinkedList<ScriptValue>();
			for (Asset asset : template.getTree().getAssetsOfType(Parser.getArchetype(params.get(0)))) {
				assets.add(Parser.getRiffAsset(this.getEnvironment(), asset));
			}
			ScriptValue returning = Parser.getRiffList(this.getEnvironment(), assets);
			assert Debugger.closeNode();
			return returning;
		}
		ScriptValue returning = this.getExtendedFauxClass().execute(ref, name, params, template);
		assert Debugger.closeNode();
		return returning;
	}

	public ArchetypeMapNode getTree() {
		return this.tree;
	}

	// All functions must be defined here. All function bodies are defined in 'execute'.
	@Override
	public void initialize() throws Exception_Nodeable {
		assert Debugger.openNode("Faux Template Initializations", "Initializing archetype tree faux template");
		List<ScriptValue> fxnParams = new LinkedList<ScriptValue>();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.createType(this.getEnvironment(), FauxTemplate_Archetype.ARCHETYPESTRING)));
		this.addConstructor(this.getType(), fxnParams);
		this.disableFullCreation();
		this.getExtendedClass().initialize();
		fxnParams = new LinkedList<ScriptValue>();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.createType(this.getEnvironment(), FauxTemplate_Asset.ASSETSTRING)));
		this.addFauxFunction("addAsset", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		this.addFauxFunction("getAssetsOfType", ScriptValueType.createType(this.getEnvironment(), FauxTemplate_List.LISTSTRING), new LinkedList<ScriptValue>(), ScriptKeywordType.PUBLIC, false, false);
		assert Debugger.closeNode();
	}

	// Define default constructor here
	@Override
	public ScriptTemplate instantiateTemplate() {
		return new FauxTemplate_Archetype(this.getEnvironment(), this.getType());
	}

	@Override
	public void nodificate() {
		assert Debugger.openNode("Archetype Tree Faux Script-Element");
		super.nodificate();
		assert Debugger.addNode(this.tree);
		assert Debugger.closeNode();
	}

	public void setTree(ArchetypeMapNode tree) {
		this.tree = tree;
	}
}
