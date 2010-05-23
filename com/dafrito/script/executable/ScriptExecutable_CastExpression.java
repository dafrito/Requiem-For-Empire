package com.dafrito.script.executable;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.script.Referenced;
import com.dafrito.script.ScriptElement;
import com.dafrito.script.types.ScriptValueType;
import com.dafrito.script.types.ScriptValue_Abstract;

public class ScriptExecutable_CastExpression extends ScriptElement implements ScriptExecutable, ScriptValue_Abstract {
    private ScriptExecutable castExpression;
    private ScriptValueType type;

    public ScriptExecutable_CastExpression(Referenced ref, ScriptValueType type, ScriptExecutable exec) {
        super(ref);
        this.castExpression = exec;
        this.type = type;
    }

    // ScriptExecutable implementation
    public ScriptValue_Abstract execute() throws Exception_Nodeable {
        ScriptValue_Abstract left = this.castExpression.execute().getValue();
        assert LegacyDebugger.open("Cast Expression Executions", "Executing cast to " + getType());
        assert LegacyDebugger.addSnapNode("Value", left);
        ScriptValue_Abstract value = this.castExpression.execute().castToType(this, getType());
        assert LegacyDebugger.close();
        return value;
    }

    // Abstract-value implementation
    public ScriptValueType getType() {
        return this.type;
    }

    public boolean isConvertibleTo(ScriptValueType testType) {
        return ScriptValueType.isConvertibleTo(getEnvironment(), getType(), testType);
    }

    public ScriptValue_Abstract castToType(Referenced ref, ScriptValueType testType) throws Exception_Nodeable {
        return getValue().castToType(ref, testType);
    }

    public ScriptValue_Abstract getValue() throws Exception_Nodeable {
        return execute();
    }

    public ScriptValue_Abstract setValue(Referenced ref, ScriptValue_Abstract value) throws Exception_Nodeable {
        return getValue().setValue(ref, value);
    }

    public boolean valuesEqual(Referenced ref, ScriptValue_Abstract rhs) throws Exception_Nodeable {
        return getValue().valuesEqual(ref, rhs);
    }

    public int valuesCompare(Referenced ref, ScriptValue_Abstract rhs) throws Exception_Nodeable {
        return getValue().valuesCompare(ref, rhs);
    }

    // Nodeable implementation
    @Override
    public boolean nodificate() {
        assert LegacyDebugger.open("Script Cast Expression (To type: " + getType() + ")");
        assert super.nodificate();
        assert LegacyDebugger.addSnapNode("Cast Expression", this.castExpression);
        assert LegacyDebugger.close();
        return true;
    }
}
