package com.dafrito.rfe;
import java.util.List;
import java.util.Vector;


public class ScriptExecutable_CallFunction extends ScriptElement implements ScriptExecutable, ScriptValue, Nodeable {
	public static ScriptValue callFunction(ScriptEnvironment env, Referenced ref, ScriptValue object, String name, List<ScriptValue> params) throws Exception_Nodeable {
		assert Debugger.openNode("Function Calls", "Calling Function (" + ScriptFunction.getDisplayableFunctionName(name) + ")");
		assert Debugger.openNode("Function Call Details");
		// Get our object
		if (object == null) {
			object = env.getCurrentObject();
			assert Debugger.addSnapNode("Reverting to current object", object);
		} else {
			assert Debugger.openNode("Getting object's core value");
			object = object.getValue();
			assert Debugger.closeNode("Core value", object);
		}
		// Convert our values of questionable nestingness down to pure values
		Vector<ScriptValue> baseList = new Vector<ScriptValue>();
		if (params != null && params.size() > 0) {
			assert Debugger.openNode("Getting parameters' core values");
			for (int i = 0; i < params.size(); i++) {
				baseList.add(params.get(i).getValue());
			}
			assert Debugger.closeNode("Core value params", baseList);
		}
		// Get our function
		ScriptFunction_Abstract function = ((ScriptTemplate_Abstract) object).getFunction(name, baseList);
		ScriptTemplate_Abstract functionTemplate = ((ScriptTemplate_Abstract) object).getFunctionTemplate(function);
		if (function == null) {
			if (ref == null) {
				throw new Exception_Nodeable_FunctionNotFound(env, name, params);
			} else {
				throw new Exception_Nodeable_FunctionNotFound(ref, name, params);
			}
		}
		if (functionTemplate.getType().equals(object.getType()) && !function.isStatic()) {
			functionTemplate = (ScriptTemplate_Abstract) object;
		}
		assert Debugger.addSnapNode("Function", function);
		assert Debugger.addSnapNode("Function's Template", functionTemplate);
		if (!function.isStatic() && !(functionTemplate).isObject()) {
			throw new Exception_Nodeable_FunctionNotFound(ref, name, params);
		}
		if (function.isStatic() && (functionTemplate).isObject()) {
			throw new Exception_Nodeable_FunctionNotFound(ref, name, params);
		}
		// Execute that function
		if (function instanceof ScriptFunction_Faux) {
			((ScriptFunction_Faux) function).setFauxTemplate(functionTemplate);
			((ScriptFunction_Faux) function).setTemplate((ScriptTemplate_Abstract) object);
		}
		assert Debugger.closeNode();
		env.advanceStack((ScriptTemplate_Abstract) object, function);
		env.getCurrentFunction().execute(ref, baseList);
		ScriptValue returning = env.getCurrentFunction().getReturnValue();
		if (returning == null && !env.getCurrentFunction().getReturnType().equals(ScriptValueType.VOID)) {
			if (ref == null) {
				throw new Exception_Nodeable_IllegalNullReturnValue(env, env.getCurrentFunction());
			} else {
				throw new Exception_Nodeable_IllegalNullReturnValue(ref, env.getCurrentFunction());
			}
		}
		env.retreatStack();
		assert Debugger.closeNode();
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
	public ScriptValue castToType(Referenced ref, ScriptValueType type) throws Exception_Nodeable {
		return this.getValue().castToType(ref, type);
	}

	// ScriptExecutable implementation
	@Override
	public ScriptValue execute() throws Exception_Nodeable {
		return callFunction(this.getEnvironment(), this, this.object, this.functionName, this.params);
	}

	// ScriptValue_Abstract implementation
	@Override
	public ScriptValueType getType() {
		try {
			return ((ScriptTemplate_Abstract) this.object.getValue()).getFunction(this.functionName, this.params).getReturnType();
		} catch (Exception_Nodeable ex) {
			throw new Exception_InternalError(this.getEnvironment(), ex.toString());
		}
	}

	@Override
	public ScriptValue getValue() throws Exception_Nodeable {
		return this.execute();
	}

	@Override
	public boolean isConvertibleTo(ScriptValueType type) {
		return ScriptValueType.isConvertibleTo(this.getEnvironment(), this.getType(), type);
	}

	// Nodeable implementation
	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Function Call (" + ScriptFunction.getDisplayableFunctionName(this.functionName) + ")");
		assert super.nodificate();
		assert Debugger.addSnapNode("Parameters", this.params);
		assert Debugger.closeNode();
		return true;
	}

	@Override
	public ScriptValue setValue(Referenced ref, ScriptValue value) throws Exception_Nodeable {
		return this.getValue().setValue(ref, value);
	}

	@Override
	public int valuesCompare(Referenced ref, ScriptValue rhs) throws Exception_Nodeable {
		return this.getValue().valuesCompare(ref, rhs);
	}

	@Override
	public boolean valuesEqual(Referenced ref, ScriptValue rhs) throws Exception_Nodeable {
		return this.getValue().valuesEqual(ref, rhs);
	}
}
