package com.dafrito.script.types;

import com.dafrito.logging.DebugString;
import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.debug.Nodeable;
import com.dafrito.debug.Exceptions.Exception_Nodeable_ClassCast;
import com.dafrito.script.Referenced;
import com.dafrito.script.ScriptEnvironment;
import com.dafrito.script.ScriptKeywordType;

public class ScriptValue_Variable implements ScriptValue_Abstract, Nodeable {

    private ScriptValue_Abstract value;
    private final ScriptKeywordType permission;
    private final ScriptEnvironment environment;
    private final ScriptValueType type;

    /*
     * @throws Exception_Nodeable Thrown by subclasses
     */
    public ScriptValue_Variable(ScriptEnvironment env, ScriptValueType type, ScriptKeywordType permission) {
        this(env, type, null, permission);
    }

    /*
     * @throws Exception_Nodeable Thrown by subclasses
     */
    public ScriptValue_Variable(ScriptEnvironment env, ScriptValue_Abstract value, ScriptKeywordType permission) {
        this(env, value.getType(), value, permission);
    }

    public ScriptValue_Variable(ScriptEnvironment env, ScriptValueType type, ScriptValue_Abstract value,
        ScriptKeywordType permission) {
        this.environment = env;
        this.permission = permission;
        this.type = type;
        if(value == null) {
            this.value = ScriptValue.createUninitializedObject(env, type);
        } else {
            this.value = value;
        }
    }

    public ScriptValue_Abstract setReference(Referenced ref, ScriptValue_Abstract value) throws Exception_Nodeable {
        assert LegacyDebugger.open("Reference Assignments", "Setting Variable Reference");
        if(!ScriptValueType.isPrimitiveType(getType())) {
            assert LegacyDebugger.addNode("Assigning reference");
            assert LegacyDebugger.addSnapNode("Variable", this);
            assert LegacyDebugger.open("Retrieving value");
            ScriptValue_Abstract valueResult = value.getValue();
            assert LegacyDebugger.close("Value", value);
            if(valueResult == null) {
                this.value = null;
            } else {
                this.value = valueResult.castToType(ref, this.getType());
            }
            assert LegacyDebugger.close("Reference assignment operation completed", this);
            return this.value;
        }
        assert LegacyDebugger.open("Assigning value...");
        this.value = this.value.setValue(ref, value.castToType(ref, getType()));
        assert LegacyDebugger.close();
        assert LegacyDebugger.close("Value assignment operation completed", this);
        return this.value;
    }

    /**
     * @return The permission
     * @throws Exception_Nodeable Thrown by subclasses.
     */
    public ScriptKeywordType getPermission() throws Exception_Nodeable {
        return this.permission;
    }

    // Abstract-value implementation
    public ScriptEnvironment getEnvironment() {
        return this.environment;
    }

    public ScriptValueType getType() {
        return this.type;
    }

    public boolean isConvertibleTo(ScriptValueType referenceType) {
        return ScriptValueType.isConvertibleTo(getEnvironment(), getType(), referenceType);
    }

    public ScriptValue_Abstract castToType(Referenced ref, ScriptValueType referenceType) throws Exception_Nodeable {
        if(this.isConvertibleTo(referenceType)) {
            return new ScriptValue_Variable(getEnvironment(), getType(), getValue(), getPermission());
        }
        throw new Exception_Nodeable_ClassCast(ref, this, referenceType);
    }

    public ScriptValue_Abstract getValue() throws Exception_Nodeable {
        assert LegacyDebugger.open("Variable Value Retrievals", "Retrieving Variable's Value");
        assert LegacyDebugger.addNode(this);
        ScriptValue_Abstract returning;
        if(this.value != null) {
            returning = this.value.getValue();
        } else {
            returning = null;
        }
        assert LegacyDebugger.addSnapNode("Value", returning);
        assert LegacyDebugger.close();
        return returning;
    }

    public ScriptValue_Abstract setValue(Referenced ref, ScriptValue_Abstract value) throws Exception_Nodeable {
        return setReference(ref, value);
    }

    public boolean valuesEqual(Referenced ref, ScriptValue_Abstract rhs) throws Exception_Nodeable {
        if(this.value == null || this.value.getValue() == null || this.value.getValue() instanceof ScriptValue_Null) {
            return(rhs == null || rhs instanceof ScriptValue_Null);
        }
        return getValue().valuesEqual(ref, rhs);
    }

    public int valuesCompare(Referenced ref, ScriptValue_Abstract rhs) throws Exception_Nodeable {
        return getValue().valuesCompare(ref, rhs);
    }

    public boolean nodificate() {
        assert LegacyDebugger.open("Script Variable (" + getType() + ")");
        if(this.value != null) {
            assert LegacyDebugger.addSnapNode("Referenced element (" + this.value.getType() + ")", this.value);
        } else {
            assert LegacyDebugger.addNode(DebugString.REFERENCEDELEMENTNULL);
        }
        if(this.permission == null) {
            LegacyDebugger.addNode(DebugString.PERMISSIONNULL);
        } else {
            switch(this.permission) {
                case PRIVATE:
                    assert LegacyDebugger.addNode(DebugString.PERMISSIONPRIVATE);
                    break;
                case PROTECTED:
                    LegacyDebugger.addNode(DebugString.PERMISSIONPROTECTED);
                    break;
                case PUBLIC:
                    LegacyDebugger.addNode(DebugString.PERMISSIONPUBLIC);
                    break;
                default:
                    throw new UnsupportedOperationException("Unexpected default");
            }
        }
        assert LegacyDebugger.close();
        return true;
    }
}
