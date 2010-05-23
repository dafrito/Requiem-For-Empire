package com.dafrito.script.types;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Exception_InternalError;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.debug.Exceptions.Exception_Nodeable_IncomparableObjects;
import com.dafrito.script.Referenced;
import com.dafrito.script.ScriptElement;
import com.dafrito.script.executable.ScriptExecutable;

public class ScriptValue_Null extends ScriptElement implements ScriptExecutable, ScriptValue_Abstract {

    public ScriptValue_Null(Referenced ref) {
        super(ref);
    }

    public ScriptValue_Abstract execute() throws Exception_Nodeable {
        return this;
    }

    // ScriptValue_Abstract implementation
    public ScriptValueType getType() {
        return null;
    }

    public boolean isConvertibleTo(ScriptValueType type) {
        return true;
    }

    public ScriptValue_Abstract castToType(Referenced ref, ScriptValueType type) throws Exception_Nodeable {
        return this;
    }

    public ScriptValue_Abstract getValue() throws Exception_Nodeable {
        return this;
    }

    public ScriptValue_Abstract setValue(Referenced ref, ScriptValue_Abstract value) throws Exception_Nodeable {
        throw new Exception_InternalError("Set Value");
    }

    public boolean valuesEqual(Referenced ref, ScriptValue_Abstract rhs) throws Exception_Nodeable {
        return(rhs == null || rhs.getValue() == null || rhs.getValue() instanceof ScriptValue_Null);
    }

    public int valuesCompare(Referenced ref, ScriptValue_Abstract rhs) throws Exception_Nodeable {
        throw new Exception_Nodeable_IncomparableObjects(ref, this, rhs);
    }

    // Nodeable implementation
    @Override
    public boolean nodificate() {
        assert LegacyDebugger.addNode("Null Script-Value");
        return true;
    }
}
