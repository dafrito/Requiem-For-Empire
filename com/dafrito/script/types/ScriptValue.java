package com.dafrito.script.types;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.debug.Exceptions.Exception_Nodeable_ClassCast;
import com.dafrito.script.Referenced;
import com.dafrito.script.ScriptElement;
import com.dafrito.script.ScriptEnvironment;
import com.dafrito.script.ScriptKeywordType;

public abstract class ScriptValue extends ScriptElement implements ScriptValue_Abstract {

    protected ScriptValueType m_type;

    public ScriptValue(Referenced elem, ScriptValueType type) {
        super(elem);
        this.m_type = type;
    }

    public ScriptValueType getType() {
        return this.m_type;
    }

    public boolean isConvertibleTo(ScriptValueType type) {
        return ScriptValueType.isConvertibleTo(getEnvironment(), getType(), type);
    }

    public boolean ofExactType(ScriptValueType type) {
        return getType().equals(type);
    }

    public ScriptValue_Abstract castToType(Referenced ref, ScriptValueType type) throws Exception_Nodeable {
        if(isConvertibleTo(type)) {
            return this;
        }
        throw new Exception_Nodeable_ClassCast(ref, this, type);
    }

    // Abstracts
    public abstract boolean valuesEqual(Referenced ref, ScriptValue_Abstract rhs) throws Exception_Nodeable;

    public abstract int valuesCompare(Referenced ref, ScriptValue_Abstract rhs) throws Exception_Nodeable;

    public abstract ScriptValue_Abstract setValue(Referenced ref, ScriptValue_Abstract value) throws Exception_Nodeable;

    public abstract ScriptValue_Abstract getValue() throws Exception_Nodeable;

    // Static functions
    public static ScriptValue_Abstract createUninitializedObject(ScriptEnvironment env, ScriptValueType type) {
        if(type == null) {
            return null;
        }
        if(type.equals(ScriptKeywordType.BOOLEAN)) {
            return new ScriptValue_Boolean(env, false);
        } else if(type.equals(ScriptKeywordType.SHORT)) {
            return new ScriptValue_Numeric(env, (short)0);
        } else if(type.equals(ScriptKeywordType.INT)) {
            return new ScriptValue_Numeric(env, 0);
        } else if(type.equals(ScriptKeywordType.LONG)) {
            return new ScriptValue_Numeric(env, (long)0);
        } else if(type.equals(ScriptKeywordType.FLOAT)) {
            return new ScriptValue_Numeric(env, 0.0f);
        } else if(type.equals(ScriptKeywordType.DOUBLE)) {
            return new ScriptValue_Numeric(env, 0.0d);
        } else if(type.equals(ScriptKeywordType.STRING)) {
            return new ScriptValue_String(env, "");
        } else {
            return null;
        }
    }

    // Overloaded
    @Override
    public boolean nodificate() {
        assert LegacyDebugger.open("Script-value (" + getType() + ")");
        assert super.nodificate();
        assert LegacyDebugger.addNode("Reference: " + this);
        assert LegacyDebugger.close();
        return true;
    }
}
