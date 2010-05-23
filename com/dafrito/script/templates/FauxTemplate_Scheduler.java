package com.dafrito.script.templates;

import java.util.LinkedList;
import java.util.List;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.util.Scheduler;
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

public class FauxTemplate_Scheduler extends FauxTemplate {
    public static final String SCHEDULERSTRING = "Scheduler";
    private Scheduler scheduler;

    public FauxTemplate_Scheduler(ScriptEnvironment env) {
        super(env, ScriptValueType.createType(env, SCHEDULERSTRING), ScriptValueType.getObjectType(env), new LinkedList<ScriptValueType>(), false);
    }

    public FauxTemplate_Scheduler(ScriptEnvironment env, ScriptValueType type) {
        super(env, type);
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public Scheduler getScheduler() {
        return this.scheduler;
    }

    // Define default constructor here
    @Override
    public ScriptTemplate instantiateTemplate() {
        return new FauxTemplate_Scheduler(getEnvironment(), getType());
    }

    // addFauxFunction(name,ScriptValueType
    // type,List<ScriptValue_Abstract>params,ScriptKeywordType
    // permission,boolean isAbstract)
    // All functions must be defined here. All function bodies are defined in
    // 'execute'.
    @Override
    public void initialize() throws Exception_Nodeable {
        assert LegacyDebugger.open("Faux Template Initializations", "Initializing scheduler faux template");
        addConstructor(getType(), ScriptValueType.createEmptyParamList());
        List<ScriptValue_Abstract> params = new LinkedList<ScriptValue_Abstract>();
        params.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.createType(
            getEnvironment(),
            FauxTemplate_SchedulerListener.SCHEDULERLISTENERSTRING)));
        addConstructor(getType(), params);
        disableFullCreation();
        getExtendedClass().initialize();
        params = new LinkedList<ScriptValue_Abstract>();
        params.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.LONG));
        params.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.createType(
            getEnvironment(),
            FauxTemplate_Asset.ASSETSTRING)));
        addFauxFunction("schedule", ScriptValueType.VOID, params, ScriptKeywordType.PUBLIC, false, false);
        params = new LinkedList<ScriptValue_Abstract>();
        params.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.LONG));
        params.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.createType(
            getEnvironment(),
            FauxTemplate_Asset.ASSETSTRING)));
        params.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.createType(
            getEnvironment(),
            FauxTemplate_SchedulerListener.SCHEDULERLISTENERSTRING)));
        addFauxFunction("schedule", ScriptValueType.VOID, params, ScriptKeywordType.PUBLIC, false, false);
        params = new LinkedList<ScriptValue_Abstract>();
        params.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.createType(
            getEnvironment(),
            FauxTemplate_SchedulerListener.SCHEDULERLISTENERSTRING)));
        addFauxFunction("setDefaultListener", ScriptValueType.VOID, params, ScriptKeywordType.PUBLIC, false, false);
        params = new LinkedList<ScriptValue_Abstract>();
        addFauxFunction("start", ScriptValueType.VOID, params, ScriptKeywordType.PUBLIC, false, false);
        assert LegacyDebugger.close();
    }

    // Function bodies are contained via a series of if statements in execute
    @SuppressWarnings("fallthrough")
    // Template will be null if the object is exactly of this type and is
    // constructing, and thus must be created then
    @Override
    public ScriptValue_Abstract execute(Referenced ref, String name, List<ScriptValue_Abstract> params,
            ScriptTemplate_Abstract rawTemplate) throws Exception_Nodeable {
        assert LegacyDebugger.open("Faux Template Executions", "Executing scheduler faux template function ("
            + ScriptFunction.getDisplayableFunctionName(name)
            + ")");
        FauxTemplate_Scheduler template = (FauxTemplate_Scheduler)rawTemplate;
        ScriptValue_Abstract returning;
        assert LegacyDebugger.addSnapNode("Template provided", template);
        assert LegacyDebugger.addSnapNode("Parameters provided", params);
        if(name == null || name.equals("")) {
            if(template == null) {
                template = (FauxTemplate_Scheduler)createObject(ref, template);
            }
            switch(params.size()) {
                case 1:
                    template.getScheduler().setDefaultListener(Parser.getSchedulerListener(params.get(0)));
                case 0:
                    assert LegacyDebugger.close();
                    return template;
            }
        } else if(name.equals("schedule")) {
            ScriptTemplate_Abstract listener = null;
            if(params.size() == 3) {
                listener = Parser.getTemplate(params.get(2));
            }
            template.getScheduler().schedule(
                Parser.getNumber(params.get(0)).longValue(),
                Parser.getAsset(params.get(1)),
                listener);
            assert LegacyDebugger.close();
            return null;
        } else if(name.equals("setDefaultListener")) {
            template.getScheduler().setDefaultListener(Parser.getSchedulerListener(params.get(0)));
            assert LegacyDebugger.close();
            return null;
        } else if(name.equals("start")) {
            template.getScheduler().start();
            assert LegacyDebugger.close();
            return null;
        }
        returning = getExtendedFauxClass().execute(ref, name, params, template);
        assert LegacyDebugger.close();
        return returning;
    }

    // Nodeable implementation
    @Override
    public Object convert() {
        return this.scheduler;
    }

    @Override
    public boolean nodificate() {
        assert LegacyDebugger.open("Scheduler Faux Template");
        assert super.nodificate();
        assert LegacyDebugger.addNode(this.scheduler);
        assert LegacyDebugger.close();
        return true;
    }
}
