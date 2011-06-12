package com.dafrito.rfe.script.values;

import java.util.List;

import com.dafrito.rfe.gui.debug.Debugger;
import com.dafrito.rfe.script.ScriptEnvironment;
import com.dafrito.rfe.script.exceptions.ScriptException;
import com.dafrito.rfe.script.parsing.Referenced;
import com.dafrito.rfe.script.parsing.ScriptKeywordType;

public class ScriptFunction_Constructor extends RiffScriptFunction {
	private ScriptEnvironment environment;

	public ScriptFunction_Constructor(ScriptValueType returnType, List<ScriptValue> paramList, ScriptKeywordType permission) {
		super(returnType, paramList, permission, false, true);
		this.environment = returnType.getEnvironment();
	}

	@Override
	public void execute(Referenced ref, List<ScriptValue> valuesGiven) throws ScriptException {
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
