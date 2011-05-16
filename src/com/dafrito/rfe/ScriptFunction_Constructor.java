package com.dafrito.rfe;
import java.util.List;


public class ScriptFunction_Constructor extends ScriptFunction {
	private ScriptEnvironment environment;

	public ScriptFunction_Constructor(ScriptValueType returnType, List<ScriptValue_Abstract> paramList, ScriptKeywordType permission) {
		super(returnType, paramList, permission, false, true);
		this.environment = returnType.getEnvironment();
	}

	@Override
	public void execute(Referenced ref, List<ScriptValue_Abstract> valuesGiven) throws Exception_Nodeable {
		assert Debugger.openNode("Constructor Iterations", "Constructor Expression Iteration");
		ScriptTemplate_Abstract object = this.getEnvironment().getTemplate(this.getReturnType()).createObject(ref, null);
		this.getEnvironment().advanceStack(object, this);
		super.execute(ref, valuesGiven);
		this.setReturnValue(ref, object);
		this.getEnvironment().retreatStack();
		assert Debugger.closeNode();
	}

	public ScriptEnvironment getEnvironment() {
		return this.environment;
	}

	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Constructor-Function Script-Element");
		assert super.nodificate();
		assert Debugger.closeNode();
		return true;
	}
}
