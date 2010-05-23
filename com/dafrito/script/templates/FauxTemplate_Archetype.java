package com.dafrito.script.templates;

import java.util.LinkedList;
import java.util.List;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.economy.Ace;
import com.dafrito.economy.Archetype;
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

public class FauxTemplate_Archetype extends FauxTemplate {
    public static final String ARCHETYPESTRING = "Archetype";
    private Archetype archetype;

    public FauxTemplate_Archetype(ScriptEnvironment env) {
        super(env, ScriptValueType.createType(env, ARCHETYPESTRING), ScriptValueType.getObjectType(env), new LinkedList<ScriptValueType>(), false);
    }

    public FauxTemplate_Archetype(ScriptEnvironment env, ScriptValueType type) {
        super(env, type);
    }

    public Archetype getArchetype() {
        return this.archetype;
    }

    public void setArchetype(Archetype archetype) {
        this.archetype = archetype;
    }

    // Define default constructor here
    @Override
    public ScriptTemplate instantiateTemplate() {
        return new FauxTemplate_Archetype(getEnvironment(), getType());
    }

    // All functions must be defined here. All function bodies are defined in
    // 'execute'.
    @Override
    public void initialize() throws Exception_Nodeable {
        assert LegacyDebugger.open("Faux Template Initializations", "Initializing archetype faux template");
        List<ScriptValue_Abstract> fxnParams = new LinkedList<ScriptValue_Abstract>();
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.STRING));
        addConstructor(getType(), fxnParams);
        disableFullCreation();
        getExtendedClass().initialize();
        fxnParams = new LinkedList<ScriptValue_Abstract>();
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.createType(
            getEnvironment(),
            FauxTemplate_Ace.ACESTRING)));
        addFauxFunction("addParent", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
        addFauxFunction(
            "getName",
            ScriptValueType.STRING,
            new LinkedList<ScriptValue_Abstract>(),
            ScriptKeywordType.PUBLIC,
            false,
            false);
        addFauxFunction(
            "getParents",
            ScriptValueType.createType(getEnvironment(), FauxTemplate_List.LISTSTRING),
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
        assert LegacyDebugger.open("Faux Template Executions", "Executing archetype faux template function ("
            + ScriptFunction.getDisplayableFunctionName(name)
            + ")");
        FauxTemplate_Archetype template = (FauxTemplate_Archetype)rawTemplate;
        assert LegacyDebugger.addSnapNode("Template provided", template);
        assert LegacyDebugger.addSnapNode("Parameters provided", params);
        if(name == null || name.equals("")) {
            if(template == null) {
                template = (FauxTemplate_Archetype)createObject(ref, template);
            }
            template.setArchetype(new Archetype(getEnvironment(), Parser.getString(params.get(0))));
            params.clear();
        } else if(name.equals("addParent")) {
            template.getArchetype().addParent(Parser.getAce(params.get(0)));
            assert LegacyDebugger.close();
            return null;
        } else if(name.equals("getName")) {
            ScriptValue_Abstract returning = Parser.getRiffString(getEnvironment(), template.getArchetype().getName());
            assert LegacyDebugger.close();
            return returning;
        } else if(name.equals("getParents")) {
            List<ScriptValue_Abstract> parents = new LinkedList<ScriptValue_Abstract>();
            for(Ace parent : template.getArchetype().getParents()) {
                parents.add(Parser.getRiffAce(parent));
            }
            assert LegacyDebugger.close();
            return null;
        }
        ScriptValue_Abstract returning = getExtendedFauxClass().execute(ref, name, params, template);
        assert LegacyDebugger.close();
        return returning;
    }

    // Nodeable and ScriptConvertible interfaces
    @Override
    public Object convert() {
        return this.archetype;
    }

    @Override
    public boolean nodificate() {
        assert LegacyDebugger.open("Archetype Faux Script-Element");
        assert super.nodificate();
        assert LegacyDebugger.addNode(this.archetype);
        assert LegacyDebugger.close();
        return true;
    }
}
