package com.dafrito.rfe.script.proxies;

import java.util.LinkedList;
import java.util.List;

import com.dafrito.rfe.Terrain;
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

public class FauxTemplate_Terrain extends FauxTemplate implements Nodeable, ScriptConvertible<Terrain> {
	public static final String TERRAINSTRING = "Terrain";
	private Terrain terrain;

	public FauxTemplate_Terrain(ScriptEnvironment env) {
		super(env, ScriptValueType.createType(env, TERRAINSTRING), ScriptValueType.getObjectType(env), new LinkedList<ScriptValueType>(), false);
	}

	public FauxTemplate_Terrain(ScriptEnvironment env, ScriptValueType type) {
		super(env, type);
		this.terrain = new Terrain(env);
	}

	// Nodeable implementation
	@Override
	public Terrain convert() {
		return this.terrain;
	}

	// Function bodies are contained via a series of if statements in execute
	// Template will be null if the object is exactly of this type and is constructing, and thus must be created then
	@Override
	public ScriptValue execute(Referenced ref, String name, List<ScriptValue> params, ScriptTemplate_Abstract rawTemplate) throws Exception_Nodeable {
		assert Debugger.openNode("Faux Template Executions", "Executing terrain faux template function (" + ScriptFunction.getDisplayableFunctionName(name) + ")");
		FauxTemplate_Terrain template = (FauxTemplate_Terrain) rawTemplate;
		ScriptValue returning = null;
		assert Debugger.addSnapNode("Template provided", template);
		assert Debugger.addSnapNode("Parameters provided", params);
		if (name == null || name.equals("")) {
			if (template == null) {
				template = (FauxTemplate_Terrain) this.createObject(ref, template);
			}
			assert Debugger.closeNode();
			return template;
		} else if (name.equals("getBrushDensityWeight")) {
			returning = Parser.getRiffDouble(ref.getEnvironment(), template.getTerrain().getBrushDensityWeight());
			assert Debugger.closeNode();
			return returning;
		} else if (name.equals("getElevationWeight")) {
			returning = Parser.getRiffDouble(ref.getEnvironment(), template.getTerrain().getElevationWeight());
			assert Debugger.closeNode();
			return returning;
		} else if (name.equals("getGroundCohesionWeight")) {
			returning = Parser.getRiffDouble(ref.getEnvironment(), template.getTerrain().getGroundCohesionWeight());
			assert Debugger.closeNode();
			return returning;
		} else if (name.equals("getTemperatureWeight")) {
			returning = Parser.getRiffDouble(ref.getEnvironment(), template.getTerrain().getTemperatureWeight());
			assert Debugger.closeNode();
			return returning;
		} else if (name.equals("getWaterDepthWeight")) {
			returning = Parser.getRiffDouble(ref.getEnvironment(), template.getTerrain().getWaterDepthWeight());
			assert Debugger.closeNode();
			return returning;
		} else if (name.equals("getBrushDensity")) {
			returning = Parser.getRiffDouble(ref.getEnvironment(), template.getTerrain().getBrushDensity());
			assert Debugger.closeNode();
			return returning;
		} else if (name.equals("getElevation")) {
			returning = Parser.getRiffDouble(ref.getEnvironment(), template.getTerrain().getElevation());
			assert Debugger.closeNode();
			return returning;
		} else if (name.equals("getGroundCohesion")) {
			returning = Parser.getRiffDouble(ref.getEnvironment(), template.getTerrain().getGroundCohesion());
			assert Debugger.closeNode();
			return returning;
		} else if (name.equals("getTemperature")) {
			returning = Parser.getRiffDouble(ref.getEnvironment(), template.getTerrain().getTemperature());
			assert Debugger.closeNode();
			return returning;
		} else if (name.equals("getWaterDepth")) {
			returning = Parser.getRiffDouble(ref.getEnvironment(), template.getTerrain().getWaterDepth());
			assert Debugger.closeNode();
			return returning;
		} else if (name.equals("setBrushDensityWeight")) {
			template.getTerrain().setBrushDensityWeight(Parser.getDouble(params.get(0)));
			assert Debugger.closeNode();
			return null;
		} else if (name.equals("setElevationWeight")) {
			template.getTerrain().setElevationWeight(Parser.getDouble(params.get(0)));
			assert Debugger.closeNode();
			return null;
		} else if (name.equals("setGroundCohesionWeight")) {
			template.getTerrain().setGroundCohesionWeight(Parser.getDouble(params.get(0)));
			assert Debugger.closeNode();
			return null;
		} else if (name.equals("setTemperatureWeight")) {
			template.getTerrain().setTemperatureWeight(Parser.getDouble(params.get(0)));
			assert Debugger.closeNode();
			return null;
		} else if (name.equals("setWaterDepthWeight")) {
			template.getTerrain().setWaterDepthWeight(Parser.getDouble(params.get(0)));
			assert Debugger.closeNode();
			return null;
		} else if (name.equals("setBrushDensity")) {
			template.getTerrain().setBrushDensity(Parser.getDouble(params.get(0)));
			if (params.size() == 2) {
				template.getTerrain().setBrushDensityWeight(Parser.getDouble(params.get(1)));
			}
			assert Debugger.closeNode();
			return null;
		} else if (name.equals("setElevation")) {
			template.getTerrain().setElevation(Parser.getDouble(params.get(0)));
			if (params.size() == 2) {
				template.getTerrain().setElevationWeight(Parser.getDouble(params.get(1)));
			}
			assert Debugger.closeNode();
			return null;
		} else if (name.equals("setGroundCohesion")) {
			template.getTerrain().setGroundCohesion(Parser.getDouble(params.get(0)));
			if (params.size() == 2) {
				template.getTerrain().setGroundCohesionWeight(Parser.getDouble(params.get(1)));
			}
			assert Debugger.closeNode();
			return null;
		} else if (name.equals("setTemperature")) {
			template.getTerrain().setTemperature(Parser.getDouble(params.get(0)));
			if (params.size() == 2) {
				template.getTerrain().setTemperatureWeight(Parser.getDouble(params.get(1)));
			}
			assert Debugger.closeNode();
			return null;
		} else if (name.equals("setWaterDepth")) {
			template.getTerrain().setWaterDepth(Parser.getDouble(params.get(0)));
			if (params.size() == 2) {
				template.getTerrain().setWaterDepthWeight(Parser.getDouble(params.get(1)));
			}
			assert Debugger.closeNode();
			return null;
		}
		returning = this.getExtendedFauxClass().execute(ref, name, params, template);
		assert Debugger.closeNode();
		return returning;
	}

