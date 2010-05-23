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

public class ScriptExecutable_EvaluateBoolean extends ScriptElement implements ScriptExecutable, ScriptValue_Abstract {
    private ScriptValue_Abstract lhs, rhs;
    private ScriptOperatorType comparison;

    public ScriptExecutable_EvaluateBoolean(Referenced ref, ScriptValue_Abstract lhs, ScriptValue_Abstract rhs,
        ScriptOperatorType comparison) {
        super(ref);
        this.lhs = lhs;
        this.rhs = rhs;
        this.comparison = comparison;
    }

    // ScriptExecutable implementation
    public ScriptValue_Abstract execute() throws Exception_Nodeable {
        assert LegacyDebugger.open("Boolean Evaluations", "Executing Boolean Evaluation");
        assert LegacyDebugger.addNode(this);
        ScriptValue_Abstract returning = null;
        assert LegacyDebugger.open("Getting left value");
        assert LegacyDebugger.addSnapNode("Left before resolution", this.lhs);
        ScriptValue_Abstract leftResult = this.lhs.getValue();
        assert LegacyDebugger.addSnapNode("Left", leftResult);
        assert LegacyDebugger.close();
        assert LegacyDebugger.open("Getting right value");
        assert LegacyDebugger.addSnapNode("Right before resolution", this.rhs);
        ScriptValue_Abstract rightResult = this.rhs.getValue();
        assert LegacyDebugger.addSnapNode("Right", rightResult);
        assert LegacyDebugger.close();
        switch(this.comparison) {
            case NONEQUIVALENCY:
                returning = new ScriptValue_Boolean(getEnvironment(), !leftResult.valuesEqual(this, rightResult));
                break;
            case LESS:
                returning = new ScriptValue_Boolean(getEnvironment(), (leftResult.valuesCompare(this, rightResult) < 0));
                break;
            case LESSEQUALS:
                returning = new ScriptValue_Boolean(getEnvironment(), (leftResult.valuesCompare(this, rightResult) <= 0));
                break;
            case EQUIVALENCY:
                returning = new ScriptValue_Boolean(getEnvironment(), leftResult.valuesEqual(this, rightResult));
                break;
            case GREATEREQUALS:
                returning = new ScriptValue_Boolean(getEnvironment(), (leftResult.valuesCompare(this, rightResult) >= 0));
                break;
            case GREATER:
                returning = new ScriptValue_Boolean(getEnvironment(), (leftResult.valuesCompare(this, rightResult) > 0));
                break;
            default:
                throw new Exception_InternalError("Invalid default");
        }
        assert LegacyDebugger.close("Returned value", returning);
        return returning;
    }

    // ScriptValue_Abstract implementation
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
        return execute();
    }

    public ScriptValue_Abstract setValue(Referenced ref, ScriptValue_Abstract value) throws Exception_Nodeable {
        return getValue().setValue(ref, value);
    }

    public boolean valuesEqual(Referenced ref, ScriptValue_Abstract comparedValue) throws Exception_Nodeable {
        return getValue().valuesEqual(ref, comparedValue);
    }

    public int valuesCompare(Referenced ref, ScriptValue_Abstract comparedValue) throws Exception_Nodeable {
        return getValue().valuesCompare(ref, comparedValue);
    }

    // Nodeable implementation
    @Override
    public boolean nodificate() {
        assert LegacyDebugger.open("Boolean Expression (" + ScriptOperator.getName(this.comparison) + ")");
        assert super.nodificate();
        assert LegacyDebugger.addSnapNode("Left hand", this.lhs);
        assert LegacyDebugger.addSnapNode("Right hand", this.rhs);
        assert LegacyDebugger.close();
        return true;
    }
}
