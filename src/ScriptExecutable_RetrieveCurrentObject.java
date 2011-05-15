public class ScriptExecutable_RetrieveCurrentObject extends ScriptValue_Variable implements ScriptExecutable, ScriptValue_Abstract, Nodeable, Referenced {
	private ScriptElement m_reference;

	public ScriptExecutable_RetrieveCurrentObject(Referenced ref, ScriptValueType type) throws Exception_Nodeable {
		super(ref.getEnvironment(), type, null);
		this.m_reference = ref.getDebugReference();
	}

	@Override
	public ScriptValue_Abstract castToType(Referenced ref, ScriptValueType type) throws Exception_Nodeable {
		return this.getVariable().castToType(ref, type);
	}

	// ScriptExecutable implementation
	@Override
	public ScriptValue_Abstract execute() throws Exception_Nodeable {
		return this.getValue();
	}

	// Referenced implementation
	@Override
	public ScriptElement getDebugReference() {
		return this.m_reference;
	}

	@Override
	public ScriptEnvironment getEnvironment() {
		return this.getDebugReference().getEnvironment();
	}

	// Overloaded ScriptValue_Variable functions
	@Override
	public ScriptKeywordType getPermission() throws Exception_Nodeable {
		return ScriptKeywordType.PRIVATE;
	}

	@Override
	public ScriptValue_Abstract getValue() throws Exception_Nodeable {
		return this.getVariable().getValue();
	}

	public ScriptValue_Variable getVariable() throws Exception_Nodeable {
		assert Debugger.addNode("Executing Current Object Retrieval");
		return new ScriptValue_Variable(this.getEnvironment(), this.getType(), this.getEnvironment().getCurrentObject(), this.getPermission());
	}

	// Abstract-value implementation
	@Override
	public boolean isConvertibleTo(ScriptValueType type) {
		return ScriptValueType.isConvertibleTo(this.getEnvironment(), this.getType(), type);
	}

	// Nodeable implementation
	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Current Object Placeholder");
		assert super.nodificate();
		assert Debugger.closeNode();
		return true;
	}

	@Override
	public ScriptValue_Abstract setReference(Referenced ref, ScriptValue_Abstract value) throws Exception_Nodeable {
		return this.getVariable().setReference(ref, value);
	}

	@Override
	public ScriptValue_Abstract setValue(Referenced ref, ScriptValue_Abstract value) throws Exception_Nodeable {
		return this.getVariable().setValue(ref, value);
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
