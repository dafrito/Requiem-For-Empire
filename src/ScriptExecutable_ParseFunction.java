import java.util.Collection;
import java.util.List;

public class ScriptExecutable_ParseFunction extends ScriptElement implements ScriptFunction_Abstract, ScriptExecutable, Nodeable {
	private boolean m_isStatic, m_isAbstract;
	private ScriptGroup m_body;
	private ScriptValue_Abstract m_template;
	private String m_name;
	private ScriptValueType m_returnType;
	private List<ScriptValue_Abstract> m_parameters;
	private ScriptKeywordType m_permission;

	public ScriptExecutable_ParseFunction(Referenced ref, ScriptValueType returnType, ScriptValue_Abstract object, String name, List<ScriptValue_Abstract> paramList, ScriptKeywordType permission, boolean isStatic, boolean isAbstract, ScriptGroup body) {
		super(ref);
		this.m_name = name;
		this.m_template = object;
		this.m_returnType = returnType;
		this.m_parameters = paramList;
		this.m_permission = permission;
		this.m_isStatic = isStatic;
		this.m_isAbstract = isAbstract;
		this.m_body = body;
	}

	@Override
	public void addExpression(ScriptExecutable exp) throws Exception_Nodeable {
		throw new Exception_InternalError(this.getEnvironment(), "Invalid call in unparsed function");
	}

	@Override
	public void addExpressions(Collection<ScriptExecutable> list) throws Exception_Nodeable {
		throw new Exception_InternalError(this.getEnvironment(), "Invalid call in unparsed function");
	}

	@Override
	public boolean areParametersConvertible(List<ScriptValue_Abstract> list) {
		return ScriptFunction.areParametersConvertible(this.getParameters(), list);
	}

	@Override
	public boolean areParametersEqual(List<ScriptValue_Abstract> list) {
		return ScriptFunction.areParametersEqual(this.getParameters(), list);
	}

	// ScriptExecutable implementation
	@Override
	public ScriptValue_Abstract execute() throws Exception_Nodeable {
		throw new Exception_InternalError(this.getEnvironment(), "Invalid call in unparsed function");
	}

	@Override
	public void execute(Referenced ref, List<ScriptValue_Abstract> valuesGiven) throws Exception_Nodeable {
		throw new Exception_InternalError(this.getEnvironment(), "Invalid call in unparsed function");
	}

	public ScriptGroup getBody() {
		return this.m_body;
	}

	public String getName() {
		return this.m_name;
	}

	@Override
	public List<ScriptValue_Abstract> getParameters() {
		return this.m_parameters;
	}

	@Override
	public ScriptKeywordType getPermission() {
		return this.m_permission;
	}

	@Override
	public ScriptValueType getReturnType() {
		return this.m_returnType;
	}

	@Override
	public ScriptValue_Abstract getReturnValue() {
		throw new Exception_InternalError(this.getEnvironment(), "Invalid call in unparsed function");
	}

	// ScriptFunction implementation
	@Override
	public boolean isAbstract() {
		return this.m_isAbstract;
	}

	@Override
	public boolean isStatic() {
		return this.m_isStatic;
	}

	// Nodeable implementation
	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Unparsed Script-Function (" + ScriptFunction.getDisplayableFunctionName(this.m_name) + ")");
		assert super.nodificate();
		assert Debugger.addNode("Static: " + this.m_isStatic);
		assert Debugger.addSnapNode("Body", this.m_body);
		assert Debugger.closeNode();
		return true;
	}

	@Override
	public void setReturnValue(Referenced element, ScriptValue_Abstract value) {
		throw new Exception_InternalError(this.getEnvironment(), "Invalid call in unparsed function");
	}
}
