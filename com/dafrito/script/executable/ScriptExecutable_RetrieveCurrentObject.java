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

public class ScriptExecutable_RetrieveCurrentObject extends ScriptValue_Variable implements ScriptExecutable,
        Referenced {
    private ScriptElement reference;

    public ScriptExecutable_RetrieveCurrentObject(Referenced ref, ScriptValueType type) {
        super(ref.getEnvironment(), type, null);
        this.reference = ref.getDebugReference();
    }

    public ScriptValue_Variable getVariable() {
        assert LegacyDebugger.addNode("Executing Current Object Retrieval");
        return new ScriptValue_Variable(getEnvironment(), getType(), getEnvironment().getCurrentObject(), getPermission());
    }

    // Overloaded ScriptValue_Variable functions
    @Override
    public ScriptKeywordType getPermission() {
        return ScriptKeywordType.PRIVATE;
    }

    @Override
    public ScriptValue_Abstract setReference(Referenced ref, ScriptValue_Abstract value) throws Exception_Nodeable {
        return getVariable().setReference(ref, value);
    }

    // ScriptExecutable implementation
    public ScriptValue_Abstract execute() throws Exception_Nodeable {
        return getValue();
    }

    // Abstract-value implementation
    @Override
    public boolean isConvertibleTo(ScriptValueType type) {
        return ScriptValueType.isConvertibleTo(getEnvironment(), getType(), type);
    }

    @Override
    public ScriptValue_Abstract castToType(Referenced ref, ScriptValueType type) throws Exception_Nodeable {
        return getVariable().castToType(ref, type);
    }

    @Override
    public ScriptValue_Abstract getValue() throws Exception_Nodeable {
        return getVariable().getValue();
    }

    @Override
    public ScriptValue_Abstract setValue(Referenced ref, ScriptValue_Abstract value) throws Exception_Nodeable {
        return getVariable().setValue(ref, value);
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

    @Override
    public ScriptEnvironment getEnvironment() {
        return getDebugReference().getEnvironment();
    }

    // Nodeable implementation
    @Override
    public boolean nodificate() {
        assert LegacyDebugger.open("Current Object Placeholder");
        assert super.nodificate();
        assert LegacyDebugger.close();
        return true;
    }
}
