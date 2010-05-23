package com.dafrito.script.types;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Exception_InternalError;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.debug.Nodeable;
import com.dafrito.script.Referenced;
import com.dafrito.script.ScriptEnvironment;

public class ScriptValue_Faux implements Nodeable, ScriptValue_Abstract {
    private final ScriptEnvironment m_environment;
    private final ScriptValueType m_type;

    public ScriptValue_Faux(ScriptEnvironment env, ScriptValueType type) {
        this.m_environment = env;
        this.m_type = type;
    }

    public ScriptEnvironment getEnvironment() {
        return this.m_environment;
    }

    public ScriptValueType getType() {
        return this.m_type;
    }

    public boolean isConvertibleTo(ScriptValueType type) {
        return ScriptValueType.isConvertibleTo(getEnvironment(), getType(), type);
    }

    public ScriptValue_Abstract castToType(Referenced ref, ScriptValueType type) throws Exception_Nodeable {
        throw new Exception_InternalError(getEnvironment(), "Invalid call in ScriptValue_Faux");
    }

    public ScriptValue_Abstract setValue(Referenced ref, ScriptValue_Abstract value) throws Exception_Nodeable {
        throw new Exception_InternalError(getEnvironment(), "Invalid call in ScriptValue_Faux");
    }

    public ScriptValue_Abstract getValue() throws Exception_Nodeable {
        throw new Exception_InternalError(getEnvironment(), "Invalid call in ScriptValue_Faux");
    }

    public boolean valuesEqual(Referenced ref, ScriptValue_Abstract rhs) throws Exception_Nodeable {
        throw new Exception_InternalError(getEnvironment(), "Invalid call in ScriptValue_Faux");
    }

    public int valuesCompare(Referenced ref, ScriptValue_Abstract rhs) throws Exception_Nodeable {
        throw new Exception_InternalError(getEnvironment(), "Invalid call in ScriptValue_Faux");
    }

    public boolean nodificate() {
        assert LegacyDebugger.addNode("Faux Script-Value (" + getType() + ")");
        return true;
    }
}
