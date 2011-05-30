package com.dafrito.rfe;

import com.dafrito.rfe.inspect.Nodeable;

public class ScriptExecutable_EvaluateMathExpression extends ScriptElement implements ScriptValue, ScriptExecutable, Nodeable {
	private ScriptValue lhs, rhs;
	private ScriptOperatorType operator;
	private ScriptValueType type;

	public ScriptExecutable_EvaluateMathExpression(Referenced ref, ScriptValue lhs, ScriptValue rhs, ScriptOperatorType expressionType) {
		super(ref);
		this.type = ScriptValueType.createType(lhs);
		this.lhs = lhs;
		this.rhs = rhs;
		this.operator = expressionType;
	}

	@Override
	public ScriptValue castToType(Referenced ref, ScriptValueType type) throws Exception_Nodeable {
		return this.getValue().castToType(ref, type);
	}

	// ScriptExecutable implementation
	@Override
	public ScriptValue execute() throws Exception_Nodeable {
		return this.getValue();
	}

	// ScriptValue_Abstract implementation
	@Override
	public ScriptValueType getType() {
		return this.type;
	}

	@Override
	public ScriptValue getValue() throws Exception_Nodeable {
		assert Debugger.openNode("Mathematic Expressions", "Executing Mathematic Expression");
		assert Debugger.addNode(this);
		ScriptValue_Numeric left = (ScriptValue_Numeric) this.lhs.getValue();
		ScriptValue_Numeric right = (ScriptValue_Numeric) this.rhs.getValue();
		if ((this.operator == ScriptOperatorType.DIVIDE || this.operator == ScriptOperatorType.MODULUS) && right.getNumericValue().doubleValue() == 0.0d) {
			throw new Exception_Nodeable_DivisionByZero(this);
		}
		ScriptValue returning = null;
		switch (this.operator) {
		case PLUS:
			returning = new ScriptValue_Numeric(this.getEnvironment(), left.increment(this, right));
			break;
		case MINUS:
			returning = new ScriptValue_Numeric(this.getEnvironment(), left.decrement(this, right));
			break;
		case MODULUS:
			returning = new ScriptValue_Numeric(this.getEnvironment(), left.modulus(this, right));
			break;
		case MULTIPLY:
			returning = new ScriptValue_Numeric(this.getEnvironment(), left.multiply(this, right));
			break;
		case DIVIDE:
			returning = new ScriptValue_Numeric(this.getEnvironment(), left.divide(this, right));
			break;
		}
		assert Debugger.closeNode();
		return returning;
	}

	@Override
	public boolean isConvertibleTo(ScriptValueType type) {
		return this.lhs.isConvertibleTo(type);
	}

	@Override
	public void nodificate() {
		assert Debugger.openNode("Mathematical Expression Evaluator (" + this.operator + ")");
		assert Debugger.addNode("Left", this.lhs);
		assert Debugger.addNode("Right", this.rhs);
		assert Debugger.closeNode();
	}

	@Override
	public ScriptValue setValue(Referenced ref, ScriptValue value) throws Exception_Nodeable {
		throw new Exception_InternalError(this, "Unexecuted Variable");
	}

	@Override
	public int valuesCompare(Referenced ref, ScriptValue rhs) throws Exception_Nodeable {
		return this.getValue().valuesCompare(ref, rhs.castToType(ref, this.getType()));
	}

	@Override
	public boolean valuesEqual(Referenced ref, ScriptValue rhs) throws Exception_Nodeable {
		return this.getValue().valuesEqual(ref, rhs.castToType(ref, this.getType()));
	}
}
