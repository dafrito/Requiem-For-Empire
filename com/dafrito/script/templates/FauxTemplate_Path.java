package com.dafrito.script.templates;

import java.util.LinkedList;
import java.util.List;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.geom.Point_Path;
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

public class FauxTemplate_Path extends FauxTemplate_Point {
    public static final String PATHSTRING = "Path";

    public FauxTemplate_Path(ScriptEnvironment env) {
        super(env, ScriptValueType.createType(env, PATHSTRING), ScriptValueType.createType(
            env,
            FauxTemplate_Point.POINTSTRING), new LinkedList<ScriptValueType>(), false);
    }

    public FauxTemplate_Path(ScriptEnvironment env, ScriptValueType type) {
        super(env, type);
    }

    // Define default constructor here
    @Override
    public ScriptTemplate instantiateTemplate() {
        return new FauxTemplate_Path(getEnvironment(), getType());
    }

    /**
     * All functions must be defined here. All function bodies are defined in
     * 'execute'.
     */
    @Override
    public void initialize() throws Exception_Nodeable {
        assert LegacyDebugger.open("Faux Template Initializations", "Initializing path faux template");
        addConstructor(getType(), ScriptValueType.createEmptyParamList());
        List<ScriptValue_Abstract> fxnParams = new LinkedList<ScriptValue_Abstract>();
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.createType(
            getEnvironment(),
            FauxTemplate_Scenario.SCENARIOSTRING)));
        addConstructor(getType(), fxnParams);
        disableFullCreation();
        getExtendedClass().initialize();
        addFauxFunction(
            "getTotalTime",
            ScriptValueType.LONG,
            new LinkedList<ScriptValue_Abstract>(),
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
        assert LegacyDebugger.open("Faux Template Executions", String.format(
            "Executing Path Faux Template Function (%s)",
            ScriptFunction.getDisplayableFunctionName(name)));
        FauxTemplate_Path template = (FauxTemplate_Path)rawTemplate;
        assert LegacyDebugger.addSnapNode("Template provided", template);
        assert LegacyDebugger.addSnapNode("Parameters provided", params);
        if(name == null || name.equals("")) {
            if(template == null) {
                template = (FauxTemplate_Path)createObject(ref, template);
            }
            if(params.size() == 1) {
                ((Point_Path)template.getPoint()).setScenario(Parser.getScenario(params.get(0)));
            }
            params.clear();
        } else if(name.equals("getTotalTime")) {
            ScriptValue_Abstract returning = Parser.getRiffLong(
                getEnvironment(),
                ((Point_Path)template.getPoint()).getTotalTime());
            assert LegacyDebugger.close();
            return returning;
        }
        ScriptValue_Abstract returning = getExtendedFauxClass().execute(ref, name, params, template);
        assert LegacyDebugger.close();
        return returning;
    }

    // Nodeable and ScriptConvertible interfaces
    @Override
    public Object convert() {
        return getPoint();
    }

    @Override
    public boolean nodificate() {
        assert LegacyDebugger.open("Path Faux Script-Element");
        assert super.nodificate();
        assert LegacyDebugger.close();
        return true;
    }
}
