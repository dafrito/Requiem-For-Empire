package com.dafrito.script.templates;

import java.util.LinkedList;
import java.util.List;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.script.Parser;
import com.dafrito.script.Referenced;
import com.dafrito.script.ScriptEnvironment;
import com.dafrito.script.ScriptFunction;
import com.dafrito.script.ScriptKeywordType;
import com.dafrito.script.ScriptTemplate_Abstract;
import com.dafrito.script.executable.ScriptExecutable_CallFunction;
import com.dafrito.script.types.ScriptValueType;
import com.dafrito.script.types.ScriptValue_Abstract;
import com.dafrito.script.types.ScriptValue_Faux;
import com.dafrito.util.RiffJavaToolbox;

public class FauxTemplate_RiffDali extends FauxTemplate {
    public static final String RIFFDALISTRING = "RiffDali";

    public FauxTemplate_RiffDali(ScriptEnvironment env) {
        super(env, ScriptValueType.createType(env, RIFFDALISTRING), ScriptValueType.getObjectType(env), new LinkedList<ScriptValueType>(), true);
    }

    // addFauxFunction(name,ScriptValueType
    // type,List<ScriptValue_Abstract>params,ScriptKeywordType
    // permission,boolean isAbstract)
    // All functions must be defined here. All function bodies are defined in
    // 'execute'.
    @Override
    public void initialize() throws Exception_Nodeable {
        assert LegacyDebugger.open("Faux Template Initializations", "Initializing RiffDali faux template");
        disableFullCreation();
        getExtendedClass().initialize();
        List<ScriptValue_Abstract> fxnParams = FauxTemplate.createEmptyParamList();
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.STRING));
        addFauxFunction(
            "parseColor",
            ScriptValueType.createType(getEnvironment(), FauxTemplate_Color.COLORSTRING),
            fxnParams,
            ScriptKeywordType.PUBLIC,
            false,
            true);
        fxnParams = FauxTemplate.createEmptyParamList();
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.createType(
            getEnvironment(),
            FauxTemplate_Panel.PANELSTRING)));
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.createType(
            getEnvironment(),
            FauxTemplate_List.LISTSTRING)));
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.createType(
            getEnvironment(),
            FauxTemplate_List.LISTSTRING)));
        addFauxFunction("paintPanel", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
        assert LegacyDebugger.close();
    }

    // Function bodies are contained via a series of if statements in execute
    // Template will be null if the object is exactly of this type and is
    // constructing, and thus must be created then
    @Override
    public ScriptValue_Abstract execute(Referenced ref, String name, List<ScriptValue_Abstract> params,
        ScriptTemplate_Abstract rawTemplate) throws Exception_Nodeable {
        assert LegacyDebugger.open("Faux Template Executions", "Executing RiffDali Faux Template Function ("
            + ScriptFunction.getDisplayableFunctionName(name)
            + ")");
        ScriptValue_Abstract returning = null;
        assert LegacyDebugger.addSnapNode("Template provided", rawTemplate);
        assert LegacyDebugger.addSnapNode("Parameters provided", params);
        if(name.equals("parseColor")) {
            returning = Parser.getRiffColor(getEnvironment(), RiffJavaToolbox.getColor(Parser.getString(params.get(0))));
        } else if(name.equals("paintPanel")) {
            List<ScriptValue_Abstract> list = Parser.getList(params.get(1));
            List<ScriptValue_Abstract> paramList = new LinkedList<ScriptValue_Abstract>();
            for(ScriptValue_Abstract value : list) {
                paramList.clear();
                paramList.add(value);
                ScriptExecutable_CallFunction.callFunction(
                    getEnvironment(),
                    ref,
                    params.get(0),
                    "drawRegion",
                    paramList);
            }
        } else {
            returning = getExtendedFauxClass().execute(ref, name, params, rawTemplate);
        }
        assert LegacyDebugger.close();
        return returning;
    }

    @Override
    public boolean nodificate() {
        assert LegacyDebugger.open("RiffDali Faux Template");
        assert super.nodificate();
        assert LegacyDebugger.close();
        return true;
    }
}
