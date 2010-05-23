package com.dafrito.script.templates;

import java.util.LinkedList;
import java.util.List;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.util.Scenario;
import com.dafrito.util.Terrestrial;
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

public class FauxTemplate_Scenario extends FauxTemplate {
    public static final String SCENARIOSTRING = "Scenario";
    private Scenario scenario;

    public FauxTemplate_Scenario(ScriptEnvironment env) {
        super(env, ScriptValueType.createType(env, SCENARIOSTRING), ScriptValueType.getObjectType(env), new LinkedList<ScriptValueType>(), false);
    }

    public FauxTemplate_Scenario(ScriptEnvironment env, ScriptValueType type) {
        super(env, type);
        this.scenario = new Scenario(env, new Terrestrial(env, 1), "Scenario");
    }

    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    public Scenario getScenario() {
        return this.scenario;
    }

    // Define default constructor here
    @Override
    public ScriptTemplate instantiateTemplate() {
        return new FauxTemplate_Scenario(getEnvironment(), getType());
    }

    // addFauxFunction(name,ScriptValueType
    // type,List<ScriptValue_Abstract>params,ScriptKeywordType
    // permission,boolean isAbstract)
    // All functions must be defined here. All function bodies are defined in
    // 'execute'.
    @Override
    public void initialize() throws Exception_Nodeable {
        assert LegacyDebugger.open("Faux Template Initializations", "Initializing scenario faux template");
        addConstructor(getType(), ScriptValueType.createEmptyParamList());
        List<ScriptValue_Abstract> fxnParams = new LinkedList<ScriptValue_Abstract>();
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.createType(
            getEnvironment(),
            FauxTemplate_Terrestrial.TERRESTRIALSTRING)));
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.STRING));
        addConstructor(getType(), fxnParams);
        disableFullCreation();
        getExtendedClass().initialize();
        addFauxFunction(
            "getName",
            ScriptValueType.STRING,
            ScriptValueType.createEmptyParamList(),
            ScriptKeywordType.PUBLIC,
            false,
            false);
        fxnParams = new LinkedList<ScriptValue_Abstract>();
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.STRING));
        addFauxFunction("setName", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
        addFauxFunction(
            "getTerrestrial",
            ScriptValueType.createType(getEnvironment(), FauxTemplate_Terrestrial.TERRESTRIALSTRING),
            ScriptValueType.createEmptyParamList(),
            ScriptKeywordType.PUBLIC,
            false,
            false);
        fxnParams = new LinkedList<ScriptValue_Abstract>();
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.createType(
            getEnvironment(),
            FauxTemplate_Terrestrial.TERRESTRIALSTRING)));
        addFauxFunction("setTerrestrial", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
        addFauxFunction(
            "getScheduler",
            ScriptValueType.createType(getEnvironment(), FauxTemplate_Scheduler.SCHEDULERSTRING),
            ScriptValueType.createEmptyParamList(),
            ScriptKeywordType.PUBLIC,
            false,
            false);
        assert LegacyDebugger.close();
    }

    // Function bodies are contained via a series of if statements in execute
    // Template will be null if the object is exactly of this type and is
    // constructing, and thus must be created then
    @SuppressWarnings("fallthrough")
    @Override
    public ScriptValue_Abstract execute(Referenced ref, String name, List<ScriptValue_Abstract> params,
        ScriptTemplate_Abstract rawTemplate) throws Exception_Nodeable {
        assert LegacyDebugger.open("Faux Template Executions", "Executing scenario faux template function ("
            + ScriptFunction.getDisplayableFunctionName(name)
            + ")");
        FauxTemplate_Scenario template = (FauxTemplate_Scenario)rawTemplate;
        ScriptValue_Abstract returning;
        assert LegacyDebugger.addSnapNode("Template provided", template);
        assert LegacyDebugger.addSnapNode("Parameters provided", params);
        if(name == null || name.equals("")) {
            if(template == null) {
                template = (FauxTemplate_Scenario)createObject(ref, template);
            }
            switch(params.size()) {
                case 2:
                    template.getScenario().setTerrestrial(Parser.getTerrestrial(params.get(0)));
                    template.getScenario().setName(Parser.getString(params.get(1)));
                case 0:
                    assert LegacyDebugger.close();
                    return template;
            }
        } else if(name.equals("getName")) {
            returning = Parser.getRiffString(ref.getEnvironment(), template.getScenario().getName());
            assert LegacyDebugger.close();
            return returning;
        } else if(name.equals("setName")) {
            template.getScenario().setName(Parser.getString(params.get(0)));
            assert LegacyDebugger.close();
            return null;
        } else if(name.equals("getTerrestrial")) {
            returning = Parser.getRiffTerrestrial(template.getScenario().getTerrestrial());
            assert LegacyDebugger.close();
            return returning;
        } else if(name.equals("setTerrestrial")) {
            template.getScenario().setTerrestrial(Parser.getTerrestrial(params.get(0)));
            assert LegacyDebugger.close();
            return null;
        } else if(name.equals("getScheduler")) {
            returning = Parser.getRiffScheduler(template.getScenario().getScheduler());
            assert LegacyDebugger.close();
            return returning;
        }
        returning = getExtendedFauxClass().execute(ref, name, params, template);
        assert LegacyDebugger.close();
        return returning;
    }

    // Nodeable implementation
    @Override
    public Object convert() {
        return this.scenario;
    }

    @Override
    public boolean nodificate() {
        assert LegacyDebugger.open("Scenario Faux Template");
        assert super.nodificate();
        assert LegacyDebugger.addNode(this.scenario);
        assert LegacyDebugger.close();
        return true;
    }
}
