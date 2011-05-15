import java.util.List;
import java.util.Vector;

public class ScriptExecutable_CallFunction extends ScriptElement implements ScriptExecutable, ScriptValue_Abstract, Nodeable {
	public static ScriptValue_Abstract callFunction(ScriptEnvironment env, Referenced ref, ScriptValue_Abstract object, String name, List<ScriptValue_Abstract> params) throws Exception_Nodeable {
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
		Vector<ScriptValue_Abstract> baseList = new Vector<ScriptValue_Abstract>();
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
		if (function == null) {
			if (ref == null) {
				throw new Exception_Nodeable_FunctionNotFound(env, name, params);
			} else {
				throw new Exception_Nodeable_FunctionNotFound(ref, name, params);
			}
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
		ScriptValue_Abstract returning = env.getCurrentFunction().getReturnValue();
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

	private String m_functionName;
	private List<ScriptValue_Abstract> m_params;

	private ScriptValue_Abstract m_object;

	public ScriptExecutable_CallFunction(Referenced ref, ScriptValue_Abstract object, String functionName, List<ScriptValue_Abstract> params) {
		super(ref);
		m_object = object;
		m_functionName = functionName;
		m_params = params;
	}

	@Override
	public ScriptValue_Abstract castToType(Referenced ref, ScriptValueType type) throws Exception_Nodeable {
		return getValue().castToType(ref, type);
	}

	// ScriptExecutable implementation
	@Override
	public ScriptValue_Abstract execute() throws Exception_Nodeable {
		return callFunction(getEnvironment(), this, m_object, m_functionName, m_params);
	}

	// ScriptValue_Abstract implementation
	@Override
	public ScriptValueType getType() {
		try {
			return ((ScriptTemplate_Abstract) m_object.getValue()).getFunction(m_functionName, m_params).getReturnType();
		} catch (Exception_Nodeable ex) {
			throw new Exception_InternalError(getEnvironment(), ex.toString());
		}
	}

	@Override
	public ScriptValue_Abstract getValue() throws Exception_Nodeable {
		return execute();
	}

	@Override
	public boolean isConvertibleTo(ScriptValueType type) {
		return ScriptValueType.isConvertibleTo(getEnvironment(), getType(), type);
	}

	// Nodeable implementation
	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Function Call (" + ScriptFunction.getDisplayableFunctionName(m_functionName) + ")");
		assert super.nodificate();
		assert Debugger.addSnapNode("Parameters", m_params);
		assert Debugger.closeNode();
		return true;
	}

	@Override
	public ScriptValue_Abstract setValue(Referenced ref, ScriptValue_Abstract value) throws Exception_Nodeable {
		return getValue().setValue(ref, value);
	}

	@Override
	public int valuesCompare(Referenced ref, ScriptValue_Abstract rhs) throws Exception_Nodeable {
		return getValue().valuesCompare(ref, rhs);
	}

	@Override
	public boolean valuesEqual(Referenced ref, ScriptValue_Abstract rhs) throws Exception_Nodeable {
		return getValue().valuesEqual(ref, rhs);
	}
}
