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
import com.dafrito.script.ScriptTemplate;
import com.dafrito.script.ScriptTemplate_Abstract;
import com.dafrito.script.types.ScriptValueType;
import com.dafrito.script.types.ScriptValue_Abstract;
import com.dafrito.script.types.ScriptValue_Faux;

public class FauxTemplate_List extends FauxTemplate {
    public static final String LISTSTRING = "List";
    private List<ScriptValue_Abstract> list = new LinkedList<ScriptValue_Abstract>();

    public FauxTemplate_List(ScriptEnvironment env) {
        super(env, ScriptValueType.createType(env, LISTSTRING), ScriptValueType.getObjectType(env), new LinkedList<ScriptValueType>(), false);
    }

    public FauxTemplate_List(ScriptEnvironment env, ScriptValueType type) {
        super(env, type);
    }

    public List<ScriptValue_Abstract> getList() {
        return this.list;
    }

    public void setList(List<ScriptValue_Abstract> list) {
        this.list = list;
    }

    // Define default constructor here
    @Override
    public ScriptTemplate instantiateTemplate() {
        return new FauxTemplate_List(getEnvironment(), getType());
    }

    // addFauxFunction(name,ScriptValueType
    // type,List<ScriptValue_Abstract>params,ScriptKeywordType
    // permission,boolean isAbstract)
    // All functions must be defined here. All function bodies are defined in
    // 'execute'.
    @Override
    public void initialize() throws Exception_Nodeable {
        assert LegacyDebugger.open("Faux Template Initializations", "Initializing list faux template");
        addConstructor(getType(), ScriptValueType.createEmptyParamList());
        disableFullCreation();
        getExtendedClass().initialize();
        List<ScriptValue_Abstract> fxnParams = FauxTemplate.createEmptyParamList();
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.getObjectType(getEnvironment())));
        addFauxFunction("add", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
        fxnParams = FauxTemplate.createEmptyParamList();
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), getType()));
        addFauxFunction("addAll", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
        fxnParams = FauxTemplate.createEmptyParamList();
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.INT));
        addFauxFunction(
            "get",
            ScriptValueType.getObjectType(getEnvironment()),
            fxnParams,
            ScriptKeywordType.PUBLIC,
            false,
            false);
        fxnParams = FauxTemplate.createEmptyParamList();
        addFauxFunction("size", ScriptValueType.INT, fxnParams, ScriptKeywordType.PUBLIC, false, false);
        assert LegacyDebugger.close();
    }

    // Function bodies are contained via a series of if statements in execute
    // Template will be null if the object is exactly of this type and is
    // constructing, and thus must be created then
    @Override
    public ScriptValue_Abstract execute(Referenced ref, String name, List<ScriptValue_Abstract> params,
        ScriptTemplate_Abstract rawTemplate) throws Exception_Nodeable {
        assert LegacyDebugger.open("Faux Template Executions", "Executing List Faux Template Function ("
            + ScriptFunction.getDisplayableFunctionName(name)
            + ")");
        FauxTemplate_List template = (FauxTemplate_List)rawTemplate;
        ScriptValue_Abstract returning;
        assert LegacyDebugger.addSnapNode("Template provided", template);
        assert LegacyDebugger.addSnapNode("Parameters provided", params);
        if(name == null || name.equals("")) {
            if(template == null) {
                template = (FauxTemplate_List)createObject(ref, template);
            }
            params.clear();
        } else if(name.equals("add")) {
            template.getList().add(params.get(0).getValue());
            assert LegacyDebugger.close();
            return null;
        } else if(name.equals("addAll")) {
            template.getList().addAll(Parser.getList(params.get(0)));
            assert LegacyDebugger.close();
            return null;
        } else if(name.equals("get")) {
            assert LegacyDebugger.close();
            return template.getList().get(Parser.getInteger(params.get(0)));
        } else if(name.equals("size")) {
            assert LegacyDebugger.close();
            return Parser.getRiffInt(getEnvironment(), template.getList().size());
        }
        returning = getExtendedFauxClass().execute(ref, name, params, template);
        assert LegacyDebugger.close();
        return returning;
    }

    @Override
    public Object convert() {
        return this.list;
    }

    @Override
    public boolean nodificate() {
        if(this.list == null) {
            assert LegacyDebugger.open("List Faux Template (0 element(s))");
        } else {
            assert LegacyDebugger.open("List Faux Template (" + this.list.size() + " element(s))");
        }
        assert super.nodificate();
        assert LegacyDebugger.addSnapNode("Elements", this.list);
        assert LegacyDebugger.close();
        return true;
    }
}
