package com.dafrito.rfe.script.operations;

import java.util.ArrayList;
import java.util.List;

import com.dafrito.rfe.inspect.Nodeable;
import com.dafrito.rfe.logging.Logs;
import com.dafrito.rfe.script.ScriptEnvironment;
import com.dafrito.rfe.script.exceptions.Exception_InternalError;
import com.dafrito.rfe.script.exceptions.FunctionNotFoundScriptException;
import com.dafrito.rfe.script.exceptions.IllegalNullReturnValueException;
import com.dafrito.rfe.script.exceptions.ScriptException;
import com.dafrito.rfe.script.parsing.Referenced;
import com.dafrito.rfe.script.parsing.ScriptElement;
import com.dafrito.rfe.script.values.RiffScriptFunction;
import com.dafrito.rfe.script.values.ScriptFunction;
import com.dafrito.rfe.script.values.ScriptFunction_Faux;
import com.dafrito.rfe.script.values.ScriptTemplate_Abstract;
import com.dafrito.rfe.script.values.ScriptValue;
import com.dafrito.rfe.script.values.ScriptValueType;

public class ScriptExecutable_CallFunction extends ScriptElement implements ScriptExecutable, ScriptValue, Nodeable {
	public static ScriptValue callFunction(ScriptEnvironment env, Referenced ref, ScriptValue object, String name, List<ScriptValue> params) throws ScriptException {
		assert Logs.openNode("Function Calls", "Calling Function (" + RiffScriptFunction.getDisplayableFunctionName(name) + ")");
		assert Logs.openNode("Function Call Details");
		// Get our object
		if (object == null) {
			object = env.getCurrentObject();
			assert Logs.addSnapNode("Reverting to current object", object);
		} else {
			assert Logs.openNode("Getting object's core value");
			object = object.getValue();
			assert Logs.closeNode("Core value", object);
		}
		// Convert our values of questionable nestingness down to pure values
		List<ScriptValue> baseList = new ArrayList<ScriptValue>();
		if (params != null && !params.isEmpty()) {
			assert Logs.openNode("Getting parameters' core values");
			for (ScriptValue param : params) {
				baseList.add(param.getValue());
			}
			assert Logs.closeNode("Core value params", baseList);
		}
		// Get our function
		ScriptFunction function = ((ScriptTemplate_Abstract) object).getFunction(name, baseList);
		ScriptTemplate_Abstract functionTemplate = ((ScriptTemplate_Abstract) object).getFunctionTemplate(function);
		if (function == null) {
			if (ref == null) {
				throw new FunctionNotFoundScriptException(env, name, params);
			} else {
				throw new FunctionNotFoundScriptException(ref, name, params);
			}
		}
		if (functionTemplate.getType().equals(object.getType()) && !function.isStatic()) {
			functionTemplate = (ScriptTemplate_Abstract) object;
		}
		assert Logs.addSnapNode("Function", function);
		assert Logs.addSnapNode("Function's Template", functionTemplate);
		if (!function.isStatic() && !(functionTemplate).isObject()) {
			throw new FunctionNotFoundScriptException(ref, name, params);
		}
		if (function.isStatic() && (functionTemplate).isObject()) {
			throw new FunctionNotFoundScriptException(ref, name, params);
		}
		// Execute that function
		if (function instanceof ScriptFunction_Faux) {
			((ScriptFunction_Faux) function).setFauxTemplate(functionTemplate);
			((ScriptFunction_Faux) function).setTemplate((ScriptTemplate_Abstract) object);
		}
		assert Logs.closeNode();
		env.advanceStack((ScriptTemplate_Abstract) object, function);
		env.getCurrentFunction().execute(ref, baseList);
		ScriptValue returning = env.getCurrentFunction().getReturnValue();
		if (returning == null && !env.getCurrentFunction().getReturnType().equals(ScriptValueType.VOID)) {
			if (ref == null) {
				throw new IllegalNullReturnValueException(env, env.getCurrentFunction());
			} else {
				throw new IllegalNullReturnValueException(ref, env.getCurrentFunction());
			}
		}
		env.retreatStack();
		assert Logs.closeNode();
		return returning;
	}

	private String functionName;
	private List<ScriptValue> params;

	private ScriptValue object;

	public ScriptExecutable_CallFunction(Referenced ref, ScriptValue object, String functionName, List<ScriptValue> params) {
		super(ref);
		this.object = object;
		this.functionName = functionName;
		this.params = params;
	}

	@Override
	public ScriptValue castToType(Referenced ref, ScriptValueType type) throws ScriptException {
		return this.getValue().castToType(ref, type);
	}

	// ScriptExecutable implementation
	@Override
	public ScriptValue execute() throws ScriptException {
		return callFunction(this.getEnvironment(), this, this.object, this.functionName, this.params);
	}

	// ScriptValue_Abstract implementation
	@Override
	public ScriptValueType getType() {
		try {
			return ((ScriptTemplate_Abstract) this.object.getValue()).getFunction(this.functionName, this.params).getReturnType();
		} catch (ScriptException ex) {
			throw new Exception_InternalError(this.getEnvironment(), ex.toString());
		}
	}

	@Override
	public ScriptValue getValue() throws ScriptException {
		return this.execute();
	}

	@Override
	public boolean isConvertibleTo(ScriptValueType type) {
		return ScriptValueType.isConvertibleTo(this.getEnvironment(), this.getType(), type);
	}

	@Override
	public void nodificate() {
		assert Logs.openNode("Function Call (" + RiffScriptFunction.getDisplayableFunctionName(this.functionName) + ")");
		assert Logs.addSnapNode("Parameters", this.params);
		assert Logs.closeNode();
	}

	@Override
	public ScriptValue setValue(Referenced ref, ScriptValue value) throws ScriptException {
		return this.getValue().setValue(ref, value);
	}

	@Override
	public int valuesCompare(Referenced ref, ScriptValue rhs) throws ScriptException {
		return this.getValue().valuesCompare(ref, rhs);
	}

	@Override
	public boolean valuesEqual(Referenced ref, ScriptValue rhs) throws ScriptException {
		return this.getValue().valuesEqual(ref, rhs);
	}
}
