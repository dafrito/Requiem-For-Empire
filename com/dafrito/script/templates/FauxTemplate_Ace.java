package com.dafrito.script.templates;

import java.util.LinkedList;
import java.util.List;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.economy.Ace;
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

public class FauxTemplate_Ace extends FauxTemplate  {
    public static final String ACESTRING = "Ace";
    private Ace ace;

    public FauxTemplate_Ace(ScriptEnvironment env) {
        super(env, ScriptValueType.createType(env, ACESTRING), ScriptValueType.getObjectType(env), new LinkedList<ScriptValueType>(), false);
    }

    public FauxTemplate_Ace(ScriptEnvironment env, ScriptValueType type) {
        super(env, type);
    }

    public Ace getAce() {
        return this.ace;
    }

    public void setAce(Ace ace) {
        this.ace = ace;
    }

    // Define default constructor here
    @Override
    public ScriptTemplate instantiateTemplate() {
        return new FauxTemplate_Ace(getEnvironment(), getType());
    }

    // All functions must be defined here. All function bodies are defined in
    // 'execute'.
    @Override
    public void initialize() throws Exception_Nodeable {
        assert LegacyDebugger.open("Faux Template Initializations", "Initializing ace faux template");
        List<ScriptValue_Abstract> fxnParams = new LinkedList<ScriptValue_Abstract>();
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.createType(
            getEnvironment(),
            FauxTemplate_Archetype.ARCHETYPESTRING)));
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.DOUBLE));
        addConstructor(getType(), fxnParams);
        disableFullCreation();
        getExtendedClass().initialize();
        fxnParams = new LinkedList<ScriptValue_Abstract>();
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.DOUBLE));
        addFauxFunction("setEfficiency", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
        fxnParams = new LinkedList<ScriptValue_Abstract>();
        addFauxFunction(
            "getEfficiency",
            ScriptValueType.DOUBLE,
            new LinkedList<ScriptValue_Abstract>(),
            ScriptKeywordType.PUBLIC,
            false,
            false);
        addFauxFunction("getArchetype", ScriptValueType.createType(
            getEnvironment(),
            FauxTemplate_Archetype.ARCHETYPESTRING), fxnParams, ScriptKeywordType.PUBLIC, false, false);
        assert LegacyDebugger.close();
    }

    // Function bodies are contained via a series of if statements in execute
    // Template will be null if the object is exactly of this type and is
    // constructing, and thus must be created then
    @Override
    public ScriptValue_Abstract execute(Referenced ref, String name, List<ScriptValue_Abstract> params,
        ScriptTemplate_Abstract rawTemplate) throws Exception_Nodeable {
        assert LegacyDebugger.open("Faux Template Executions", "Executing ace faux template function ("
            + ScriptFunction.getDisplayableFunctionName(name)
            + ")");
        FauxTemplate_Ace template = (FauxTemplate_Ace)rawTemplate;
        assert LegacyDebugger.addSnapNode("Template provided", template);
        assert LegacyDebugger.addSnapNode("Parameters provided", params);
        if(name == null || name.equals("")) {
            if(template == null) {
                template = (FauxTemplate_Ace)createObject(ref, template);
            }
            template.setAce(new Ace(getEnvironment(), Parser.getArchetype(params.get(0)), Parser.getDouble(params.get(1))));
            params.clear();
        } else if(name.equals("setEfficiency")) {
            template.getAce().setEfficiency(Parser.getDouble(params.get(0)));
            assert LegacyDebugger.close();
            return null;
        } else if(name.equals("getEfficiency")) {
            ScriptValue_Abstract returning = Parser.getRiffDouble(getEnvironment(), template.getAce().getEfficiency());
            assert LegacyDebugger.close();
            return returning;
        } else if(name.equals("getArchetype")) {
            ScriptValue_Abstract returning = Parser.getRiffArchetype(template.getAce().getArchetype());
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
        return this.ace;
    }

    @Override
    public boolean nodificate() {
        assert LegacyDebugger.open("Ace Faux Script-Element");
        assert super.nodificate();
        assert LegacyDebugger.addNode(this.ace);
        assert LegacyDebugger.close();
        return true;
    }
}
