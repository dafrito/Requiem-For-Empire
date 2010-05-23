package com.dafrito.script.executable;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Exception_InternalError;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.debug.Exceptions.Exception_Nodeable_DivisionByZero;
import com.dafrito.script.Referenced;
import com.dafrito.script.ScriptElement;
import com.dafrito.script.ScriptOperator;
import com.dafrito.script.ScriptOperatorType;
import com.dafrito.script.types.ScriptValue_Abstract;
import com.dafrito.script.types.ScriptValue_Numeric;

public class ScriptExecutable_EvalAssignMathExpression extends ScriptElement implements ScriptExecutable {
    private ScriptValue_Abstract left, right;
    private ScriptOperatorType operation;

    public ScriptExecutable_EvalAssignMathExpression(Referenced ref, ScriptValue_Abstract lhs,
        ScriptValue_Abstract rhs, ScriptOperatorType operation) {
        super(ref);
        this.left = lhs;
        this.right = rhs;
        this.operation = operation;
    }

    // ScriptExecutable implementation
    public ScriptValue_Abstract execute() throws Exception_Nodeable {
        assert LegacyDebugger.open("'Evaluate and Assign' Executions", "Executing 'Evaluate and Assign' Expression");
        assert LegacyDebugger.addNode(this);
        ScriptValue_Numeric leftResult = (ScriptValue_Numeric)this.left.getValue();
        ScriptValue_Numeric rightResult = (ScriptValue_Numeric)this.right.getValue();
        if((this.operation == ScriptOperatorType.DIVIDE || this.operation == ScriptOperatorType.MODULUS)
            && rightResult.getNumericValue().doubleValue() == 0.0d) {
            throw new Exception_Nodeable_DivisionByZero(this);
        }
        ScriptValue_Abstract returning = null;
        switch(this.operation) {
            case PLUSEQUALS:
                returning = leftResult.setNumericValue(leftResult.increment(this, rightResult));
                break;
            case MINUSEQUALS:
                returning = leftResult.setNumericValue(leftResult.decrement(this, rightResult));
                break;
            case MULTIPLYEQUALS:
                returning = leftResult.setNumericValue(leftResult.multiply(this, rightResult));
                break;
            case DIVIDEEQUALS:
                returning = leftResult.setNumericValue(leftResult.divide(this, rightResult));
                break;
            case MODULUSEQUALS:
                returning = leftResult.setNumericValue(leftResult.modulus(this, rightResult));
                break;
            default:
                throw new Exception_InternalError("Invalid default");
        }
        assert LegacyDebugger.close();
        return returning;
    }

    // Nodeable implementation
    @Override
    public boolean nodificate() {
        assert LegacyDebugger.open("'Evaluate and Assign' mathematical expression ("
            + ScriptOperator.getName(this.operation)
            + ")");
        assert LegacyDebugger.addSnapNode("Left side", this.left);
        assert LegacyDebugger.addSnapNode("Right side", this.right);
        assert LegacyDebugger.close();
        return true;
    }
}
