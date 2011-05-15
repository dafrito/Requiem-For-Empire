public class ScriptExecutable_CreateVariable extends ScriptValue_Variable implements ScriptValue_Abstract, ScriptExecutable, Nodeable, Referenced {
	private ScriptKeywordType permission;
	private String name;
	private ScriptElement reference;

	public ScriptExecutable_CreateVariable(Referenced ref, ScriptValueType type, String name, ScriptKeywordType permission) throws Exception_Nodeable {
		super(ref.getEnvironment(), type, null);
		this.reference = ref.getDebugReference();
		this.name = name;
		this.permission = permission;
	}

	@Override
	public ScriptValue_Abstract castToType(Referenced ref, ScriptValueType type) throws Exception_Nodeable {
		return this.getValue().castToType(ref, type);
	}

	// ScriptExecutable implementation
	@Override
	public ScriptValue_Abstract execute() throws Exception_Nodeable {
		assert Debugger.openNode("Creating Variable (" + this.name + ")");
		ScriptValue_Variable value;
		this.getEnvironment().getCurrentObject().addVariable(this, this.name, value = new ScriptValue_Variable(this.getEnvironment(), this.getType(), this.getPermission()));
		assert Debugger.addSnapNode("Variable Created", value);
		assert Debugger.closeNode();
		return value;
	}

	// Referenced implementation
	@Override
	public ScriptElement getDebugReference() {
		return this.reference;
	}

	// ScriptValue_Abstract implementation
	@Override
	public ScriptEnvironment getEnvironment() {
		return this.getDebugReference().getEnvironment();
	}

	public String getName() {
		return this.name;
	}

	// Overloaded ScriptValue_Variable functions
	@Override
	public ScriptKeywordType getPermission() throws Exception_Nodeable {
		return this.permission;
	}

	@Override
	public ScriptValue_Abstract getValue() throws Exception_Nodeable {
		return this.execute().getValue();
	}

	@Override
	public boolean isConvertibleTo(ScriptValueType type) {
		return ScriptValueType.isConvertibleTo(this.getEnvironment(), this.getType(), type);
	}

	// Nodeable implementation
	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Variable-Creation Script-Element (" + this.name + ")");
		assert super.nodificate();
		assert Debugger.closeNode();
		return true;
	}

	@Override
	public ScriptValue_Abstract setReference(Referenced ref, ScriptValue_Abstract value) throws Exception_Nodeable {
		return ((ScriptValue_Variable) this.execute()).setReference(ref, value);
	}

	@Override
	public ScriptValue_Abstract setValue(Referenced ref, ScriptValue_Abstract value) throws Exception_Nodeable {
		return this.execute().setValue(ref, value);
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
