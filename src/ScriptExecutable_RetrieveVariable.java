public class ScriptExecutable_RetrieveVariable extends ScriptValue_Variable implements ScriptExecutable, ScriptValue_Abstract, Nodeable, Referenced {
	private String m_name;
	private ScriptValue_Abstract m_template;
	private ScriptElement m_reference;

	public ScriptExecutable_RetrieveVariable(Referenced ref, ScriptValue_Abstract template, String name, ScriptValueType type) throws Exception_Nodeable {
		super(ref.getEnvironment(), type, null);
		this.m_reference = ref.getDebugReference();
		this.m_name = name;
		this.m_template = template;
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
		return this.getVariable().getPermission();
	}

	@Override
	public ScriptValue_Abstract getValue() throws Exception_Nodeable {
		return this.getVariable().getValue();
	}

	public ScriptValue_Variable getVariable() throws Exception_Nodeable {
		assert Debugger.openNode("Executing Variable Retrieval (" + this.m_name + ")");
		ScriptValue_Variable variable;
		if (this.m_template != null) {
			assert Debugger.addSnapNode("Template", this.m_template);
			variable = ((ScriptTemplate_Abstract) this.m_template.getValue()).getVariable(this.m_name);
		} else {
			variable = this.getEnvironment().retrieveVariable(this.m_name);
		}
		assert variable != null : "Variable not found (" + this.m_name + ")";
		assert Debugger.closeNode();
		return variable;
	}

	// Abstract-value implementation
	@Override
	public boolean isConvertibleTo(ScriptValueType type) {
		return ScriptValueType.isConvertibleTo(this.getEnvironment(), this.getType(), type);
	}

	// Nodeable implementation
	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Variable-Placeholder (" + this.m_name + ")");
		assert super.nodificate();
		if (this.m_template != null) {
			assert Debugger.addSnapNode("Reference Template", this.m_template);
		}
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
