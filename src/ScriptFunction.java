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
		this.m_type = returnType;
		this.m_params = params;
		this.m_permission = permission;
		this.m_isAbstract = isAbstract;
		this.m_isStatic = isStatic;
	}

	@Override
	public void addExpression(ScriptExecutable exp) throws Exception_Nodeable {
		assert exp != null;
		this.m_expressions.add(exp);
	}

	@Override
	public void addExpressions(Collection<ScriptExecutable> list) throws Exception_Nodeable {
		for (ScriptExecutable exec : list) {
			this.addExpression(exec);
		}
	}

	@Override
	public boolean areParametersConvertible(List<ScriptValue_Abstract> list) {
		return areParametersConvertible(this.getParameters(), list);
	}

	@Override
	public boolean areParametersEqual(List<ScriptValue_Abstract> list) {
		return areParametersEqual(this.getParameters(), list);
	}

	@Override
	public void execute(Referenced ref, List<ScriptValue_Abstract> valuesGiven) throws Exception_Nodeable {
		String currNode = "Executing Function Expressions (" + this.m_expressions.size() + " expressions)";
		assert Debugger.openNode("Function Expression Executions", currNode);
		if (valuesGiven != null && valuesGiven.size() > 0) {
			assert Debugger.openNode("Assigning Initial Parameters (" + valuesGiven.size() + " parameter(s))");
			assert this.areParametersConvertible(valuesGiven) : "Parameters-convertible test failed in execute";
			for (int i = 0; i < this.getParameters().size(); i++) {
				this.getParameters().get(i).setValue(ref, valuesGiven.get(i));
			}
			assert Debugger.closeNode();
		}
		for (ScriptExecutable exec : this.m_expressions) {
			exec.execute();
			if (exec instanceof Returnable && ((Returnable) exec).shouldReturn()) {
				this.setReturnValue(exec.getDebugReference(), ((Returnable) exec).getReturnValue());
				assert Debugger.ensureCurrentNode("Executing Function Expressions (" + this.m_expressions.size() + " expressions)");
				assert Debugger.closeNode();
				return;
			}
		}
		assert Debugger.ensureCurrentNode(currNode);
		assert Debugger.closeNode();
	}

	@Override
	public List<ScriptValue_Abstract> getParameters() {
		return this.m_params;
	}

	@Override
	public ScriptKeywordType getPermission() {
		return this.m_permission;
	}

	@Override
	public ScriptValueType getReturnType() {
		return this.m_type;
	}

	@Override
	public ScriptValue_Abstract getReturnValue() {
		return this.m_returnValue;
	}

	@Override
	public boolean isAbstract() {
		return this.m_isAbstract;
	}

	@Override
	public boolean isStatic() {
		return this.m_isStatic;
	}

	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Script-Function (Returning " + this.m_type.getName() + ")");
		if (this.m_params != null && this.m_params.size() > 0) {
			assert Debugger.addSnapNode("Parameters: " + this.m_params.size() + " parameter(s)", this.m_params);
		}
		if (this.m_expressions != null && this.m_expressions.size() > 0) {
			assert Debugger.addSnapNode("Expressions: " + this.m_expressions.size() + " expression(s)", this.m_expressions);
		}
		if (this.m_permission == null) {
			Debugger.addNode(DebugString.PERMISSIONNULL);
		} else {
			switch (this.m_permission) {
			case PRIVATE:
				assert Debugger.addNode(DebugString.PERMISSIONPRIVATE);
				break;
			case PROTECTED:
				Debugger.addNode(DebugString.PERMISSIONPROTECTED);
			case PUBLIC:
				Debugger.addNode(DebugString.PERMISSIONPUBLIC);
			}
		}
		assert Debugger.addNode("Abstract: " + this.m_isAbstract);
		assert Debugger.addNode("Static: " + this.m_isStatic);
		assert Debugger.addNode("Return Value Reference: " + this.m_returnValue);
		assert Debugger.closeNode();
		return true;
	}

	@Override
	public void setReturnValue(Referenced ref, ScriptValue_Abstract value) throws Exception_Nodeable {
		if (value == null && this.getReturnType().equals(ScriptKeywordType.VOID)) {
			return;
		}
		assert Debugger.openNode("Setting Return-Value");
		assert Debugger.addSnapNode("Function", this);
		assert Debugger.addSnapNode("Value", value);
		this.m_returnValue = value.castToType(ref, this.getReturnType());
		assert Debugger.closeNode();
	}
}
