package com.dafrito.script.templates;

import java.util.LinkedList;
import java.util.List;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.script.Referenced;
import com.dafrito.script.ScriptEnvironment;
import com.dafrito.script.ScriptFunction;
import com.dafrito.script.ScriptKeywordType;
import com.dafrito.script.ScriptTemplate;
import com.dafrito.script.ScriptTemplate_Abstract;
import com.dafrito.script.types.ScriptValueType;
import com.dafrito.script.types.ScriptValue_Abstract;
import com.dafrito.script.types.ScriptValue_Faux;

public class FauxTemplate_MovementEvaluator extends FauxTemplate {
    public static final String MOVEMENTEVALUATORSTRING = "MovementEvaluator";

    public FauxTemplate_MovementEvaluator(ScriptEnvironment env) {
        super(env, ScriptValueType.createType(env, FauxTemplate_MovementEvaluator.MOVEMENTEVALUATORSTRING), ScriptValueType.getObjectType(env), new LinkedList<ScriptValueType>(), true);
    }

    public FauxTemplate_MovementEvaluator(ScriptEnvironment env, ScriptValueType type) {
        super(env, type);
    }

    // Define default constructor here
    @Override
    public ScriptTemplate instantiateTemplate() {
        return new FauxTemplate_MovementEvaluator(getEnvironment(), getType());
    }

    // addFauxFunction(name,ScriptValueType
    // type,List<ScriptValue_Abstract>params,ScriptKeywordType
    // permission,boolean isAbstract)
    // All functions must be defined here. All function bodies are defined in
    // 'execute'.
    @Override
    public void initialize() throws Exception_Nodeable {
        assert LegacyDebugger.open("Faux Template Initializations", "Initializing movement evaluator faux template");
        disableFullCreation();
        getExtendedClass().initialize();
        List<ScriptValue_Abstract> params = new LinkedList<ScriptValue_Abstract>();
        params.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.createType(
            getEnvironment(),
            FauxTemplate_DiscreteRegion.DISCRETEREGIONSTRING)));
        params.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.createType(
            getEnvironment(),
            FauxTemplate_Asset.ASSETSTRING)));
        addFauxFunction("evaluateMovementCost", ScriptValueType.DOUBLE, params, ScriptKeywordType.PUBLIC, true, false);
        assert LegacyDebugger.close();
    }

    // Function bodies are contained via a series of if statements in execute
    // Template will be null if the object is exactly of this type and is
    // constructing, and thus must be created then
    @Override
    public ScriptValue_Abstract execute(Referenced ref, String name, List<ScriptValue_Abstract> params,
            ScriptTemplate_Abstract rawTemplate) throws Exception_Nodeable {
        assert LegacyDebugger.open("Faux Template Executions", "Executing movement evaluator faux template function ("
            + ScriptFunction.getDisplayableFunctionName(name)
            + ")");
        assert LegacyDebugger.addSnapNode("Template provided", rawTemplate);
        assert LegacyDebugger.addSnapNode("Parameters provided", params);
        ScriptValue_Abstract returning = getExtendedFauxClass().execute(ref, name, params, rawTemplate);
        assert LegacyDebugger.close();
        return returning;
    }

    // Nodeable implementation
    @Override
    public boolean nodificate() {
        assert LegacyDebugger.open("Movement evaluator faux template");
        assert super.nodificate();
        assert LegacyDebugger.close();
        return true;
    }
}
