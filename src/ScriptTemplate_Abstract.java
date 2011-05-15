import java.util.Collections;
import java.util.List;

public abstract class ScriptTemplate_Abstract implements ScriptValue_Abstract, Nodeable {
	private final ScriptEnvironment m_environment;
	private final ScriptValueType m_type;
	private ScriptValueType m_extended;
	private List<ScriptValueType> m_interfaces;

	public ScriptTemplate_Abstract(ScriptEnvironment env, ScriptValueType type) {
		m_environment = env;
		m_type = type;
	}

	public ScriptTemplate_Abstract(ScriptEnvironment env, ScriptValueType type, ScriptValueType extended, List<ScriptValueType> interfaces) {
		m_environment = env;
		m_type = type;
		m_extended = extended;
		m_interfaces = interfaces;
	}

	public abstract void addFunction(Referenced ref, String name, ScriptFunction_Abstract function) throws Exception_Nodeable;

	public abstract void addPreconstructorExpression(ScriptExecutable exec) throws Exception_Nodeable;

	public abstract void addTemplatePreconstructorExpression(ScriptExecutable exec) throws Exception_Nodeable;

	public abstract ScriptValue_Variable addVariable(Referenced ref, String name, ScriptValue_Variable value) throws Exception_Nodeable;

	@Override
	public ScriptValue_Abstract castToType(Referenced ref, ScriptValueType type) throws Exception_Nodeable {
		if (isConvertibleTo(type)) {
			return this;
		}
		throw new Exception_Nodeable_ClassCast(ref, this, type);
	}

	public abstract ScriptTemplate_Abstract createObject(Referenced ref, ScriptTemplate_Abstract object) throws Exception_Nodeable;

	public abstract void disableFullCreation();

	// Abstract-value implementation
	@Override
	public ScriptEnvironment getEnvironment() {
		return m_environment;
	}

	public ScriptTemplate_Abstract getExtendedClass() {
		assert getEnvironment() != null : "Environment is null: " + this;
		if (getEnvironment().getTemplate(getType()) != null && getEnvironment().getTemplate(getType()) != this) {
			return getEnvironment().getTemplate(getType()).getExtendedClass();
		}
		if (m_extended == null) {
			return null;
		}
		assert getEnvironment().getTemplate(m_extended) != null : "Extended class is null!";
		return getEnvironment().getTemplate(m_extended);
	}

	public abstract ScriptFunction_Abstract getFunction(String name, List<ScriptValue_Abstract> params);

	public abstract List<ScriptFunction_Abstract> getFunctions();

	public abstract ScriptTemplate_Abstract getFunctionTemplate(ScriptFunction_Abstract fxn);

	public List<ScriptValueType> getInterfaces() {
		if (getEnvironment().getTemplate(getType()) != null && getEnvironment().getTemplate(getType()) != this) {
			return getEnvironment().getTemplate(getType()).getInterfaces();
		}
		return Collections.unmodifiableList(m_interfaces);
	}

	public abstract ScriptValue_Variable getStaticReference() throws Exception_Nodeable;

	@Override
	public ScriptValueType getType() {
		return m_type;
	}

	@Override
	public ScriptValue_Abstract getValue() throws Exception_Nodeable {
		return this;
	}

	public abstract ScriptValue_Variable getVariable(String name) throws Exception_Nodeable;

	public abstract void initialize() throws Exception_Nodeable;

	public abstract void initializeFunctions(Referenced ref) throws Exception_Nodeable;

	public abstract ScriptTemplate instantiateTemplate();

	public abstract boolean isAbstract() throws Exception_Nodeable;

	public abstract boolean isConstructing() throws Exception_Nodeable;

	@Override
	public boolean isConvertibleTo(ScriptValueType type) {
		if (getEnvironment().getTemplate(getType()) != null && getEnvironment().getTemplate(getType()) != this) {
			return getEnvironment().getTemplate(getType()).isConvertibleTo(type);
		}
		assert Debugger.openNode("Value Type Match Test", "Checking for Type-Match (" + getType() + " to " + type + ")");
		assert Debugger.addNode(this);
		if (getType().equals(type)) {
			assert Debugger.closeNode("Direct match.");
			return true;
		}
		if (getInterfaces() != null) {
			for (ScriptValueType scriptInterface : getInterfaces()) {
				if (ScriptValueType.isConvertibleTo(getEnvironment(), scriptInterface, type)) {
					assert Debugger.closeNode("Interface type match.");
					return true;
				}
			}
		}
		assert Debugger.openNode("No type match, checking extended classes for match.");
		if (getExtendedClass() != null && getExtendedClass().isConvertibleTo(type)) {
			assert Debugger.closeNode();
			assert Debugger.closeNode();
			return true;
		}
		assert Debugger.closeNode();
		assert Debugger.closeNode();
		return false;
	}

	// Abstract-template implementation
	public abstract boolean isFullCreation();

	public abstract boolean isObject();

	// Remaining unimplemented ScriptValue_Abstract functions
	@Override
	public abstract boolean nodificate();

	public abstract void setConstructing(boolean constructing) throws Exception_Nodeable;

	@Override
	public ScriptValue_Abstract setValue(Referenced ref, ScriptValue_Abstract value) throws Exception_Nodeable {
		throw new Exception_InternalError("Templates have no inherent value, and thus their value cannot be set directly.");
	}

	@Override
	public int valuesCompare(Referenced ref, ScriptValue_Abstract rhs) throws Exception_Nodeable {
		throw new Exception_InternalError("Templates have no inherent value, and thus cannot be compared.");
	}

	@Override
	public boolean valuesEqual(Referenced ref, ScriptValue_Abstract rhs) throws Exception_Nodeable {
		return (this == rhs);
	}
}
