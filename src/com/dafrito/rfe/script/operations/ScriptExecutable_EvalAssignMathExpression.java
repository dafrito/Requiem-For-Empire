package com.dafrito.rfe.script.operations;

import com.dafrito.rfe.inspect.Nodeable;
import com.dafrito.rfe.logging.Logs;
import com.dafrito.rfe.script.exceptions.DivisionByZeroScriptException;
import com.dafrito.rfe.script.exceptions.ScriptException;
import com.dafrito.rfe.script.parsing.Referenced;
import com.dafrito.rfe.script.parsing.ScriptElement;
import com.dafrito.rfe.script.parsing.ScriptOperatorType;
import com.dafrito.rfe.script.values.ScriptValue;
import com.dafrito.rfe.script.values.ScriptValue_Numeric;

public class ScriptExecutable_EvalAssignMathExpression extends ScriptElement implements ScriptExecutable, Nodeable {
	private ScriptValue left, right;
	private ScriptOperatorType operation;

	public ScriptExecutable_EvalAssignMathExpression(Referenced ref, ScriptValue lhs, ScriptValue rhs, ScriptOperatorType operation) {
		super(ref);
		this.left = lhs;
		this.right = rhs;
		this.operation = operation;
	}

	// ScriptExecutable implementation
	@Override
	public ScriptValue execute() throws ScriptException {
		assert Logs.openNode("'Evaluate and Assign' Executions", "Executing 'Evaluate and Assign' Expression");
		assert Logs.addNode(this);
		ScriptValue_Numeric left = (ScriptValue_Numeric) this.left.getValue();
		ScriptValue_Numeric right = (ScriptValue_Numeric) this.right.getValue();
		if ((this.operation == ScriptOperatorType.DIVIDE || this.operation == ScriptOperatorType.MODULUS) && right.getNumericValue().doubleValue() == 0.0d) {
			throw new DivisionByZeroScriptException(this);
		}
		ScriptValue returning = null;
		switch (this.operation) {
		case PLUSEQUALS:
			returning = left.setNumericValue(left.increment(this, right));
			break;
		case MINUSEQUALS:
			returning = left.setNumericValue(left.decrement(this, right));
			break;
		case MULTIPLYEQUALS:
			returning = left.setNumericValue(left.multiply(this, right));
			break;
		case DIVIDEEQUALS:
			returning = left.setNumericValue(left.divide(this, right));
			break;
		case MODULUSEQUALS:
			returning = left.setNumericValue(left.modulus(this, right));
			break;
		default:
			throw new AssertionError("Invalid default");
		}
		assert Logs.closeNode();
		return returning;
	}

	@Override
	public void nodificate() {
		assert Logs.openNode("'Evaluate and Assign' mathematical expression (" + this.operation + ")");
		assert Logs.addSnapNode("Left side", this.left);
		assert Logs.addSnapNode("Right side", this.right);
		assert Logs.closeNode();
	}
}
