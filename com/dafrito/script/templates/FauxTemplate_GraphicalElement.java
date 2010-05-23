package com.dafrito.script.templates;

import java.util.LinkedList;
import java.util.List;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.script.Referenced;
import com.dafrito.script.ScriptEnvironment;
import com.dafrito.script.ScriptFunction;
import com.dafrito.script.ScriptTemplate;
import com.dafrito.script.ScriptTemplate_Abstract;
import com.dafrito.script.types.ScriptValueType;
import com.dafrito.script.types.ScriptValue_Abstract;

public class FauxTemplate_GraphicalElement extends FauxTemplate {
    public static final String GRAPHICALELEMENTSTRING = "GraphicalElement";

    public FauxTemplate_GraphicalElement(ScriptEnvironment env, ScriptValueType type, ScriptValueType extended,
        List<ScriptValueType> implemented, boolean isAbstract) {
        super(env, type, extended, implemented, isAbstract);
    }

    public FauxTemplate_GraphicalElement(ScriptEnvironment env) {
        super(env, ScriptValueType.createType(env, GRAPHICALELEMENTSTRING), ScriptValueType.getObjectType(env), new LinkedList<ScriptValueType>(), true);
    }

    public FauxTemplate_GraphicalElement(ScriptEnvironment env, ScriptValueType type) {
        super(env, type);
    }

    // Define default constructor here
    @Override
    public ScriptTemplate instantiateTemplate() {
        return new FauxTemplate_GraphicalElement(getEnvironment(), getType());
    }

    // All functions must be defined here. All function bodies are defined in
    // 'execute'.
    @Override
    public void initialize() throws Exception_Nodeable {
        assert LegacyDebugger.open("Faux Template Initializations", "Initializing graphical element faux template");
        addConstructor(getType(), ScriptValueType.createEmptyParamList());
        disableFullCreation();
        getExtendedClass().initialize();
        assert LegacyDebugger.close();
    }

    // Function bodies are contained via a series of if statements in execute
    // Template will be null if the object is exactly of this type and is
    // constructing, and thus must be created then
    @Override
    public ScriptValue_Abstract execute(Referenced ref, String name, List<ScriptValue_Abstract> params,
        ScriptTemplate_Abstract rawTemplate) throws Exception_Nodeable {
        assert LegacyDebugger.open("Faux Template Executions", "Executing Graphical Element Faux Template Function ("
            + ScriptFunction.getDisplayableFunctionName(name)
            + ")");
        FauxTemplate_GraphicalElement template = (FauxTemplate_GraphicalElement)rawTemplate;
        ScriptValue_Abstract returning;
        assert LegacyDebugger.addSnapNode("Template provided", template);
        assert LegacyDebugger.addSnapNode("Parameters provided", params);
        returning = getExtendedFauxClass().execute(ref, name, params, template);
        assert LegacyDebugger.close();
        return returning;
    }

    // Nodeable implementation
    @Override
    public boolean nodificate() {
        assert LegacyDebugger.open("Graphical Element Faux Template");
        assert super.nodificate();
        assert LegacyDebugger.close();
        return true;
    }
}
