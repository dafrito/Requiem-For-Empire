package com.dafrito.rfe.script;

import java.util.List;

import com.dafrito.rfe.Debugger;
import com.dafrito.rfe.script.exceptions.Exception_Nodeable;

public class ScriptFunction_Constructor extends ScriptFunction {
	private ScriptEnvironment environment;

	public ScriptFunction_Constructor(ScriptValueType returnType, List<ScriptValue> paramList, ScriptKeywordType permission) {
		super(returnType, paramList, permission, false, true);
		this.environment = returnType.getEnvironment();
	}

	@Override
	public void execute(Referenced ref, List<ScriptValue> valuesGiven) throws Exception_Nodeable {
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
	public void nodificate() {
		assert Debugger.openNode("Constructor-Function Script-Element");
		super.nodificate();
		assert Debugger.closeNode();
	}
}
