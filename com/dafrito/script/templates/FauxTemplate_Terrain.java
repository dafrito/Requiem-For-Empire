package com.dafrito.script.templates;

import java.util.LinkedList;
import java.util.List;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.util.Terrain;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.script.Parser;
import com.dafrito.script.Referenced;
import com.dafrito.script.ScriptEnvironment;
import com.dafrito.script.ScriptFunction;
import com.dafrito.script.ScriptKeywordType;
import com.dafrito.script.ScriptTemplate;
import com.dafrito.script.ScriptTemplate_Abstract;
import com.dafrito.script.types.ScriptValueType;
import com.dafrito.script.types.ScriptValue_Abstract;
import com.dafrito.script.types.ScriptValue_Faux;

public class FauxTemplate_Terrain extends FauxTemplate {
    public static final String TERRAINSTRING = "Terrain";
    private Terrain terrain;

    public FauxTemplate_Terrain(ScriptEnvironment env) {
        super(env, ScriptValueType.createType(env, TERRAINSTRING), ScriptValueType.getObjectType(env), new LinkedList<ScriptValueType>(), false);
    }

    public FauxTemplate_Terrain(ScriptEnvironment env, ScriptValueType type) {
        super(env, type);
        this.terrain = new Terrain(env);
    }

    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }

    public Terrain getTerrain() {
        return this.terrain;
    }

    // Define default constructor here
    @Override
    public ScriptTemplate instantiateTemplate() {
        return new FauxTemplate_Terrain(getEnvironment(), getType());
    }

    // addFauxFunction(name,ScriptValueType
    // type,List<ScriptValue_Abstract>params,ScriptKeywordType
    // permission,boolean isAbstract)
    // All functions must be defined here. All function bodies are defined in
    // 'execute'.
    @Override
    public void initialize() throws Exception_Nodeable {
        assert LegacyDebugger.open("Faux Template Initializations", "Initializing terrain faux template");
        addConstructor(getType(), ScriptValueType.createEmptyParamList());
        disableFullCreation();
        getExtendedClass().initialize();
        addFauxFunction(
            "getBrushDensityWeight",
            ScriptValueType.DOUBLE,
            ScriptValueType.createEmptyParamList(),
            ScriptKeywordType.PUBLIC,
            false,
            false);
        addFauxFunction(
            "getElevationWeight",
            ScriptValueType.DOUBLE,
            ScriptValueType.createEmptyParamList(),
            ScriptKeywordType.PUBLIC,
            false,
            false);
        addFauxFunction(
            "getGroundCohesionWeight",
            ScriptValueType.DOUBLE,
            ScriptValueType.createEmptyParamList(),
            ScriptKeywordType.PUBLIC,
            false,
            false);
        addFauxFunction(
            "getTemperatureWeight",
            ScriptValueType.DOUBLE,
            ScriptValueType.createEmptyParamList(),
            ScriptKeywordType.PUBLIC,
            false,
            false);
        addFauxFunction(
            "getWaterDepthWeight",
            ScriptValueType.DOUBLE,
            ScriptValueType.createEmptyParamList(),
            ScriptKeywordType.PUBLIC,
            false,
            false);
        addFauxFunction(
            "getBrushDensity",
            ScriptValueType.DOUBLE,
            ScriptValueType.createEmptyParamList(),
            ScriptKeywordType.PUBLIC,
            false,
            false);
        addFauxFunction(
            "getElevation",
            ScriptValueType.DOUBLE,
            ScriptValueType.createEmptyParamList(),
            ScriptKeywordType.PUBLIC,
            false,
            false);
        addFauxFunction(
            "getGroundCohesion",
            ScriptValueType.DOUBLE,
            ScriptValueType.createEmptyParamList(),
            ScriptKeywordType.PUBLIC,
            false,
            false);
        addFauxFunction(
            "getTemperature",
            ScriptValueType.DOUBLE,
            ScriptValueType.createEmptyParamList(),
            ScriptKeywordType.PUBLIC,
            false,
            false);
        addFauxFunction(
            "getWaterDepth",
            ScriptValueType.DOUBLE,
            ScriptValueType.createEmptyParamList(),
            ScriptKeywordType.PUBLIC,
            false,
            false);
        List<ScriptValue_Abstract> fxnParams = new LinkedList<ScriptValue_Abstract>();
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.DOUBLE));
        addFauxFunction(
            "setBrushDensityWeight",
            ScriptValueType.VOID,
            fxnParams,
            ScriptKeywordType.PUBLIC,
            false,
            false);
        addFauxFunction("setElevationWeight", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
        addFauxFunction(
            "setGroundCohesionWeight",
            ScriptValueType.VOID,
            fxnParams,
            ScriptKeywordType.PUBLIC,
            false,
            false);
        addFauxFunction("setTemperatureWeight", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
        addFauxFunction("setWaterDepthWeight", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
        addFauxFunction("setBrushDensity", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
        addFauxFunction("setElevation", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
        addFauxFunction("setGroundCohesion", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
        addFauxFunction("setTemperature", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
        addFauxFunction("setWaterDepth", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
        fxnParams = new LinkedList<ScriptValue_Abstract>();
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.DOUBLE));
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.DOUBLE));
        addFauxFunction("setBrushDensity", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
        addFauxFunction("setElevation", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
        addFauxFunction("setGroundCohesion", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
        addFauxFunction("setTemperature", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
        addFauxFunction("setWaterDepth", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
        assert LegacyDebugger.close();
    }

    // Function bodies are contained via a series of if statements in execute
    // Template will be null if the object is exactly of this type and is
    // constructing, and thus must be created then
    @Override
    public ScriptValue_Abstract execute(Referenced ref, String name, List<ScriptValue_Abstract> params,
            ScriptTemplate_Abstract rawTemplate) throws Exception_Nodeable {
        assert LegacyDebugger.open("Faux Template Executions", "Executing terrain faux template function ("
            + ScriptFunction.getDisplayableFunctionName(name)
            + ")");
        FauxTemplate_Terrain template = (FauxTemplate_Terrain)rawTemplate;
        ScriptValue_Abstract returning = null;
        assert LegacyDebugger.addSnapNode("Template provided", template);
        assert LegacyDebugger.addSnapNode("Parameters provided", params);
        if(name == null || name.equals("")) {
            if(template == null) {
                template = (FauxTemplate_Terrain)createObject(ref, template);
            }
            assert LegacyDebugger.close();
            return template;
        } else if(name.equals("getBrushDensityWeight")) {
            returning = Parser.getRiffDouble(ref.getEnvironment(), template.getTerrain().getBrushDensityWeight());
            assert LegacyDebugger.close();
            return returning;
        } else if(name.equals("getElevationWeight")) {
            returning = Parser.getRiffDouble(ref.getEnvironment(), template.getTerrain().getElevationWeight());
            assert LegacyDebugger.close();
            return returning;
        } else if(name.equals("getGroundCohesionWeight")) {
            returning = Parser.getRiffDouble(ref.getEnvironment(), template.getTerrain().getGroundCohesionWeight());
            assert LegacyDebugger.close();
            return returning;
        } else if(name.equals("getTemperatureWeight")) {
            returning = Parser.getRiffDouble(ref.getEnvironment(), template.getTerrain().getTemperatureWeight());
            assert LegacyDebugger.close();
            return returning;
        } else if(name.equals("getWaterDepthWeight")) {
            returning = Parser.getRiffDouble(ref.getEnvironment(), template.getTerrain().getWaterDepthWeight());
            assert LegacyDebugger.close();
            return returning;
        } else if(name.equals("getBrushDensity")) {
            returning = Parser.getRiffDouble(ref.getEnvironment(), template.getTerrain().getBrushDensity());
            assert LegacyDebugger.close();
            return returning;
        } else if(name.equals("getElevation")) {
            returning = Parser.getRiffDouble(ref.getEnvironment(), template.getTerrain().getElevation());
            assert LegacyDebugger.close();
            return returning;
        } else if(name.equals("getGroundCohesion")) {
            returning = Parser.getRiffDouble(ref.getEnvironment(), template.getTerrain().getGroundCohesion());
            assert LegacyDebugger.close();
            return returning;
        } else if(name.equals("getTemperature")) {
            returning = Parser.getRiffDouble(ref.getEnvironment(), template.getTerrain().getTemperature());
            assert LegacyDebugger.close();
            return returning;
        } else if(name.equals("getWaterDepth")) {
            returning = Parser.getRiffDouble(ref.getEnvironment(), template.getTerrain().getWaterDepth());
            assert LegacyDebugger.close();
            return returning;
        } else if(name.equals("setBrushDensityWeight")) {
            template.getTerrain().setBrushDensityWeight(Parser.getDouble(params.get(0)));
            assert LegacyDebugger.close();
            return null;
        } else if(name.equals("setElevationWeight")) {
            template.getTerrain().setElevationWeight(Parser.getDouble(params.get(0)));
            assert LegacyDebugger.close();
            return null;
        } else if(name.equals("setGroundCohesionWeight")) {
            template.getTerrain().setGroundCohesionWeight(Parser.getDouble(params.get(0)));
            assert LegacyDebugger.close();
            return null;
        } else if(name.equals("setTemperatureWeight")) {
            template.getTerrain().setTemperatureWeight(Parser.getDouble(params.get(0)));
            assert LegacyDebugger.close();
            return null;
        } else if(name.equals("setWaterDepthWeight")) {
            template.getTerrain().setWaterDepthWeight(Parser.getDouble(params.get(0)));
            assert LegacyDebugger.close();
            return null;
        } else if(name.equals("setBrushDensity")) {
            template.getTerrain().setBrushDensity(Parser.getDouble(params.get(0)));
            if(params.size() == 2) {
                template.getTerrain().setBrushDensityWeight(Parser.getDouble(params.get(1)));
            }
            assert LegacyDebugger.close();
            return null;
        } else if(name.equals("setElevation")) {
            template.getTerrain().setElevation(Parser.getDouble(params.get(0)));
            if(params.size() == 2) {
                template.getTerrain().setElevationWeight(Parser.getDouble(params.get(1)));
            }
            assert LegacyDebugger.close();
            return null;
        } else if(name.equals("setGroundCohesion")) {
            template.getTerrain().setGroundCohesion(Parser.getDouble(params.get(0)));
            if(params.size() == 2) {
                template.getTerrain().setGroundCohesionWeight(Parser.getDouble(params.get(1)));
            }
            assert LegacyDebugger.close();
            return null;
        } else if(name.equals("setTemperature")) {
            template.getTerrain().setTemperature(Parser.getDouble(params.get(0)));
            if(params.size() == 2) {
                template.getTerrain().setTemperatureWeight(Parser.getDouble(params.get(1)));
            }
            assert LegacyDebugger.close();
            return null;
        } else if(name.equals("setWaterDepth")) {
            template.getTerrain().setWaterDepth(Parser.getDouble(params.get(0)));
            if(params.size() == 2) {
                template.getTerrain().setWaterDepthWeight(Parser.getDouble(params.get(1)));
            }
            assert LegacyDebugger.close();
            return null;
        }
        returning = getExtendedFauxClass().execute(ref, name, params, template);
        assert LegacyDebugger.close();
        return returning;
    }

    // Nodeable implementation
    @Override
    public Object convert() {
        return this.terrain;
    }

    @Override
    public boolean nodificate() {
        assert LegacyDebugger.open("Terrain Faux Template");
        assert super.nodificate();
        assert LegacyDebugger.addNode(this.terrain);
        assert LegacyDebugger.close();
        return true;
    }
}
