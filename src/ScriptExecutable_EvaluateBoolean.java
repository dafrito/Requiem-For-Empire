public class ScriptExecutable_EvaluateBoolean extends ScriptElement implements ScriptExecutable, ScriptValue_Abstract {
	private ScriptValue_Abstract m_lhs, m_rhs;
	private ScriptOperatorType m_comparison;

	public ScriptExecutable_EvaluateBoolean(Referenced ref, ScriptValue_Abstract lhs, ScriptValue_Abstract rhs, ScriptOperatorType comparison) {
		super(ref);
		this.m_lhs = lhs;
		this.m_rhs = rhs;
		this.m_comparison = comparison;
	}

	@Override
	public ScriptValue_Abstract castToType(Referenced ref, ScriptValueType type) throws Exception_Nodeable {
		assert Debugger.addNode("Type Casting", "Casting (" + this.getType() + " to " + type + ")");
		if (this.getType().equals(type)) {
			return this;
		}
		throw new Exception_Nodeable_ClassCast(ref, this, type);
	}

	// ScriptExecutable implementation
	@Override
	public ScriptValue_Abstract execute() throws Exception_Nodeable {
		assert Debugger.openNode("Boolean Evaluations", "Executing Boolean Evaluation");
		assert Debugger.addNode(this);
		ScriptValue_Abstract returning = null;
		assert Debugger.openNode("Getting left value");
		assert Debugger.addSnapNode("Left before resolution", this.m_lhs);
		ScriptValue_Abstract lhs = this.m_lhs.getValue();
		assert Debugger.addSnapNode("Left", lhs);
		assert Debugger.closeNode();
		assert Debugger.openNode("Getting right value");
		assert Debugger.addSnapNode("Right before resolution", this.m_rhs);
		ScriptValue_Abstract rhs = this.m_rhs.getValue();
		assert Debugger.addSnapNode("Right", rhs);
		assert Debugger.closeNode();
		switch (this.m_comparison) {
		case NONEQUIVALENCY:
			returning = new ScriptValue_Boolean(this.getEnvironment(), !lhs.valuesEqual(this, rhs));
			break;
		case LESS:
			returning = new ScriptValue_Boolean(this.getEnvironment(), (lhs.valuesCompare(this, rhs) < 0));
			break;
		case LESSEQUALS:
			returning = new ScriptValue_Boolean(this.getEnvironment(), (lhs.valuesCompare(this, rhs) <= 0));
			break;
		case EQUIVALENCY:
			returning = new ScriptValue_Boolean(this.getEnvironment(), lhs.valuesEqual(this, rhs));
			break;
		case GREATEREQUALS:
			returning = new ScriptValue_Boolean(this.getEnvironment(), (lhs.valuesCompare(this, rhs) >= 0));
			break;
		case GREATER:
			returning = new ScriptValue_Boolean(this.getEnvironment(), (lhs.valuesCompare(this, rhs) > 0));
			break;
		default:
			throw new Exception_InternalError("Invalid default");
		}
		assert Debugger.closeNode("Returned value", returning);
		return returning;
	}

	// ScriptValue_Abstract implementation
	@Override
	public ScriptValueType getType() {
		return ScriptValueType.BOOLEAN;
	}

	@Override
	public ScriptValue_Abstract getValue() throws Exception_Nodeable {
		return this.execute();
	}

	@Override
	public boolean isConvertibleTo(ScriptValueType type) {
		return this.getType().equals(type);
	}

	// Nodeable implementation
	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Boolean Expression (" + ScriptOperator.getName(this.m_comparison) + ")");
		assert super.nodificate();
		assert Debugger.addSnapNode("Left hand", this.m_lhs);
		assert Debugger.addSnapNode("Right hand", this.m_rhs);
		assert Debugger.closeNode();
		return true;
	}

	@Override
	public ScriptValue_Abstract setValue(Referenced ref, ScriptValue_Abstract value) throws Exception_Nodeable {
		return this.getValue().setValue(ref, value);
	}

	@Override
	public int valuesCompare(Referenced ref, ScriptValue_Abstract rhs) throws Exception_Nodeable {
		return this.getValue().valuesCompare(ref, rhs);
	}

	@Override
	public boolean valuesEqual(Referenced ref, ScriptValue_Abstract rhs) throws Exception_Nodeable {
		return this.getValue().valuesEqual(ref, rhs);
	}
}
