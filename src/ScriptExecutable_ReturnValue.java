public class ScriptExecutable_ReturnValue extends ScriptElement implements ScriptExecutable, Returnable, Nodeable {
	private ScriptValue_Abstract m_value;

	public ScriptExecutable_ReturnValue(Referenced ref, ScriptValue_Abstract value) {
		super(ref);
		this.m_value = value;
	}

	// ScriptExecutable implementation
	@Override
	public ScriptValue_Abstract execute() throws Exception_Nodeable {
		assert Debugger.openNode("Executing returnable script-value");
		ScriptValue_Abstract value = this.m_value.getValue();
		assert Debugger.closeNode();
		return value;
	}

	@Override
	public ScriptValue_Abstract getReturnValue() throws Exception_Nodeable {
		return this.m_value.getValue();
	}

	// Nodeable implementation
	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Returnable Script-Value");
		assert Debugger.addSnapNode("Returned Value", this.m_value);
		assert Debugger.closeNode();
		return true;
	}

	// Returnabled implementation
	@Override
	public boolean shouldReturn() {
		return true;
	}
}
