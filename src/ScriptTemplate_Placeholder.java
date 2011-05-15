import java.util.List;

public class ScriptTemplate_Placeholder extends ScriptTemplate_Abstract implements ScriptValue_Abstract, Nodeable {
	private String m_name;

	public ScriptTemplate_Placeholder(ScriptEnvironment env, String name) {
		super(env, null, null, null);
		m_name = name;
	}

	@Override
	public void addFunction(Referenced ref, String name, ScriptFunction_Abstract function) throws Exception_Nodeable {
		getTemplate().addFunction(ref, name, function);
	}

	@Override
	public void addPreconstructorExpression(ScriptExecutable exec) throws Exception_Nodeable {
		getTemplate().addPreconstructorExpression(exec);
	}

	@Override
	public void addTemplatePreconstructorExpression(ScriptExecutable exec) throws Exception_Nodeable {
		getTemplate().addTemplatePreconstructorExpression(exec);
	}

	@Override
	public ScriptValue_Variable addVariable(Referenced ref, String name, ScriptValue_Variable value) throws Exception_Nodeable {
		return getTemplate().addVariable(ref, name, value);
	}

	@Override
	public ScriptValue_Abstract castToType(Referenced ref, ScriptValueType type) throws Exception_Nodeable {
		return getTemplate().castToType(ref, type);
	}

	@Override
	public ScriptTemplate_Abstract createObject(Referenced ref, ScriptTemplate_Abstract object) throws Exception_Nodeable {
		return getTemplate().createObject(ref, object);
	}

	@Override
	public void disableFullCreation() {
		getTemplate().disableFullCreation();
	}

	// Overloaded ScriptTemplate_Abstract fxns
	@Override
	public ScriptTemplate_Abstract getExtendedClass() {
		return getTemplate().getExtendedClass();
	}

	@Override
	public ScriptFunction_Abstract getFunction(String name, List<ScriptValue_Abstract> params) {
		return getTemplate().getFunction(name, params);
	}

	@Override
	public List<ScriptFunction_Abstract> getFunctions() {
		return getTemplate().getFunctions();
	}

	@Override
	public ScriptTemplate_Abstract getFunctionTemplate(ScriptFunction_Abstract fxn) {
		return getTemplate().getFunctionTemplate(fxn);
	}

	@Override
	public List<ScriptValueType> getInterfaces() {
		return getTemplate().getInterfaces();
	}

	@Override
	public ScriptValue_Variable getStaticReference() throws Exception_Nodeable {
		return getTemplate().getStaticReference();
	}

	private ScriptTemplate_Abstract getTemplate() {
		try {
			ScriptTemplate_Abstract template = (ScriptTemplate_Abstract) getEnvironment().retrieveVariable(m_name).getValue();
			assert template != null : "Template could not be retrieved (" + m_name + ")";
			return template;
		} catch (Exception_Nodeable ex) {
			throw new Exception_InternalError("Exception occurred while retrieving template: " + ex);
		}
	}

	// Abstract-value implementation
	@Override
	public ScriptValueType getType() {
		return getTemplate().getType();
	}

	@Override
	public ScriptValue_Abstract getValue() throws Exception_Nodeable {
		return getTemplate().getValue();
	}

	@Override
	public ScriptValue_Variable getVariable(String name) throws Exception_Nodeable {
		return getTemplate().getVariable(name);
	}

	@Override
	public void initialize() throws Exception_Nodeable {
		getTemplate().initialize();
	}

	@Override
	public void initializeFunctions(Referenced ref) throws Exception_Nodeable {
		getTemplate().initializeFunctions(ref);
	}

	@Override
	public ScriptTemplate instantiateTemplate() {
		return getTemplate().instantiateTemplate();
	}

	@Override
	public boolean isAbstract() throws Exception_Nodeable {
		return getTemplate().isAbstract();
	}

	@Override
	public boolean isConstructing() throws Exception_Nodeable {
		return getTemplate().isConstructing();
	}

	@Override
	public boolean isConvertibleTo(ScriptValueType type) {
		return getTemplate().isConvertibleTo(type);
	}

	// Abstract-template implementation
	@Override
	public boolean isFullCreation() {
		return getTemplate().isFullCreation();
	}

	@Override
	public boolean isObject() {
		return getTemplate().isObject();
	}

	// Nodeable implementation
	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Template Placeholder (" + m_name + ")");
		assert Debugger.addSnapNode("Referenced Template", getTemplate());
		assert Debugger.closeNode();
		return true;
	}

	@Override
	public void setConstructing(boolean constructing) throws Exception_Nodeable {
		getTemplate().setConstructing(constructing);
	}

	@Override
	public ScriptValue_Abstract setValue(Referenced ref, ScriptValue_Abstract value) throws Exception_Nodeable {
		return getTemplate().setValue(ref, value);
	}

	@Override
	public int valuesCompare(Referenced ref, ScriptValue_Abstract rhs) throws Exception_Nodeable {
		return getTemplate().valuesCompare(ref, rhs);
	}

	@Override
	public boolean valuesEqual(Referenced ref, ScriptValue_Abstract rhs) throws Exception_Nodeable {
		return getTemplate().valuesEqual(ref, rhs);
	}
}
