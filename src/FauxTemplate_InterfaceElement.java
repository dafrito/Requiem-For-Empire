import java.util.LinkedList;
import java.util.List;

public class FauxTemplate_InterfaceElement extends FauxTemplate implements Nodeable, ScriptConvertible {
	public static final String INTERFACEELEMENTSTRING = "InterfaceElement";
	private InterfaceElement m_element;

	public FauxTemplate_InterfaceElement(ScriptEnvironment env) {
		super(env, ScriptValueType.createType(env, INTERFACEELEMENTSTRING), ScriptValueType.getObjectType(env), new LinkedList<ScriptValueType>(), true);
	}

	public FauxTemplate_InterfaceElement(ScriptEnvironment env, ScriptValueType type) {
		super(env, type);
	}

	public FauxTemplate_InterfaceElement(ScriptEnvironment env, ScriptValueType type, ScriptValueType extended, List<ScriptValueType> implemented, boolean isAbstract) {
		super(env, type, extended, implemented, isAbstract);
	}

	// Nodeable implementation
	@Override
	public Object convert() {
		return getElement();
	}

	// Function bodies are contained via a series of if statements in execute
	// Template will be null if the object is exactly of this type and is constructing, and thus must be created then
	@Override
	public ScriptValue_Abstract execute(Referenced ref, String name, List<ScriptValue_Abstract> params, ScriptTemplate_Abstract rawTemplate) throws Exception_Nodeable {
		assert Debugger.openNode("Faux Template Executions", "Executing Interface Element Faux Template Function (" + ScriptFunction.getDisplayableFunctionName(name) + ")");
		FauxTemplate_InterfaceElement template = (FauxTemplate_InterfaceElement) rawTemplate;
		ScriptValue_Abstract returning;
		assert Debugger.addSnapNode("Template provided", template);
		assert Debugger.addSnapNode("Parameters provided", params);
		ScriptValue_Abstract value;
		if (name == null || name.equals("")) {
			if (template == null) {
				template = (FauxTemplate_InterfaceElement) createObject(ref, template);
			}
			switch (params.size()) {
			// Intentionally out of order to allow for case 2 to run case 1's code.
			case 2:
				value = params.get(1);
				template.getElement().setClassStylesheet((Stylesheet) value.getValue());
			case 1:
				value = params.get(0);
				template.getElement().setUniqueStylesheet((Stylesheet) value.getValue());
				break;
			}
		} else if (name.equals("getUniqueStylesheet")) {
			returning = Parser.getRiffStylesheet(template.getElement().getUniqueStylesheet());
			assert Debugger.closeNode();
			return returning;
		} else if (name.equals("getClassStylesheet")) {
			returning = Parser.getRiffStylesheet(template.getElement().getClassStylesheet());
			assert Debugger.closeNode();
			return returning;
		} else if (name.equals("setUniqueStylesheet")) {
			template.getElement().setUniqueStylesheet(Parser.getStylesheet(params.get(0)));
			assert Debugger.closeNode();
			return null;
		} else if (name.equals("setClassStylesheet")) {
			template.getElement().setClassStylesheet(Parser.getStylesheet(params.get(0)));
			assert Debugger.closeNode();
			return null;
		}
		params.clear();
		returning = getExtendedFauxClass().execute(ref, name, params, template);
		assert Debugger.closeNode();
		return returning;
	}

	public InterfaceElement getElement() {
		return m_element;
	}

	// addFauxFunction(name,ScriptValueType type,List<ScriptValue_Abstract>params,ScriptKeywordType permission,boolean isAbstract)
	// All functions must be defined here. All function bodies are defined in 'execute'.
	@Override
	public void initialize() throws Exception_Nodeable {
		assert Debugger.openNode("Faux Template Initializations", "Initializing interface element faux template");
		addConstructor(getType(), ScriptValueType.createEmptyParamList());
		List<ScriptValue_Abstract> fxnParams = new LinkedList<ScriptValue_Abstract>();
		fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.createType(getEnvironment(), Stylesheet.STYLESHEETSTRING)));
		addConstructor(getType(), fxnParams);
		fxnParams = new LinkedList<ScriptValue_Abstract>();
		fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.createType(getEnvironment(), Stylesheet.STYLESHEETSTRING)));
		fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.createType(getEnvironment(), Stylesheet.STYLESHEETSTRING)));
		addConstructor(getType(), fxnParams);
		disableFullCreation();
		getExtendedClass().initialize();
		fxnParams = new LinkedList<ScriptValue_Abstract>();
		addFauxFunction("getUniqueStylesheet", ScriptValueType.createType(getEnvironment(), Stylesheet.STYLESHEETSTRING), new LinkedList<ScriptValue_Abstract>(), ScriptKeywordType.PUBLIC, false, false);
		addFauxFunction("getClassStylesheet", ScriptValueType.createType(getEnvironment(), Stylesheet.STYLESHEETSTRING), new LinkedList<ScriptValue_Abstract>(), ScriptKeywordType.PUBLIC, false, false);
		fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.createType(getEnvironment(), Stylesheet.STYLESHEETSTRING)));
		addFauxFunction("setUniqueStylesheet", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		addFauxFunction("setClassStylesheet", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		assert Debugger.closeNode();
	}

	// Define default constructor here
	@Override
	public ScriptTemplate instantiateTemplate() {
		return new FauxTemplate_InterfaceElement(getEnvironment(), getType());
	}

	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Interface Element Faux Template");
		assert super.nodificate();
		if (getElement() == null) {
			assert Debugger.addNode("Interface Element: null");
		} else {
			assert Debugger.addNode(getElement());
		}
		assert Debugger.closeNode();
		return true;
	}

	public void setElement(InterfaceElement element) {
		assert Debugger.openNode("Interface Element Faux Template Changes", "Changing Interface Element");
		assert Debugger.addNode(this);
		assert Debugger.addNode(element);
		m_element = element;
		assert Debugger.closeNode();
	}
}
