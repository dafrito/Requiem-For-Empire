package com.dafrito.script.templates;

import java.util.LinkedList;
import java.util.List;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.economy.ArchetypeMapNode;
import com.dafrito.economy.Asset;
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

public class FauxTemplate_ArchetypeTree extends FauxTemplate {
    public static final String ARCHETYPETREESTRING = "ArchetypeTree";
    private ArchetypeMapNode tree;

    public FauxTemplate_ArchetypeTree(ScriptEnvironment env) {
        super(env, ScriptValueType.createType(env, ARCHETYPETREESTRING), ScriptValueType.getObjectType(env), new LinkedList<ScriptValueType>(), false);
    }

    public FauxTemplate_ArchetypeTree(ScriptEnvironment env, ScriptValueType type) {
        super(env, type);
    }

    public ArchetypeMapNode getTree() {
        return this.tree;
    }

    public void setTree(ArchetypeMapNode tree) {
        this.tree = tree;
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
        assert LegacyDebugger.open("Faux Template Initializations", "Initializing archetype tree faux template");
        List<ScriptValue_Abstract> fxnParams = new LinkedList<ScriptValue_Abstract>();
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.createType(
            getEnvironment(),
            FauxTemplate_Archetype.ARCHETYPESTRING)));
        addConstructor(getType(), fxnParams);
        disableFullCreation();
        getExtendedClass().initialize();
        fxnParams = new LinkedList<ScriptValue_Abstract>();
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.createType(
            getEnvironment(),
            FauxTemplate_Asset.ASSETSTRING)));
        addFauxFunction("addAsset", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
        addFauxFunction(
            "getAssetsOfType",
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
        assert LegacyDebugger.open("Faux Template Executions", "Executing archetype tree faux template function ("
            + ScriptFunction.getDisplayableFunctionName(name)
            + ")");
        FauxTemplate_ArchetypeTree template = (FauxTemplate_ArchetypeTree)rawTemplate;
        assert LegacyDebugger.addSnapNode("Template provided", template);
        assert LegacyDebugger.addSnapNode("Parameters provided", params);
        if(name == null || name.equals("")) {
            if(template == null) {
                template = (FauxTemplate_ArchetypeTree)createObject(ref, template);
            }
            template.setTree(new ArchetypeMapNode(Parser.getArchetype(params.get(0))));
            params.clear();
        } else if(name.equals("addAsset")) {
            template.getTree().addAsset(Parser.getAsset(params.get(0)));
            assert LegacyDebugger.close();
            return null;
        } else if(name.equals("getAssetsOfType")) {
            List<ScriptValue_Abstract> assets = new LinkedList<ScriptValue_Abstract>();
            for(Asset asset : template.getTree().getAssetsOfType(Parser.getArchetype(params.get(0)))) {
                assets.add(Parser.getRiffAsset(asset));
            }
            ScriptValue_Abstract returning = Parser.getRiffList(getEnvironment(), assets);
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
        return this.tree;
    }

    @Override
    public boolean nodificate() {
        assert LegacyDebugger.open("Archetype Tree Faux Script-Element");
        assert super.nodificate();
        assert LegacyDebugger.addNode(this.tree);
        assert LegacyDebugger.close();
        return true;
    }
}
