package com.dafrito.script.executable;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.script.Referenced;
import com.dafrito.script.ScriptElement;
import com.dafrito.script.ScriptEnvironment;
import com.dafrito.script.ScriptKeywordType;
import com.dafrito.script.types.ScriptValueType;
import com.dafrito.script.types.ScriptValue_Abstract;
import com.dafrito.script.types.ScriptValue_Variable;


public class ScriptExecutable_CreateVariable extends ScriptValue_Variable implements ScriptExecutable, Referenced {
    private ScriptKeywordType permission;
    private String name;
    private ScriptElement reference;

    public ScriptExecutable_CreateVariable(Referenced ref, ScriptValueType type, String name,
        ScriptKeywordType permission) {
        super(ref.getEnvironment(), type, null);
        this.reference = ref.getDebugReference();
        this.name = name;
        this.permission = permission;
    }

    public String getName() {
        return this.name;
    }

    // Overloaded ScriptValue_Variable functions
    @Override
    public ScriptKeywordType getPermission() {
        return this.permission;
    }

    @Override
    public ScriptValue_Abstract setReference(Referenced ref, ScriptValue_Abstract value) throws Exception_Nodeable {
        return ((ScriptValue_Variable)execute()).setReference(ref, value);
    }

    // ScriptExecutable implementation
    public ScriptValue_Abstract execute() throws Exception_Nodeable {
        assert LegacyDebugger.open("Creating Variable (" + this.name + ")");
        ScriptValue_Variable value;
        getEnvironment().getCurrentObject().addVariable(
            this,
            this.name,
            value = new ScriptValue_Variable(getEnvironment(), getType(), getPermission()));
        assert LegacyDebugger.addSnapNode("Variable Created", value);
        assert LegacyDebugger.close();
        return value;
    }

    // ScriptValue_Abstract implementation
    @Override
    public ScriptEnvironment getEnvironment() {
        return getDebugReference().getEnvironment();
    }

    @Override
    public boolean isConvertibleTo(ScriptValueType type) {
        return ScriptValueType.isConvertibleTo(getEnvironment(), getType(), type);
    }

    @Override
    public ScriptValue_Abstract castToType(Referenced ref, ScriptValueType type) throws Exception_Nodeable {
        return getValue().castToType(ref, type);
    }

    @Override
    public ScriptValue_Abstract getValue() throws Exception_Nodeable {
        return execute().getValue();
    }

    @Override
    public ScriptValue_Abstract setValue(Referenced ref, ScriptValue_Abstract value) throws Exception_Nodeable {
        return execute().setValue(ref, value);
    }

    @Override
    public boolean valuesEqual(Referenced ref, ScriptValue_Abstract rhs) throws Exception_Nodeable {
        return getValue().valuesEqual(ref, rhs);
    }

    @Override
    public int valuesCompare(Referenced ref, ScriptValue_Abstract rhs) throws Exception_Nodeable {
        return getValue().valuesCompare(ref, rhs);
    }

    // Referenced implementation
    public ScriptElement getDebugReference() {
        return this.reference;
    }

    // Nodeable implementation
    @Override
    public boolean nodificate() {
        assert LegacyDebugger.open("Variable-Creation Script-Element (" + this.name + ")");
        assert super.nodificate();
        assert LegacyDebugger.close();
        return true;
    }
}
