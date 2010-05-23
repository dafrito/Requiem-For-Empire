package com.dafrito.script.types;

import com.dafrito.debug.Exception_Nodeable;

public class ScriptValueType_ValueDeferrer extends ScriptValueType {
    private ScriptValue_Abstract value;

    public ScriptValueType_ValueDeferrer(ScriptValue_Abstract value) {
        super(value.getEnvironment());
        this.value = value;
    }

    @Override
    public ScriptValueType getBaseType() throws Exception_Nodeable {
        return this.value.getType();
    }
}
