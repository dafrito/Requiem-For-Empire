/**
 * 
 */
package com.dafrito.rfe.script.values;

import java.util.Collections;
import java.util.List;

import com.dafrito.rfe.script.exceptions.ScriptException;
import com.dafrito.rfe.script.operations.ScriptExecutable;
import com.dafrito.rfe.script.parsing.Referenced;
import com.dafrito.rfe.script.parsing.ScriptKeywordType;

/**
 * An immutable {@link ScriptFunction} that does nothing when called. This
 * function must not be modified; attempts to modify it will result in
 * {@link UnsupportedOperationException} errors.
 * 
 * @author Aaron Faanes
 * 
 */
public class NoopScriptFunction implements ScriptFunction {

	private static final NoopScriptFunction INSTANCE = new NoopScriptFunction();

	public static NoopScriptFunction instance() {
		return INSTANCE;
	}

	private NoopScriptFunction() {
	}

	@Override
	public void addExpression(ScriptExecutable exp) throws ScriptException {
		throw new UnsupportedOperationException("Noop function does not accept expressions");
	}

	@Override
	public void addExpressions(List<ScriptExecutable> list) throws ScriptException {
		throw new UnsupportedOperationException("Noop function does not accept expressions");
	}

	@Override
	public boolean areParametersConvertible(List<ScriptValue> list) {
		return true;
	}

	@Override
	public boolean areParametersEqual(List<ScriptValue> list) {
		return true;
	}

	@Override
	public void execute(Referenced ref, List<ScriptValue> valuesGiven) throws ScriptException {
		// Do nothing
	}

	@Override
	public List<ScriptValue> getParameters() {
		return Collections.emptyList();
	}

	@Override
	public ScriptKeywordType getPermission() {
		return ScriptKeywordType.PUBLIC;
	}

	@Override
	public ScriptValueType getReturnType() {
		return ScriptValueType.VOID;
	}

	@Override
	public ScriptValue getReturnValue() {
		return new ScriptValue_Null(null);
	}

	@Override
	public boolean isAbstract() {
		return false;
	}

	@Override
	public boolean isStatic() {
		return true;
	}

	@Override
	public void setReturnValue(Referenced element, ScriptValue value) throws ScriptException {
		throw new UnsupportedOperationException("Return value must not be set");
	}

}
