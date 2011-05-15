import java.util.List;

public class ScriptExecutable_ForStatement extends ScriptElement implements ScriptExecutable, Returnable, Nodeable {
	private ScriptExecutable initializer, tester, repeater;
	private boolean shouldReturn = false;
	private ScriptValue_Abstract returnValue;
	private List<ScriptExecutable> expressions;

	public ScriptExecutable_ForStatement(ScriptExecutable initializer, ScriptExecutable tester, ScriptExecutable repeater, List<ScriptExecutable> expressions) {
		super((Referenced) initializer);
		this.initializer = initializer;
		this.tester = tester;
		this.repeater = repeater;
		this.expressions = expressions;
	}

	// ScriptExecutable implementation
	@Override
	public ScriptValue_Abstract execute() throws Exception_Nodeable {
		assert Debugger.openNode("For-Statement Executions", "Executing For-Statement");
		this.getEnvironment().advanceNestedStack();
		assert Debugger.openNode("Initializing");
		this.initializer.execute();
		assert Debugger.closeNode();
		while (((ScriptValue_Boolean) this.tester.execute().getValue()).getBooleanValue()) {
			assert Debugger.openNode("Looping", "Looping iteration");
			this.getEnvironment().advanceNestedStack();
			for (ScriptExecutable exec : this.expressions) {
				exec.execute();
				if (exec instanceof Returnable && ((Returnable) exec).shouldReturn()) {
					this.returnValue = ((Returnable) exec).getReturnValue();
					this.shouldReturn = true;
					assert Debugger.closeNode();
					return null;
				}
			}
			this.getEnvironment().retreatNestedStack();
			assert Debugger.closeNode();
			assert Debugger.openNode("Executing repeater");
			this.repeater.execute();
			assert Debugger.closeNode();
		}
		this.getEnvironment().retreatNestedStack();
		assert Debugger.closeNode();
		return null;
	}

	@Override
	public ScriptValue_Abstract getReturnValue() throws Exception_Nodeable {
		if (this.returnValue != null) {
			this.returnValue = this.returnValue.getValue();
		}
		return this.returnValue;
	}

	// Nodeable implementation
	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Script For-Statement");
		assert Debugger.addSnapNode("Initializer", this.initializer);
		assert Debugger.addSnapNode("Boolean expression", this.tester);
		assert Debugger.addSnapNode("Repeating expression", this.repeater);
		assert Debugger.addSnapNode("Expressions", this.expressions);
		assert Debugger.closeNode();
		return true;
	}

	// Returnable implementation
	@Override
	public boolean shouldReturn() {
		return this.shouldReturn;
	}
}
