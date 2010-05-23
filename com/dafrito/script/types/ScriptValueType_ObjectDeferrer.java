package com.dafrito.script.types;

import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.script.ScriptEnvironment;

public class ScriptValueType_ObjectDeferrer extends ScriptValueType {

    private ScriptValue_Abstract template;
    private String name;

    public ScriptValueType_ObjectDeferrer(ScriptValue_Abstract template, String name) {
        this(template.getEnvironment(), template, name);
    }

    public ScriptValueType_ObjectDeferrer(ScriptEnvironment env, ScriptValue_Abstract template, String name) {
        super(env);
        assert name != null;
        this.template = template;
        this.name = name;
    }

    @Override
    public ScriptValueType getBaseType() throws Exception_Nodeable {
        if(this.template != null) {
            return getEnvironment().getTemplate(this.template.getType()).getVariable(this.name).getType();
        }
        return getEnvironment().retrieveVariable(this.name).getType();
    }
}
