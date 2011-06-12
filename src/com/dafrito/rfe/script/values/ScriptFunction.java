package com.dafrito.rfe.script.values;

import java.util.List;

import com.dafrito.rfe.script.exceptions.ScriptException;
import com.dafrito.rfe.script.operations.ScriptExecutable;
import com.dafrito.rfe.script.parsing.Referenced;
import com.dafrito.rfe.script.parsing.ScriptKeywordType;

public interface ScriptFunction {
	public void addExpression(ScriptExecutable exp) throws ScriptException;

	public void addExpressions(List<ScriptExecutable> list) throws ScriptException;

	public boolean areParametersConvertible(List<ScriptValue> list);

	public boolean areParametersEqual(List<ScriptValue> list);

	public void execute(Referenced ref, List<ScriptValue> valuesGiven) throws ScriptException;

	public List<ScriptValue> getParameters();

	public ScriptKeywordType getPermission();

	public ScriptValueType getReturnType();

	public ScriptValue getReturnValue();

	// ScriptFunction implementation
	public boolean isAbstract();

	public boolean isStatic();

	public void setReturnValue(Referenced element, ScriptValue value) throws ScriptException;
}
