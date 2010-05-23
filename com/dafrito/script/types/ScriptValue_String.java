package com.dafrito.script.types;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.debug.Nodeable;
import com.dafrito.debug.Exceptions.Exception_Nodeable_ClassCast;
import com.dafrito.script.Referenced;
import com.dafrito.script.ScriptConvertible;
import com.dafrito.script.ScriptEnvironment;

public class ScriptValue_String implements ScriptValue_Abstract, ScriptConvertible, Nodeable {
    private String value;
    private final ScriptEnvironment environment;

    public ScriptValue_String(ScriptEnvironment env, String string) {
        this.environment = env;
        this.value = string;
    }

    public String getStringValue() {
        return this.value;
    }

    // Abstract-value implementation
    public ScriptEnvironment getEnvironment() {
        return this.environment;
    }

    public ScriptValueType getType() {
        return ScriptValueType.STRING;
    }

    public boolean isConvertibleTo(ScriptValueType type) {
        return getType().equals(type);
    }

    public ScriptValue_Abstract castToType(Referenced ref, ScriptValueType type) throws Exception_Nodeable {
        assert LegacyDebugger.addNode("Type Casting", "Casting (" + getType() + " to " + type + ")");
        if(getType().equals(type)) {
            return this;
        }
        throw new Exception_Nodeable_ClassCast(ref, this, type);
    }

    public ScriptValue_Abstract getValue() throws Exception_Nodeable {
        return this;
    }

    public ScriptValue_Abstract setValue(Referenced ref, ScriptValue_Abstract value) throws Exception_Nodeable {
        assert LegacyDebugger.open("Value Assignments", "Setting String Value");
        assert LegacyDebugger.addSnapNode("Former value", this);
        this.value = ((ScriptValue_String)value.castToType(ref, getType())).getStringValue();
        assert LegacyDebugger.close("New value", this);
        return this;
    }

    public boolean valuesEqual(Referenced ref, ScriptValue_Abstract rhs) throws Exception_Nodeable {
        return getStringValue().equals(((ScriptValue_String)rhs.castToType(ref, getType())).getStringValue());
    }

    public int valuesCompare(Referenced ref, ScriptValue_Abstract rhs) throws Exception_Nodeable {
        return getStringValue().compareTo(((ScriptValue_String)rhs.castToType(ref, getType())).getStringValue());
    }

    // Interface implementations
    public Object convert() {
        return new String(this.value);
    }

    public boolean nodificate() {
        assert LegacyDebugger.open("String Script-Value (" + getStringValue().length() + " character(s): " + getStringValue());
        assert LegacyDebugger.addSnapNode("Reference", super.toString());
        assert LegacyDebugger.close();
        return true;
    }
}
