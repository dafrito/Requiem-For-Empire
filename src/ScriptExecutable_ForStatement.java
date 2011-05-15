import java.util.List;

public class ScriptExecutable_ForStatement extends ScriptElement implements ScriptExecutable, Returnable, Nodeable {
	private ScriptExecutable m_initializer, m_tester, m_repeater;
	private boolean m_shouldReturn = false;
	private ScriptValue_Abstract m_returnValue;
	private List<ScriptExecutable> m_expressions;

	public ScriptExecutable_ForStatement(ScriptExecutable initializer, ScriptExecutable tester, ScriptExecutable repeater, List<ScriptExecutable> expressions) {
		super((Referenced) initializer);
		this.m_initializer = initializer;
		this.m_tester = tester;
		this.m_repeater = repeater;
		this.m_expressions = expressions;
	}

	// ScriptExecutable implementation
	@Override
	public ScriptValue_Abstract execute() throws Exception_Nodeable {
		assert Debugger.openNode("For-Statement Executions", "Executing For-Statement");
		this.getEnvironment().advanceNestedStack();
		assert Debugger.openNode("Initializing");
		this.m_initializer.execute();
		assert Debugger.closeNode();
		while (((ScriptValue_Boolean) this.m_tester.execute().getValue()).getBooleanValue()) {
			assert Debugger.openNode("Looping", "Looping iteration");
			this.getEnvironment().advanceNestedStack();
			for (ScriptExecutable exec : this.m_expressions) {
				exec.execute();
				if (exec instanceof Returnable && ((Returnable) exec).shouldReturn()) {
					this.m_returnValue = ((Returnable) exec).getReturnValue();
					this.m_shouldReturn = true;
					assert Debugger.closeNode();
					return null;
				}
			}
			this.getEnvironment().retreatNestedStack();
			assert Debugger.closeNode();
			assert Debugger.openNode("Executing repeater");
			this.m_repeater.execute();
			assert Debugger.closeNode();
		}
		this.getEnvironment().retreatNestedStack();
		assert Debugger.closeNode();
		return null;
	}

	@Override
	public ScriptValue_Abstract getReturnValue() throws Exception_Nodeable {
		if (this.m_returnValue != null) {
			this.m_returnValue = this.m_returnValue.getValue();
		}
		return this.m_returnValue;
	}

	// Nodeable implementation
	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Script For-Statement");
		assert Debugger.addSnapNode("Initializer", this.m_initializer);
		assert Debugger.addSnapNode("Boolean expression", this.m_tester);
		assert Debugger.addSnapNode("Repeating expression", this.m_repeater);
		assert Debugger.addSnapNode("Expressions", this.m_expressions);
		assert Debugger.closeNode();
		return true;
	}

	// Returnable implementation
	@Override
	public boolean shouldReturn() {
		return this.m_shouldReturn;
	}
}
