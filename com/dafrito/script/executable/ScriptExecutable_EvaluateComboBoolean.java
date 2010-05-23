package com.dafrito.script.executable;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Exception_InternalError;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.debug.Exceptions.Exception_Nodeable_ClassCast;
import com.dafrito.script.Referenced;
import com.dafrito.script.ScriptElement;
import com.dafrito.script.ScriptOperator;
import com.dafrito.script.ScriptOperatorType;
import com.dafrito.script.types.ScriptValueType;
import com.dafrito.script.types.ScriptValue_Abstract;
import com.dafrito.script.types.ScriptValue_Boolean;

public class ScriptExecutable_EvaluateComboBoolean extends ScriptElement implements ScriptValue_Abstract,
        ScriptExecutable {
    private ScriptValue_Abstract lhs, rhs;
    private ScriptOperatorType operator;

    public ScriptExecutable_EvaluateComboBoolean(Referenced ref, ScriptValue_Abstract lhs, ScriptValue_Abstract rhs,
        ScriptOperatorType operator) {
        super(ref);
        this.lhs = lhs;
        this.rhs = rhs;
        this.operator = operator;
    }

    // ScriptExecutable implementation
    public ScriptValue_Abstract execute() throws Exception_Nodeable {
        assert LegacyDebugger.open("Combo-Boolean Evaluations", "Evaluating Combo-Boolean Expression ("
            + ScriptOperator.getName(this.operator)
            + ")");
        assert LegacyDebugger.addNode(this);
        if(this.lhs.isConvertibleTo(ScriptValueType.BOOLEAN)) {
            throw new Exception_Nodeable_ClassCast(this, this.lhs, ScriptValueType.BOOLEAN);
        }
        if(this.rhs.isConvertibleTo(ScriptValueType.BOOLEAN)) {
            throw new Exception_Nodeable_ClassCast(this, this.rhs, ScriptValueType.BOOLEAN);
        }
        ScriptValue_Boolean leftResult = (ScriptValue_Boolean)this.lhs.getValue();
        ScriptValue_Boolean rightResult = (ScriptValue_Boolean)this.rhs.getValue();
        switch(this.operator) {
            case AND:
                return new ScriptValue_Boolean(getEnvironment(), (leftResult.getBooleanValue() && rightResult.getBooleanValue()));
            case OR:
                return new ScriptValue_Boolean(getEnvironment(), (leftResult.getBooleanValue() || rightResult.getBooleanValue()));
            default:
                throw new Exception_InternalError("Unexpected operator");
        }
    }

    // ScriptValue_Abstract implementation
    public ScriptValueType getType() {
        return this.lhs.getType();
    }

    public boolean isConvertibleTo(ScriptValueType type) {
        return ScriptValueType.isConvertibleTo(getEnvironment(), getType(), type);
    }

    public ScriptValue_Abstract castToType(Referenced ref, ScriptValueType type) throws Exception_Nodeable {
        return getValue().castToType(ref, type);
    }

    public ScriptValue_Abstract getValue() throws Exception_Nodeable {
        return execute();
    }

    public ScriptValue_Abstract setValue(Referenced ref, ScriptValue_Abstract value) throws Exception_Nodeable {
        return getValue().setValue(ref, value);
    }

    public boolean valuesEqual(Referenced ref, ScriptValue_Abstract rightValue) throws Exception_Nodeable {
        return getValue().valuesEqual(ref, rightValue);
    }

    public int valuesCompare(Referenced ref, ScriptValue_Abstract rightValue) throws Exception_Nodeable {
        return getValue().valuesCompare(ref, rightValue);
    }

    // Nodeable implementation
    @Override
    public boolean nodificate() {
        assert LegacyDebugger.open("Combo Boolean Expression");
        assert super.nodificate();
        assert LegacyDebugger.addSnapNode("Left side", this.lhs);
        assert LegacyDebugger.addSnapNode("Right side", this.rhs);
        assert LegacyDebugger.close();
        return true;
    }
}
