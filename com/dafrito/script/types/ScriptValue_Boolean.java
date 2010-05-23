package com.dafrito.script.types;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.debug.Nodeable;
import com.dafrito.debug.Exceptions.Exception_Nodeable_ClassCast;
import com.dafrito.debug.Exceptions.Exception_Nodeable_IncomparableObjects;
import com.dafrito.script.Referenced;
import com.dafrito.script.ScriptConvertible;
import com.dafrito.script.ScriptEnvironment;

public class ScriptValue_Boolean implements ScriptConvertible, ScriptValue_Abstract, Nodeable {
    private boolean value;
    private final ScriptEnvironment environment;

    public ScriptValue_Boolean(ScriptEnvironment env, boolean value) {
        this.environment = env;
        this.value = value;
    }

    public boolean getBooleanValue() {
        return this.value;
    }

    // Abstract-value implementation
    public ScriptEnvironment getEnvironment() {
        return this.environment;
    }

    public ScriptValueType getType() {
        return ScriptValueType.BOOLEAN;
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
        assert LegacyDebugger.open("Value Assignments", "Setting Boolean Value");
        assert LegacyDebugger.addSnapNode("Former value", this);
        this.value = ((ScriptValue_Boolean)value.castToType(ref, getType())).getBooleanValue();
        assert LegacyDebugger.close("New value", this);
        return this;
    }

    public boolean valuesEqual(Referenced ref, ScriptValue_Abstract rhs) throws Exception_Nodeable {
        return ((ScriptValue_Boolean)getValue()).getBooleanValue() == ((ScriptValue_Boolean)rhs.castToType(
            ref,
            getType())).getBooleanValue();
    }

    public int valuesCompare(Referenced ref, ScriptValue_Abstract rhs) throws Exception_Nodeable {
        throw new Exception_Nodeable_IncomparableObjects(ref, this, rhs);
    }

    // Overloaded and miscellaneous functions
    public Object convert() {
        return new Boolean(this.value);
    }

    public boolean nodificate() {
        assert LegacyDebugger.open("Boolean Script Value (" + getBooleanValue() + ")");
        assert LegacyDebugger.addNode("Reference: " + this);
        assert LegacyDebugger.close();
        return true;
    }
}
