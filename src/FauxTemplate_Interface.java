import java.util.LinkedList;
import java.util.List;

public class FauxTemplate_Interface extends FauxTemplate implements Nodeable, ScriptConvertible {
	public static final String INTERFACESTRING = "Interface";
	private Interface m_interface;

	public FauxTemplate_Interface(ScriptEnvironment env) {
		super(env, ScriptValueType.createType(env, INTERFACESTRING), ScriptValueType.getObjectType(env), new LinkedList<ScriptValueType>(), false);
	}

	public FauxTemplate_Interface(ScriptEnvironment env, ScriptValueType type) {
		super(env, type);
		m_interface = new Interface(env);
	}

	// Function bodies are contained via a series of if statements in execute
	// Template will be null if the object is exactly of this type and is constructing, and thus must be created then
	@Override
	public ScriptValue_Abstract execute(Referenced ref, String name, List<ScriptValue_Abstract> params, ScriptTemplate_Abstract rawTemplate) throws Exception_Nodeable {
		assert Debugger.openNode("Faux Template Executions", "Executing interface faux template function (" + ScriptFunction.getDisplayableFunctionName(name) + ")");
		FauxTemplate_Interface template = (FauxTemplate_Interface) rawTemplate;
		ScriptValue_Abstract returning;
		assert Debugger.addSnapNode("Template provided", template);
		assert Debugger.addSnapNode("Parameters provided", params);
		if (name == null || name.equals("")) {
			if (template == null) {
				template = (FauxTemplate_Interface) createObject(ref, template);
			}
			assert Debugger.closeNode();
			return template;
		} else if (name.equals("add")) {
			template.getInterface().getRoot().add(Parser.getElement(params.get(0)));
			assert Debugger.closeNode();
			return null;
		} else if (name.equals("getRoot")) {
			returning = Parser.getRiffElement(getEnvironment(), template.getInterface().getRoot());
			assert Debugger.closeNode();
			return returning;
		}
		returning = getExtendedFauxClass().execute(ref, name, params, template);
		assert Debugger.closeNode();
		return returning;
	}

	public Interface getInterface() {
		return m_interface;
	}

	// addFauxFunction(name,ScriptValueType type,List<ScriptValue_Abstract>params,ScriptKeywordType permission,boolean isAbstract)
	// All functions must be defined here. All function bodies are defined in 'execute'.
	@Override
	public void initialize() throws Exception_Nodeable {
		assert Debugger.openNode("Faux Template Initializations", "Initializing interface faux template");
		addConstructor(getType(), ScriptValueType.createEmptyParamList());
		disableFullCreation();
		getExtendedClass().initialize();
		List<ScriptValue_Abstract> fxnParams = FauxTemplate.createEmptyParamList();
		fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.createType(getEnvironment(), FauxTemplate_InterfaceElement.INTERFACEELEMENTSTRING)));
		addFauxFunction("add", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		fxnParams = new LinkedList<ScriptValue_Abstract>();
		addFauxFunction("getRoot", ScriptValueType.createType(getEnvironment(), FauxTemplate_InterfaceElement.INTERFACEELEMENTSTRING), fxnParams, ScriptKeywordType.PUBLIC, false, false);
		assert Debugger.closeNode();
	}

	// Define default constructor here
	@Override
	public ScriptTemplate instantiateTemplate() {
		return new FauxTemplate_Interface(getEnvironment(), getType());
	}

	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Interface Faux Template");
		assert super.nodificate();
		assert Debugger.addNode(m_interface);
		assert Debugger.closeNode();
		return true;
	}
}
