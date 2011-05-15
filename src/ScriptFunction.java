import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ScriptFunction implements Nodeable, ScriptFunction_Abstract {
	public static boolean areParametersConvertible(List<ScriptValue_Abstract> source, List<ScriptValue_Abstract> list) {
		assert Debugger.openNode("Parameter-Convertibility Tests", "Parameter-Convertibility Test");
		assert Debugger.addNode("Keys must be convertible to their function-param socket counterpart.");
		assert Debugger.addSnapNode("Function-Parameter Sockets", source);
		assert Debugger.addSnapNode("Parameter Keys", list);
		if (list.size() != source.size()) {
			assert Debugger.closeNode("Parameter sizes do not match (" + list.size() + " and " + source.size() + ")");
			return false;
		}
		for (int i = 0; i < list.size(); i++) {
			if (!list.get(i).isConvertibleTo(source.get(i).getType())) {
				assert Debugger.closeNode("Parameters are not equal (" + source.get(i).getType() + " and " + list.get(i).getType() + ")");
				return false;
			}
		}
		assert Debugger.closeNode("Parameters match.");
		return true;
	}

	public static boolean areParametersEqual(List<ScriptValue_Abstract> source, List<ScriptValue_Abstract> list) {
		assert Debugger.openNode("Parameter-Equality Tests", "Parameter-Equality Test");
		assert Debugger.addSnapNode("Function-Parameter Sockets", source);
		assert Debugger.addSnapNode("Parameter Keys", list);
		if (list.size() != source.size()) {
			assert Debugger.closeNode("Parameter sizes do not match (" + list.size() + " and " + source.size() + ")");
			return false;
		}
		for (int i = 0; i < list.size(); i++) {
			if (!source.get(i).getType().equals(list.get(i).getType())) {
				assert Debugger.closeNode("Parameters are not equal (" + source.get(i).getType() + " and " + list.get(i).getType() + ")");
				return false;
			}
		}
		assert Debugger.closeNode("Parameters match.");
		return true;
	}

	public static String getDisplayableFunctionName(String name) {
		if (name == null || name.equals("")) {
			return "constructor";
		}
		return name;
	}

	private ScriptValueType m_type;
	private List<ScriptValue_Abstract> m_params;
	private ScriptKeywordType m_permission;
	private ScriptValue_Abstract m_returnValue;

	private boolean m_isAbstract, m_isStatic;

	private List<ScriptExecutable> m_expressions = new LinkedList<ScriptExecutable>();

	public ScriptFunction(ScriptValueType returnType, List<ScriptValue_Abstract> params, ScriptKeywordType permission, boolean isAbstract, boolean isStatic) {
		m_type = returnType;
		m_params = params;
		m_permission = permission;
		m_isAbstract = isAbstract;
		m_isStatic = isStatic;
	}

	@Override
	public void addExpression(ScriptExecutable exp) throws Exception_Nodeable {
		assert exp != null;
		m_expressions.add(exp);
	}

	@Override
	public void addExpressions(Collection<ScriptExecutable> list) throws Exception_Nodeable {
		for (ScriptExecutable exec : list) {
			addExpression(exec);
		}
	}

	@Override
	public boolean areParametersConvertible(List<ScriptValue_Abstract> list) {
		return areParametersConvertible(getParameters(), list);
	}

	@Override
	public boolean areParametersEqual(List<ScriptValue_Abstract> list) {
		return areParametersEqual(getParameters(), list);
	}

	@Override
	public void execute(Referenced ref, List<ScriptValue_Abstract> valuesGiven) throws Exception_Nodeable {
		String currNode = "Executing Function Expressions (" + m_expressions.size() + " expressions)";
		assert Debugger.openNode("Function Expression Executions", currNode);
		if (valuesGiven != null && valuesGiven.size() > 0) {
			assert Debugger.openNode("Assigning Initial Parameters (" + valuesGiven.size() + " parameter(s))");
			assert areParametersConvertible(valuesGiven) : "Parameters-convertible test failed in execute";
			for (int i = 0; i < getParameters().size(); i++) {
				getParameters().get(i).setValue(ref, valuesGiven.get(i));
			}
			assert Debugger.closeNode();
		}
		for (ScriptExecutable exec : m_expressions) {
			exec.execute();
			if (exec instanceof Returnable && ((Returnable) exec).shouldReturn()) {
				setReturnValue(exec.getDebugReference(), ((Returnable) exec).getReturnValue());
				assert Debugger.ensureCurrentNode("Executing Function Expressions (" + m_expressions.size() + " expressions)");
				assert Debugger.closeNode();
				return;
			}
		}
		assert Debugger.ensureCurrentNode(currNode);
		assert Debugger.closeNode();
	}

	@Override
	public List<ScriptValue_Abstract> getParameters() {
		return m_params;
	}

	@Override
	public ScriptKeywordType getPermission() {
		return m_permission;
	}

	@Override
	public ScriptValueType getReturnType() {
		return m_type;
	}

	@Override
	public ScriptValue_Abstract getReturnValue() {
		return m_returnValue;
	}

	@Override
	public boolean isAbstract() {
		return m_isAbstract;
	}

	@Override
	public boolean isStatic() {
		return m_isStatic;
	}

	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Script-Function (Returning " + m_type.getName() + ")");
		if (m_params != null && m_params.size() > 0) {
			assert Debugger.addSnapNode("Parameters: " + m_params.size() + " parameter(s)", m_params);
		}
		if (m_expressions != null && m_expressions.size() > 0) {
			assert Debugger.addSnapNode("Expressions: " + m_expressions.size() + " expression(s)", m_expressions);
		}
		if (m_permission == null) {
			Debugger.addNode(DebugString.PERMISSIONNULL);
		} else {
			switch (m_permission) {
			case PRIVATE:
				assert Debugger.addNode(DebugString.PERMISSIONPRIVATE);
				break;
			case PROTECTED:
				Debugger.addNode(DebugString.PERMISSIONPROTECTED);
			case PUBLIC:
				Debugger.addNode(DebugString.PERMISSIONPUBLIC);
			}
		}
		assert Debugger.addNode("Abstract: " + m_isAbstract);
		assert Debugger.addNode("Static: " + m_isStatic);
		assert Debugger.addNode("Return Value Reference: " + m_returnValue);
		assert Debugger.closeNode();
		return true;
	}

	@Override
	public void setReturnValue(Referenced ref, ScriptValue_Abstract value) throws Exception_Nodeable {
		if (value == null && getReturnType().equals(ScriptKeywordType.VOID)) {
			return;
		}
		assert Debugger.openNode("Setting Return-Value");
		assert Debugger.addSnapNode("Function", this);
		assert Debugger.addSnapNode("Value", value);
		m_returnValue = value.castToType(ref, getReturnType());
		assert Debugger.closeNode();
	}
}
