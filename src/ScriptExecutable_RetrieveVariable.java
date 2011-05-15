public class ScriptExecutable_RetrieveVariable extends ScriptValue_Variable implements ScriptExecutable, ScriptValue_Abstract, Nodeable, Referenced {
	private String m_name;
	private ScriptValue_Abstract m_template;
	private ScriptElement m_reference;

	public ScriptExecutable_RetrieveVariable(Referenced ref, ScriptValue_Abstract template, String name, ScriptValueType type) throws Exception_Nodeable {
		super(ref.getEnvironment(), type, null);
		m_reference = ref.getDebugReference();
		m_name = name;
		m_template = template;
	}

	@Override
	public ScriptValue_Abstract castToType(Referenced ref, ScriptValueType type) throws Exception_Nodeable {
		return getVariable().castToType(ref, type);
	}

	// ScriptExecutable implementation
	@Override
	public ScriptValue_Abstract execute() throws Exception_Nodeable {
		return getValue();
	}

	// Referenced implementation
	@Override
	public ScriptElement getDebugReference() {
		return m_reference;
	}

	@Override
	public ScriptEnvironment getEnvironment() {
		return getDebugReference().getEnvironment();
	}

	// Overloaded ScriptValue_Variable functions
	@Override
	public ScriptKeywordType getPermission() throws Exception_Nodeable {
		return getVariable().getPermission();
	}

	@Override
	public ScriptValue_Abstract getValue() throws Exception_Nodeable {
		return getVariable().getValue();
	}

	public ScriptValue_Variable getVariable() throws Exception_Nodeable {
		assert Debugger.openNode("Executing Variable Retrieval (" + m_name + ")");
		ScriptValue_Variable variable;
		if (m_template != null) {
			assert Debugger.addSnapNode("Template", m_template);
			variable = ((ScriptTemplate_Abstract) m_template.getValue()).getVariable(m_name);
		} else {
			variable = getEnvironment().retrieveVariable(m_name);
		}
		assert variable != null : "Variable not found (" + m_name + ")";
		assert Debugger.closeNode();
		return variable;
	}

	// Abstract-value implementation
	@Override
	public boolean isConvertibleTo(ScriptValueType type) {
		return ScriptValueType.isConvertibleTo(getEnvironment(), getType(), type);
	}

	// Nodeable implementation
	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Variable-Placeholder (" + m_name + ")");
		assert super.nodificate();
		if (m_template != null) {
			assert Debugger.addSnapNode("Reference Template", m_template);
		}
		assert Debugger.closeNode();
		return true;
	}

	@Override
	public ScriptValue_Abstract setReference(Referenced ref, ScriptValue_Abstract value) throws Exception_Nodeable {
		return getVariable().setReference(ref, value);
	}

	@Override
	public ScriptValue_Abstract setValue(Referenced ref, ScriptValue_Abstract value) throws Exception_Nodeable {
		return getVariable().setValue(ref, value);
	}

	@Override
	public int valuesCompare(Referenced ref, ScriptValue_Abstract rhs) throws Exception_Nodeable {
		return getValue().valuesCompare(ref, rhs);
	}

	@Override
	public boolean valuesEqual(Referenced ref, ScriptValue_Abstract rhs) throws Exception_Nodeable {
		return getValue().valuesEqual(ref, rhs);
	}
}
