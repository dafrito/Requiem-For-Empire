package com.dafrito.script.templates;

import java.util.LinkedList;
import java.util.List;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.economy.Ace;
import com.dafrito.economy.Asset;
import com.dafrito.script.Parser;
import com.dafrito.script.Referenced;
import com.dafrito.script.ScriptConvertible;
import com.dafrito.script.ScriptEnvironment;
import com.dafrito.script.ScriptFunction;
import com.dafrito.script.ScriptKeywordType;
import com.dafrito.script.ScriptTemplate;
import com.dafrito.script.ScriptTemplate_Abstract;
import com.dafrito.script.types.ScriptValueType;
import com.dafrito.script.types.ScriptValue_Abstract;
import com.dafrito.script.types.ScriptValue_Faux;

public class FauxTemplate_Asset extends FauxTemplate {
    public static final String ASSETSTRING = "Asset";
    private Asset asset;

    public FauxTemplate_Asset(ScriptEnvironment env) {
        super(env, ScriptValueType.createType(env, ASSETSTRING), ScriptValueType.getObjectType(env), new LinkedList<ScriptValueType>(), false);
    }

    public FauxTemplate_Asset(ScriptEnvironment env, ScriptValueType type) {
        super(env, type);
        this.asset = new Asset(env);
    }

    public Asset getAsset() {
        return this.asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    // Define default constructor here
    @Override
    public ScriptTemplate instantiateTemplate() {
        return new FauxTemplate_Asset(getEnvironment(), getType());
    }

    // All functions must be defined here. All function bodies are defined in
    // 'execute'.
    @Override
    public void initialize() throws Exception_Nodeable {
        assert LegacyDebugger.open("Faux Template Initializations", "Initializing asset faux template");
        List<ScriptValue_Abstract> fxnParams = new LinkedList<ScriptValue_Abstract>();
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.createType(
            getEnvironment(),
            FauxTemplate_Point.POINTSTRING)));
        addConstructor(getType(), fxnParams);
        disableFullCreation();
        getExtendedClass().initialize();
        fxnParams = new LinkedList<ScriptValue_Abstract>();
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.STRING));
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.getObjectType(getEnvironment())));
        addFauxFunction("setProperty", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
        fxnParams = new LinkedList<ScriptValue_Abstract>();
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.STRING));
        addFauxFunction(
            "getProperty",
            ScriptValueType.getObjectType(getEnvironment()),
            fxnParams,
            ScriptKeywordType.PUBLIC,
            false,
            false);
        fxnParams = new LinkedList<ScriptValue_Abstract>();
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.createType(
            getEnvironment(),
            FauxTemplate_Ace.ACESTRING)));
        addFauxFunction("addAce", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
        fxnParams = new LinkedList<ScriptValue_Abstract>();
        addFauxFunction(
            "getAces",
            ScriptValueType.createType(getEnvironment(), FauxTemplate_List.LISTSTRING),
            fxnParams,
            ScriptKeywordType.PUBLIC,
            false,
            false);
        fxnParams = new LinkedList<ScriptValue_Abstract>();
        addFauxFunction(
            "getLocation",
            ScriptValueType.createType(getEnvironment(), FauxTemplate_Point.POINTSTRING),
            fxnParams,
            ScriptKeywordType.PUBLIC,
            false,
            false);
        fxnParams = new LinkedList<ScriptValue_Abstract>();
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.createType(
            getEnvironment(),
            FauxTemplate_Point.POINTSTRING)));
        addFauxFunction("setLocation", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
        assert LegacyDebugger.close();
    }

    // Function bodies are contained via a series of if statements in execute
    // Template will be null if the object is exactly of this type and is
    // constructing, and thus must be created then
    @Override
    public ScriptValue_Abstract execute(Referenced ref, String name, List<ScriptValue_Abstract> params,
        ScriptTemplate_Abstract rawTemplate) throws Exception_Nodeable {
        assert LegacyDebugger.open("Faux Template Executions", "Executing Asset Faux Template Function ("
            + ScriptFunction.getDisplayableFunctionName(name)
            + ")");
        FauxTemplate_Asset template = (FauxTemplate_Asset)rawTemplate;
        assert LegacyDebugger.addSnapNode("Template provided", template);
        assert LegacyDebugger.addSnapNode("Parameters provided", params);
        if(name == null || name.equals("")) {
            if(template == null) {
                template = (FauxTemplate_Asset)createObject(ref, template);
            }
            template.getAsset().setLocation(Parser.getPoint(params.get(0)));
            params.clear();
        } else if(name.equals("setProperty")) {
            if(params.size() == 2) {
                template.getAsset().setProperty(Parser.getString(params.get(0)), params.get(1).getValue());
                assert LegacyDebugger.close();
                return null;
            }
        } else if(name.equals("getProperty")) {
            if(params.size() == 1) {
                ScriptValue_Abstract value = (ScriptValue_Abstract)((ScriptConvertible)template.getAsset().getProperty(
                    Parser.getString(params.get(0)))).convert();
                assert LegacyDebugger.close();
                return value;
            }
        } else if(name.equals("addAce")) {
            template.getAsset().addAce(Parser.getAce(params.get(0)));
            assert LegacyDebugger.close();
            return null;
        } else if(name.equals("getAces")) {
            List<ScriptValue_Abstract> list = new LinkedList<ScriptValue_Abstract>();
            for(Ace ace : template.getAsset().getAces()) {
                list.add(Parser.getRiffAce(ace));
            }
            ScriptValue_Abstract returning = Parser.getRiffList(getEnvironment(), list);
            assert LegacyDebugger.close();
            return returning;
        } else if(name.equals("getLocation")) {
            assert template.getAsset().getLocation() != null : "Asset location is null!";
            ScriptValue_Abstract returning = Parser.getRiffPoint(template.getAsset().getLocation());
            assert LegacyDebugger.close();
            return returning;
        } else if(name.equals("setLocation")) {
            template.getAsset().setLocation(Parser.getPoint(params.get(0)));
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
        return this.asset;
    }

    @Override
    public boolean nodificate() {
        assert LegacyDebugger.open("Asset Faux Script-Element");
        assert super.nodificate();
        assert LegacyDebugger.addNode(this.asset);
        assert LegacyDebugger.close();
        return true;
    }
}
