package com.dafrito.script.executable;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.script.Referenced;
import com.dafrito.script.ScriptElement;
import com.dafrito.script.ScriptEnvironment;
import com.dafrito.script.ScriptKeywordType;
import com.dafrito.script.ScriptTemplate_Abstract;
import com.dafrito.script.types.ScriptValueType;
import com.dafrito.script.types.ScriptValue_Abstract;
import com.dafrito.script.types.ScriptValue_Variable;

public class ScriptExecutable_RetrieveVariable extends ScriptValue_Variable implements ScriptExecutable, Referenced {
    private String name;
    private ScriptValue_Abstract template;
    private ScriptElement reference;

    public ScriptExecutable_RetrieveVariable(Referenced ref, ScriptValue_Abstract template, String name,
        ScriptValueType type) {
        super(ref.getEnvironment(), type, null);
        this.reference = ref.getDebugReference();
        this.name = name;
        this.template = template;
    }

    public ScriptValue_Variable getVariable() throws Exception_Nodeable {
        assert LegacyDebugger.open("Executing Variable Retrieval (" + this.name + ")");
        ScriptValue_Variable variable;
        if(this.template != null) {
            assert LegacyDebugger.addSnapNode("Template", this.template);
            variable = ((ScriptTemplate_Abstract)this.template.getValue()).getVariable(this.name);
        } else {
            variable = getEnvironment().retrieveVariable(this.name);
        }
        assert variable != null : "Variable not found (" + this.name + ")";
        assert LegacyDebugger.close();
        return variable;
    }

    // Overloaded ScriptValue_Variable functions
    @Override
    public ScriptKeywordType getPermission() throws Exception_Nodeable {
        return getVariable().getPermission();
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
        assert LegacyDebugger.open("Variable-Placeholder (" + this.name + ")");
        assert super.nodificate();
        if(this.template != null) {
            assert LegacyDebugger.addSnapNode("Reference Template", this.template);
        }
        assert LegacyDebugger.close();
        return true;
    }
}
