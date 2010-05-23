package com.dafrito.script.executable;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Exception_InternalError;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.debug.Exceptions.Exception_Nodeable_DivisionByZero;
import com.dafrito.script.Referenced;
import com.dafrito.script.ScriptElement;
import com.dafrito.script.ScriptOperator;
import com.dafrito.script.ScriptOperatorType;
import com.dafrito.script.types.ScriptValueType;
import com.dafrito.script.types.ScriptValue_Abstract;
import com.dafrito.script.types.ScriptValue_Numeric;

public class ScriptExecutable_EvaluateMathExpression extends ScriptElement implements ScriptValue_Abstract,
        ScriptExecutable {
    private ScriptValue_Abstract lhs, rhs;
    private ScriptOperatorType operator;
    private ScriptValueType type;

    public ScriptExecutable_EvaluateMathExpression(Referenced ref, ScriptValue_Abstract lhs, ScriptValue_Abstract rhs,
        ScriptOperatorType expressionType) {
        super(ref);
        this.type = ScriptValueType.createType(lhs);
        this.lhs = lhs;
        this.rhs = rhs;
        this.operator = expressionType;
    }

    // ScriptExecutable implementation
    public ScriptValue_Abstract execute() throws Exception_Nodeable {
        return getValue();
    }

    // ScriptValue_Abstract implementation
    public ScriptValueType getType() {
        return this.type;
    }

    public boolean isConvertibleTo(ScriptValueType referenceType) {
        return this.lhs.isConvertibleTo(referenceType);
    }

    public ScriptValue_Abstract castToType(Referenced ref, ScriptValueType referenceType) throws Exception_Nodeable {
        return getValue().castToType(ref, referenceType);
    }

    public ScriptValue_Abstract getValue() throws Exception_Nodeable {
        assert LegacyDebugger.open("Mathematic Expressions", "Executing Mathematic Expression");
        assert LegacyDebugger.addNode(this);
        ScriptValue_Numeric left = (ScriptValue_Numeric)this.lhs.getValue();
        ScriptValue_Numeric right = (ScriptValue_Numeric)this.rhs.getValue();
        if((this.operator == ScriptOperatorType.DIVIDE || this.operator == ScriptOperatorType.MODULUS)
            && right.getNumericValue().doubleValue() == 0.0d) {
            throw new Exception_Nodeable_DivisionByZero(this);
        }
        ScriptValue_Abstract returning = null;
        switch(this.operator) {
            case PLUS:
                returning = new ScriptValue_Numeric(getEnvironment(), left.increment(this, right));
                break;
            case MINUS:
                returning = new ScriptValue_Numeric(getEnvironment(), left.decrement(this, right));
                break;
            case MODULUS:
                returning = new ScriptValue_Numeric(getEnvironment(), left.modulus(this, right));
                break;
            case MULTIPLY:
                returning = new ScriptValue_Numeric(getEnvironment(), left.multiply(this, right));
                break;
            case DIVIDE:
                returning = new ScriptValue_Numeric(getEnvironment(), left.divide(this, right));
                break;
            default:
                throw new UnsupportedOperationException("Unexpected default");
        }
        assert LegacyDebugger.close();
        return returning;
    }

    public ScriptValue_Abstract setValue(Referenced ref, ScriptValue_Abstract value) throws Exception_Nodeable {
        throw new Exception_InternalError(this, "Unexecuted Variable");
    }

    public boolean valuesEqual(Referenced ref, ScriptValue_Abstract testValue) throws Exception_Nodeable {
        return getValue().valuesEqual(ref, testValue.castToType(ref, getType()));
    }

    public int valuesCompare(Referenced ref, ScriptValue_Abstract testValue) throws Exception_Nodeable {
        return getValue().valuesCompare(ref, testValue.castToType(ref, getType()));
    }

    // Nodeable implementation
    @Override
    public boolean nodificate() {
        assert LegacyDebugger.open("Mathematical Expression Evaluator (" + ScriptOperator.getName(this.operator) + ")");
        assert LegacyDebugger.addNode("Left", this.lhs);
        assert LegacyDebugger.addNode("Right", this.rhs);
        assert LegacyDebugger.close();
        return true;
    }
}
