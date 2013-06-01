package com.dafrito.rfe.script.values;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.dafrito.rfe.gui.logging.cache.CommonString;
import com.dafrito.rfe.inspect.Nodeable;
import com.dafrito.rfe.logging.Logs;
import com.dafrito.rfe.script.exceptions.ScriptException;
import com.dafrito.rfe.script.operations.ScriptExecutable;
import com.dafrito.rfe.script.parsing.Referenced;
import com.dafrito.rfe.script.parsing.ScriptKeywordType;

public class RiffScriptFunction implements Nodeable, ScriptFunction {
	public static boolean areParametersConvertible(List<ScriptValue> parameters, List<ScriptValue> values) {
		assert Logs.openNode("Parameter-Convertibility Tests", "Parameter-Convertibility Test");
		assert Logs.addNode("Keys must be convertible to their function-param socket counterpart.");
		assert Logs.addSnapNode("Function-Parameter Sockets", parameters);
		assert Logs.addSnapNode("Parameter Keys", values);
		if (values.size() != parameters.size()) {
			assert Logs.closeNode("Parameter sizes do not match (" + values.size() + " and " + parameters.size() + ")");
			return false;
		}
		for (int i = 0; i < values.size(); i++) {
			ScriptValue param = parameters.get(i);
			if (param == null) {
				throw new NullPointerException("parameter must not be null");
			}
			ScriptValue value = values.get(i);
			if (value != null && !value.isConvertibleTo(param.getType())) {
				assert Logs.closeNode("Parameters are not equal (" + param.getType() + " and " + value.getType() + ")");
				return false;
			}
		}
		assert Logs.closeNode("Parameters match.");
		return true;
	}

	public static boolean areParametersEqual(List<ScriptValue> source, List<ScriptValue> list) {
		assert Logs.openNode("Parameter-Equality Tests", "Parameter-Equality Test");
		assert Logs.addSnapNode("Function-Parameter Sockets", source);
		assert Logs.addSnapNode("Parameter Keys", list);
		if (list.size() != source.size()) {
			assert Logs.closeNode("Parameter sizes do not match (" + list.size() + " and " + source.size() + ")");
			return false;
		}
		for (int i = 0; i < list.size(); i++) {
			if (!source.get(i).getType().equals(list.get(i).getType())) {
				assert Logs.closeNode("Parameters are not equal (" + source.get(i).getType() + " and " + list.get(i).getType() + ")");
				return false;
			}
		}
		assert Logs.closeNode("Parameters match.");
		return true;
	}

	public static String getDisplayableFunctionName(String name) {
		if (name == null || name.equals("")) {
			return "constructor";
		}
		return name;
	}

	private ScriptValueType type;
	private List<ScriptValue> params;
	private ScriptKeywordType permission;
	private ScriptValue returnValue;

	private boolean isAbstract, isStatic;

	private List<ScriptExecutable> expressions = new LinkedList<ScriptExecutable>();

	public RiffScriptFunction(ScriptValueType returnType, List<ScriptValue> params, ScriptKeywordType permission, boolean isAbstract, boolean isStatic) {
		this.type = returnType;
		this.params = new ArrayList<ScriptValue>(params);
		this.permission = permission;
		if (this.permission == null) {
			throw new NullPointerException("permission must not be null");
		}
		switch (this.permission) {
		case PRIVATE:
		case PROTECTED:
		case PUBLIC:
			break;
		default:
			throw new IllegalArgumentException("Permission must be PUBLIC, PROTECTED, or PRIVATE");
		}
		this.isAbstract = isAbstract;
		this.isStatic = isStatic;
	}

	@Override
	public void addExpression(ScriptExecutable exp) throws ScriptException {
		assert exp != null;
		this.expressions.add(exp);
	}

	@Override
	public void addExpressions(List<ScriptExecutable> list) throws ScriptException {
		for (ScriptExecutable exec : list) {
			this.addExpression(exec);
		}
	}

	@Override
	public boolean areParametersConvertible(List<ScriptValue> list) {
		return areParametersConvertible(this.getParameters(), list);
	}

	@Override
	public boolean areParametersEqual(List<ScriptValue> list) {
		return areParametersEqual(this.getParameters(), list);
	}

	@Override
	public void execute(Referenced ref, List<ScriptValue> valuesGiven) throws ScriptException {
		String currNode = "Executing Function Expressions (" + this.expressions.size() + " expressions)";
		assert Logs.openNode("Function Expression Executions", currNode);
		try {
			if (valuesGiven != null && valuesGiven.size() > 0) {
				assert Logs.openNode("Assigning Initial Parameters (" + valuesGiven.size() + " parameter(s))");
				assert this.areParametersConvertible(valuesGiven) : "Parameters-convertible test failed in execute";
				for (int i = 0; i < this.getParameters().size(); i++) {
					this.getParameters().get(i).setValue(ref, valuesGiven.get(i));
				}
				assert Logs.closeNode();
			}
			for (ScriptExecutable exec : this.expressions) {
				exec.execute();
				if (exec instanceof Returnable && ((Returnable) exec).shouldReturn()) {
					this.setReturnValue(exec.getDebugReference(), ((Returnable) exec).getReturnValue());
					return;
				}
			}
		} finally {
			assert Logs.closeNode();
		}
	}

	@Override
	public List<ScriptValue> getParameters() {
		return this.params;
	}

	@Override
	public ScriptKeywordType getPermission() {
		return this.permission;
	}

	@Override
	public ScriptValueType getReturnType() {
		return this.type;
	}

	@Override
	public ScriptValue getReturnValue() {
		return this.returnValue;
	}

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
		assert Logs.openNode("Script-Function (Returning " + this.type.getName() + ")");
		if (this.params != null && this.params.size() > 0) {
			assert Logs.addSnapNode("Parameters: " + this.params.size() + " parameter(s)", this.params);
		}
		if (this.expressions != null && this.expressions.size() > 0) {
			assert Logs.addSnapNode("Expressions: " + this.expressions.size() + " expression(s)", this.expressions);
		}
		if (this.permission == null) {
			Logs.addNode(CommonString.PERMISSIONNULL);
		} else {
			switch (this.permission) {
			case PRIVATE:
				assert Logs.addNode(CommonString.PERMISSIONPRIVATE);
				break;
			case PROTECTED:
				Logs.addNode(CommonString.PERMISSIONPROTECTED);
				break;
			case PUBLIC:
				Logs.addNode(CommonString.PERMISSIONPUBLIC);
				break;
			default:
				throw new AssertionError("Unexpected permission");
			}
		}
		assert Logs.addNode("Abstract: " + this.isAbstract);
		assert Logs.addNode("Static: " + this.isStatic);
		assert Logs.addNode("Return Value Reference: " + this.returnValue);
		assert Logs.closeNode();
	}

	@Override
	public void setReturnValue(Referenced ref, ScriptValue value) throws ScriptException {
		if (this.getReturnType().equals(ScriptKeywordType.VOID)) {
			return;
		}
		assert Logs.openNode("Setting Return-Value");
		assert Logs.addSnapNode("Function", this);
		assert Logs.addSnapNode("Value", value);
		this.returnValue = value.castToType(ref, this.getReturnType());
		assert Logs.closeNode();
	}
}