	public Terrain getTerrain() {
		return this.terrain;
	}

	// addFauxFunction(name,ScriptValueType type,List<ScriptValue_Abstract>params,ScriptKeywordType permission,boolean isAbstract)
	// All functions must be defined here. All function bodies are defined in 'execute'.
	@Override
	public void initialize() throws Exception_Nodeable {
		assert Debugger.openNode("Faux Template Initializations", "Initializing terrain faux template");
		this.addConstructor(this.getType(), ScriptValueType.createEmptyParamList());
		this.disableFullCreation();
		this.getExtendedClass().initialize();
		this.addFauxFunction("getBrushDensityWeight", ScriptValueType.DOUBLE, ScriptValueType.createEmptyParamList(), ScriptKeywordType.PUBLIC, false, false);
		this.addFauxFunction("getElevationWeight", ScriptValueType.DOUBLE, ScriptValueType.createEmptyParamList(), ScriptKeywordType.PUBLIC, false, false);
		this.addFauxFunction("getGroundCohesionWeight", ScriptValueType.DOUBLE, ScriptValueType.createEmptyParamList(), ScriptKeywordType.PUBLIC, false, false);
		this.addFauxFunction("getTemperatureWeight", ScriptValueType.DOUBLE, ScriptValueType.createEmptyParamList(), ScriptKeywordType.PUBLIC, false, false);
		this.addFauxFunction("getWaterDepthWeight", ScriptValueType.DOUBLE, ScriptValueType.createEmptyParamList(), ScriptKeywordType.PUBLIC, false, false);
		this.addFauxFunction("getBrushDensity", ScriptValueType.DOUBLE, ScriptValueType.createEmptyParamList(), ScriptKeywordType.PUBLIC, false, false);
		this.addFauxFunction("getElevation", ScriptValueType.DOUBLE, ScriptValueType.createEmptyParamList(), ScriptKeywordType.PUBLIC, false, false);
		this.addFauxFunction("getGroundCohesion", ScriptValueType.DOUBLE, ScriptValueType.createEmptyParamList(), ScriptKeywordType.PUBLIC, false, false);
		this.addFauxFunction("getTemperature", ScriptValueType.DOUBLE, ScriptValueType.createEmptyParamList(), ScriptKeywordType.PUBLIC, false, false);
		this.addFauxFunction("getWaterDepth", ScriptValueType.DOUBLE, ScriptValueType.createEmptyParamList(), ScriptKeywordType.PUBLIC, false, false);
		List<ScriptValue> fxnParams = new LinkedList<ScriptValue>();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.DOUBLE));
		this.addFauxFunction("setBrushDensityWeight", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		this.addFauxFunction("setElevationWeight", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		this.addFauxFunction("setGroundCohesionWeight", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		this.addFauxFunction("setTemperatureWeight", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		this.addFauxFunction("setWaterDepthWeight", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		this.addFauxFunction("setBrushDensity", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		this.addFauxFunction("setElevation", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		this.addFauxFunction("setGroundCohesion", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		this.addFauxFunction("setTemperature", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		this.addFauxFunction("setWaterDepth", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		fxnParams = new LinkedList<ScriptValue>();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.DOUBLE));
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.DOUBLE));
		this.addFauxFunction("setBrushDensity", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		this.addFauxFunction("setElevation", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		this.addFauxFunction("setGroundCohesion", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		this.addFauxFunction("setTemperature", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		this.addFauxFunction("setWaterDepth", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		assert Debugger.closeNode();
	}

	// Define default constructor here
	@Override
	public ScriptTemplate instantiateTemplate() {
		return new FauxTemplate_Terrain(this.getEnvironment(), this.getType());
	}

	@Override
	public void nodificate() {
		assert Debugger.openNode("Terrain Faux Template");
		super.nodificate();
		assert Debugger.addNode(this.terrain);
		assert Debugger.closeNode();
	}

	public void setTerrain(Terrain terrain) {
		this.terrain = terrain;
	}
}
