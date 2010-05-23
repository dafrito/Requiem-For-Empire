package com.dafrito.script.templates;

import java.util.LinkedList;
import java.util.List;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.util.Terrestrial;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.geom.DiscreteRegion;
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

public class FauxTemplate_Terrestrial extends FauxTemplate {
    public static final String TERRESTRIALSTRING = "Terrestrial";
    private Terrestrial m_terrestrial;

    public FauxTemplate_Terrestrial(ScriptEnvironment env) {
        super(env, ScriptValueType.createType(env, TERRESTRIALSTRING), ScriptValueType.getObjectType(env), new LinkedList<ScriptValueType>(), false);
    }

    public FauxTemplate_Terrestrial(ScriptEnvironment env, ScriptValueType type) {
        super(env, type);
        setTerrestrial(new Terrestrial(env, 1));
    }

    public Terrestrial getTerrestrial() {
        return this.m_terrestrial;
    }

    public void setTerrestrial(Terrestrial terrestrial) {
        this.m_terrestrial = terrestrial;
    }

    // Define default constructor here
    @Override
    public ScriptTemplate instantiateTemplate() {
        return new FauxTemplate_Terrestrial(getEnvironment(), getType());
    }

    /**
     * addFauxFunction(name,ScriptValueType
     * type,List<ScriptValue_Abstract>params,ScriptKeywordType
     * permission,boolean isAbstract) All functions must be defined here. All
     * function bodies are defined in 'execute'.
     */
    @Override
    public void initialize() throws Exception_Nodeable {
        assert LegacyDebugger.open("Faux Template Initializations", "Initializing terrestrial faux template");
        addConstructor(getType(), ScriptValueType.createEmptyParamList());
        List<ScriptValue_Abstract> fxnParams = new LinkedList<ScriptValue_Abstract>();
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.DOUBLE));
        addConstructor(getType(), fxnParams);
        disableFullCreation();
        getExtendedClass().initialize();
        fxnParams = ScriptValueType.createEmptyParamList();
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.createType(
            getEnvironment(),
            FauxTemplate_DiscreteRegion.DISCRETEREGIONSTRING)));
        addFauxFunction("add", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
        fxnParams = ScriptValueType.createEmptyParamList();
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.createType(
            getEnvironment(),
            FauxTemplate_Scenario.SCENARIOSTRING)));
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.createType(
            getEnvironment(),
            FauxTemplate_MovementEvaluator.MOVEMENTEVALUATORSTRING)));
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.createType(
            getEnvironment(),
            FauxTemplate_Asset.ASSETSTRING)));
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.createType(
            getEnvironment(),
            FauxTemplate_Point.POINTSTRING)));
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.createType(
            getEnvironment(),
            FauxTemplate_Point.POINTSTRING)));
        addFauxFunction(
            "getPath",
            ScriptValueType.createType(getEnvironment(), FauxTemplate_Path.PATHSTRING),
            fxnParams,
            ScriptKeywordType.PUBLIC,
            false,
            false);
        assert LegacyDebugger.close();
    }

    // Function bodies are contained via a series of if statements in execute
    // Template will be null if the object is exactly of this type and is
    // constructing, and thus must be created then
    @Override
    public ScriptValue_Abstract execute(Referenced ref, String name, List<ScriptValue_Abstract> params,
        ScriptTemplate_Abstract rawTemplate) throws Exception_Nodeable {
        assert LegacyDebugger.open("Faux Template Executions", "Executing terrestrial faux template function ("
            + ScriptFunction.getDisplayableFunctionName(name)
            + ")");
        FauxTemplate_Terrestrial template = (FauxTemplate_Terrestrial)rawTemplate;
        ScriptValue_Abstract returning;
        assert LegacyDebugger.addSnapNode("Template provided", template);
        assert LegacyDebugger.addSnapNode("Parameters provided", params);
        if(name == null || name.equals("")) {
            if(template == null) {
                template = (FauxTemplate_Terrestrial)createObject(ref, template);
            }
            template.setTerrestrial(new Terrestrial(getEnvironment(), Parser.getDouble(params.get(0))));
            assert LegacyDebugger.close();
            return template;
        } else if(name.equals("add")) {
            DiscreteRegion region = Parser.getDiscreteRegion(params.get(0));
            assert LegacyDebugger.addSnapNode("Adding discrete region to terrestrial", region);
            template.getTerrestrial().add(region);
            assert LegacyDebugger.close();
            return null;
        } else if(name.equals("getPath")) {
            returning = Parser.getRiffPath(template.getTerrestrial().getPath(
                getEnvironment(),
                Parser.getScenario(params.get(0)),
                Parser.getTemplate(params.get(1)),
                Parser.getAsset(params.get(2)),
                Parser.getPoint(params.get(3)),
                Parser.getPoint(params.get(4))));
            assert LegacyDebugger.close();
            return returning;
        }
        returning = getExtendedFauxClass().execute(ref, name, params, template);
        assert LegacyDebugger.close();
        return returning;
    }

    // Nodeable and ScriptConvertible implementations
    @Override
    public Object convert() {
        return getTerrestrial();
    }

    @Override
    public boolean nodificate() {
        assert LegacyDebugger.open("Terrestrial Faux Template");
        assert super.nodificate();
        assert LegacyDebugger.close();
        return true;
    }
}
