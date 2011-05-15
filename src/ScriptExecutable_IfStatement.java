import java.util.List;

public class ScriptExecutable_IfStatement extends ScriptElement implements ScriptExecutable, Returnable {
	private ScriptValue_Abstract m_testingValue;
	private List<ScriptExecutable> m_expressions;
	private ScriptExecutable_IfStatement m_elseStatement;
	private boolean m_shouldReturn = false;
	private ScriptValue_Abstract m_returnValue;

	public ScriptExecutable_IfStatement(Referenced ref, ScriptValue_Abstract test, List<ScriptExecutable> list) {
		super(ref);
		this.m_testingValue = test;
		this.m_expressions = list;
	}

	// ScriptExecutable implementation
	@Override
	public ScriptValue_Abstract execute() throws Exception_Nodeable {
		assert Debugger.openNode("If-Statement Executions", "Executing If-Statements");
		if (((ScriptValue_Boolean) this.m_testingValue.getValue()).getBooleanValue()) {
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
		} else {
			if (this.m_elseStatement != null) {
				this.m_elseStatement.execute();
			}
		}
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
		assert Debugger.openNode("Script If-Statement");
		assert Debugger.addSnapNode("Testing Expression", this.m_testingValue);
		assert Debugger.addSnapNode("Expressions", this.m_expressions);
		assert Debugger.addSnapNode("Else statement", this.m_elseStatement);
		assert Debugger.closeNode();
		return true;
	}

	public void setElseStatement(ScriptExecutable_IfStatement statement) {
		if (this.m_elseStatement != null) {
			this.m_elseStatement.setElseStatement(statement);
		} else {
			this.m_elseStatement = statement;
		}
	}

	// Returnable implementation
	@Override
	public boolean shouldReturn() {
		return this.m_shouldReturn;
	}
}
