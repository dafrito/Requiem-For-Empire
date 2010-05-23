package com.dafrito.script.executable;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.script.Referenced;
import com.dafrito.script.ScriptElement;
import com.dafrito.script.types.ScriptValueType;
import com.dafrito.script.types.ScriptValue_Abstract;

public class ScriptExecutable_AssignValue extends ScriptElement implements ScriptExecutable, ScriptValue_Abstract {
    private ScriptValue_Abstract variable;
    private ScriptValue_Abstract value;

    public ScriptExecutable_AssignValue(Referenced ref, ScriptValue_Abstract lhs, ScriptValue_Abstract rhs) {
        super(ref);
        this.variable = lhs;
        this.value = rhs;
    }

    public ScriptValue_Abstract getLeft() {
        return this.variable;
    }

    // ScriptExecutable implementation
    public ScriptValue_Abstract execute() throws Exception_Nodeable {
        assert LegacyDebugger.open("Value-Assignment Expressions", "Assigning Value");
        assert LegacyDebugger.addSnapNode("Left variable", this.variable);
        assert LegacyDebugger.open("Retrieving value");
        assert LegacyDebugger.addSnapNode("Current value", this.value);
        ScriptValue_Abstract thisResult = this.value.getValue();
        assert LegacyDebugger.close();
        assert LegacyDebugger.addSnapNode("Right value", thisResult);
        thisResult = this.variable.setValue(this, thisResult);
        assert LegacyDebugger.close();
        return thisResult;
    }

    // ScriptValue_Abstract implementation
    public ScriptValueType getType() {
        return this.variable.getType();
    }

    public boolean isConvertibleTo(ScriptValueType type) {
        return this.variable.isConvertibleTo(type);
    }

    public ScriptValue_Abstract castToType(Referenced ref, ScriptValueType type) throws Exception_Nodeable {
        return this.variable.castToType(ref, type);
    }

    public ScriptValue_Abstract getValue() throws Exception_Nodeable {
        return execute();
    }

    public ScriptValue_Abstract setValue(Referenced ref, ScriptValue_Abstract value) throws Exception_Nodeable {
        assert LegacyDebugger.open("Value Assignments", "Setting assigment-expression's right-side value");
        assert LegacyDebugger.addSnapNode("Former value", this.value);
        this.value = value.castToType(this, getType());
        assert LegacyDebugger.addSnapNode("New value", this.value);
        ScriptValue_Abstract returning = execute();
        assert LegacyDebugger.close();
        return returning;
    }

    public boolean valuesEqual(Referenced ref, ScriptValue_Abstract rhs) throws Exception_Nodeable {
        return this.variable.valuesEqual(ref, rhs);
    }

    public int valuesCompare(Referenced ref, ScriptValue_Abstract rhs) throws Exception_Nodeable {
        return this.variable.valuesCompare(ref, rhs);
    }

    // Nodeable implementation
    @Override
    public boolean nodificate() {
        assert LegacyDebugger.open("Assignment Script Expression");
        assert super.nodificate();
        assert LegacyDebugger.addSnapNode("Left variable", this.variable);
        assert LegacyDebugger.addSnapNode("Right value", this.value);
        assert LegacyDebugger.close();
        return true;
    }
}
