package com.dafrito.script.templates;

import java.util.LinkedList;
import java.util.List;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.debug.Exceptions.Exception_Nodeable_FunctionNotFound;
import com.dafrito.script.Referenced;
import com.dafrito.script.ScriptEnvironment;
import com.dafrito.script.ScriptFunction;
import com.dafrito.script.ScriptKeyword;
import com.dafrito.script.ScriptTemplate;
import com.dafrito.script.ScriptTemplate_Abstract;
import com.dafrito.script.types.ScriptValueType;
import com.dafrito.script.types.ScriptValue_Abstract;

public class FauxTemplate_Object extends FauxTemplate {
    public static ScriptKeyword OBJECT;
    public static final String OBJECTSTRING = "Object";

    public FauxTemplate_Object(ScriptEnvironment env) {
        super(env, ScriptValueType.getObjectType(env), null, new LinkedList<ScriptValueType>(), true);
    }

    public FauxTemplate_Object(ScriptEnvironment env, ScriptValueType type) {
        super(env, type);
    }

    // Define default constructor here
    @Override
    public ScriptTemplate instantiateTemplate() {
        return new FauxTemplate_Object(getEnvironment(), getType());
    }

    // addFauxFunction(name,ScriptValueType
    // type,List<ScriptValue_Abstract>params,ScriptKeywordType
    // permission,boolean isAbstract)
    // All functions must be defined here. All function bodies are defined in
    // 'execute'.
    @Override
    public void initialize() throws Exception_Nodeable {
        assert LegacyDebugger.open("Faux Template Initializations", "Initializing object faux template");
        addConstructor(getType(), ScriptValueType.createEmptyParamList());
        disableFullCreation();
        assert LegacyDebugger.close();
    }

    // Function bodies are contained via a series of if statements in execute
    // Template will be null if the object is exactly of this type and is
    // constructing, and thus must be created then
    @Override
    public ScriptValue_Abstract execute(Referenced ref, String name, List<ScriptValue_Abstract> params,
        ScriptTemplate_Abstract rawTemplate) throws Exception_Nodeable {
        assert LegacyDebugger.open("Faux Template Executions", "Executing Object Faux Template Function ("
            + ScriptFunction.getDisplayableFunctionName(name)
            + ")");
        assert LegacyDebugger.addSnapNode("Template provided", rawTemplate);
        assert LegacyDebugger.addSnapNode("Parameters provided", params);
        if(name == null || name.equals("")) {
            if(params.size() != 0) {
                throw new Exception_Nodeable_FunctionNotFound(ref, name, params);
            }
            assert LegacyDebugger.close();
            if(rawTemplate == null) {
                return createObject(ref, rawTemplate);
            }
            return rawTemplate;
        }
        throw new Exception_Nodeable_FunctionNotFound(ref, name, params);
    }

    // ScriptConvertible and Nodeable implementations
    @Override
    public Object convert() {
        return this;
    }

    @Override
    public boolean nodificate() {
        assert LegacyDebugger.open("Object Faux Template");
        assert super.nodificate();
        assert LegacyDebugger.close();
        return true;
    }
}
