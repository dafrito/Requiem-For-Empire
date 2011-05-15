public class ScriptExecutable_EvalAssignMathExpression extends ScriptElement implements ScriptExecutable, Nodeable {
	private ScriptValue_Abstract m_left, m_right;
	private ScriptOperatorType m_operation;

	public ScriptExecutable_EvalAssignMathExpression(Referenced ref, ScriptValue_Abstract lhs, ScriptValue_Abstract rhs, ScriptOperatorType operation) {
		super(ref);
		this.m_left = lhs;
		this.m_right = rhs;
		this.m_operation = operation;
	}

	// ScriptExecutable implementation
	@Override
	public ScriptValue_Abstract execute() throws Exception_Nodeable {
		assert Debugger.openNode("'Evaluate and Assign' Executions", "Executing 'Evaluate and Assign' Expression");
		assert Debugger.addNode(this);
		ScriptValue_Numeric left = (ScriptValue_Numeric) this.m_left.getValue();
		ScriptValue_Numeric right = (ScriptValue_Numeric) this.m_right.getValue();
		if ((this.m_operation == ScriptOperatorType.DIVIDE || this.m_operation == ScriptOperatorType.MODULUS) && right.getNumericValue().doubleValue() == 0.0d) {
			throw new Exception_Nodeable_DivisionByZero(this);
		}
		ScriptValue_Abstract returning = null;
		switch (this.m_operation) {
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

	// Nodeable implementation
	@Override
	public boolean nodificate() {
		assert Debugger.openNode("'Evaluate and Assign' mathematical expression (" + ScriptOperator.getName(this.m_operation) + ")");
		assert Debugger.addSnapNode("Left side", this.m_left);
		assert Debugger.addSnapNode("Right side", this.m_right);
		assert Debugger.closeNode();
		return true;
	}
}
