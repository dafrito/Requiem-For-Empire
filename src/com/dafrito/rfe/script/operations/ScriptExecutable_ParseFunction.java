package com.dafrito.rfe.script.operations;

import java.util.List;

import com.dafrito.rfe.gui.debug.Debugger;
import com.dafrito.rfe.inspect.Nodeable;
import com.dafrito.rfe.script.exceptions.Exception_InternalError;
import com.dafrito.rfe.script.exceptions.ScriptException;
import com.dafrito.rfe.script.parsing.Referenced;
import com.dafrito.rfe.script.parsing.ScriptElement;
import com.dafrito.rfe.script.parsing.ScriptGroup;
import com.dafrito.rfe.script.parsing.ScriptKeywordType;
import com.dafrito.rfe.script.values.RiffScriptFunction;
import com.dafrito.rfe.script.values.ScriptFunction;
import com.dafrito.rfe.script.values.ScriptValue;
import com.dafrito.rfe.script.values.ScriptValueType;

public class ScriptExecutable_ParseFunction extends ScriptElement implements ScriptFunction, ScriptExecutable, Nodeable {
	private boolean isStatic, isAbstract;
	private ScriptGroup body;
	private String name;
	private ScriptValueType returnType;
	private List<ScriptValue> parameters;
	private ScriptKeywordType permission;

	public ScriptExecutable_ParseFunction(Referenced ref, ScriptValueType returnType, ScriptValue object, String name, List<ScriptValue> paramList, ScriptKeywordType permission, boolean isStatic, boolean isAbstract, ScriptGroup body) {
		super(ref);
		this.name = name;
		this.returnType = returnType;
		this.parameters = paramList;
		this.permission = permission;
		this.isStatic = isStatic;
		this.isAbstract = isAbstract;
		this.body = body;
	}

	@Override
	public void addExpression(ScriptExecutable exp) throws ScriptException {
		throw new Exception_InternalError(this.getEnvironment(), "Invalid call in unparsed function");
	}

	@Override
	public void addExpressions(List<ScriptExecutable> list) throws ScriptException {
		throw new Exception_InternalError(this.getEnvironment(), "Invalid call in unparsed function");
	}

	@Override
	public boolean areParametersConvertible(List<ScriptValue> list) {
		return RiffScriptFunction.areParametersConvertible(this.getParameters(), list);
	}

	@Override
	public boolean areParametersEqual(List<ScriptValue> list) {
		return RiffScriptFunction.areParametersEqual(this.getParameters(), list);
	}

	// ScriptExecutable implementation
	@Override
	public ScriptValue execute() throws ScriptException {
		throw new Exception_InternalError(this.getEnvironment(), "Invalid call in unparsed function");
	}

	@Override
	public void execute(Referenced ref, List<ScriptValue> valuesGiven) throws ScriptException {
		throw new Exception_InternalError(this.getEnvironment(), "Invalid call in unparsed function");
	}

	public ScriptGroup getBody() {
		return this.body;
	}

	public String getName() {
		return this.name;
	}

	@Override
	public List<ScriptValue> getParameters() {
		return this.parameters;
	}

	@Override
	public ScriptKeywordType getPermission() {
		return this.permission;
	}

	@Override
	public ScriptValueType getReturnType() {
		return this.returnType;
	}

	@Override
	public ScriptValue getReturnValue() {
		throw new Exception_InternalError(this.getEnvironment(), "Invalid call in unparsed function");
	}

	// ScriptFunction implementation
	@Override
	public boolean isAbstract() {
		return this.isAbstract;
	}

	@Override
	public boolean isStatic() {
		return this.isStatic;
	}

	@Override
	public void nodificate() {
		assert Debugger.openNode("Unparsed Script-Function (" + RiffScriptFunction.getDisplayableFunctionName(this.name) + ")");
		assert Debugger.addNode("Static: " + this.isStatic);
		assert Debugger.addSnapNode("Body", this.body);
		assert Debugger.closeNode();
	}

	@Override
	public void setReturnValue(Referenced element, ScriptValue value) {
		throw new Exception_InternalError(this.getEnvironment(), "Invalid call in unparsed function");
	}
}
