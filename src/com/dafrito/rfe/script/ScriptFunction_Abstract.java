package com.dafrito.rfe.script;
import java.util.Collection;
import java.util.List;

import com.dafrito.rfe.Referenced;
import com.dafrito.rfe.script.exceptions.Exception_Nodeable;

public interface ScriptFunction_Abstract {
	public void addExpression(ScriptExecutable exp) throws Exception_Nodeable;

	public void addExpressions(Collection<ScriptExecutable> list) throws Exception_Nodeable;

	public boolean areParametersConvertible(List<ScriptValue> list);

	public boolean areParametersEqual(List<ScriptValue> list);

	public void execute(Referenced ref, List<ScriptValue> valuesGiven) throws Exception_Nodeable;

	public List<ScriptValue> getParameters();

	public ScriptKeywordType getPermission();

	public ScriptValueType getReturnType();

	public ScriptValue getReturnValue();

	// ScriptFunction implementation
	public boolean isAbstract();

	public boolean isStatic();

	public void nodificate();

	public void setReturnValue(Referenced element, ScriptValue value) throws Exception_Nodeable;
}
