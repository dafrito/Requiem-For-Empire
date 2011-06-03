package com.dafrito.rfe.script;

import com.dafrito.rfe.gui.debug.Debugger;
import com.dafrito.rfe.inspect.Nodeable;
import com.dafrito.rfe.script.exceptions.Exception_InternalError;
import com.dafrito.rfe.script.exceptions.Exception_Nodeable;
import com.dafrito.rfe.script.exceptions.Exception_Nodeable_DivisionByZero;

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
	public ScriptValue execute() throws Exception_Nodeable {
		assert Debugger.openNode("'Evaluate and Assign' Executions", "Executing 'Evaluate and Assign' Expression");
		assert Debugger.addNode(this);
		ScriptValue_Numeric left = (ScriptValue_Numeric) this.left.getValue();
		ScriptValue_Numeric right = (ScriptValue_Numeric) this.right.getValue();
		if ((this.operation == ScriptOperatorType.DIVIDE || this.operation == ScriptOperatorType.MODULUS) && right.getNumericValue().doubleValue() == 0.0d) {
			throw new Exception_Nodeable_DivisionByZero(this);
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
			throw new Exception_InternalError("Invalid default");
		}
		assert Debugger.closeNode();
		return returning;
	}

	@Override
	public void nodificate() {
		assert Debugger.openNode("'Evaluate and Assign' mathematical expression (" + this.operation + ")");
		assert Debugger.addSnapNode("Left side", this.left);
		assert Debugger.addSnapNode("Right side", this.right);
		assert Debugger.closeNode();
	}
}
