import java.util.LinkedList;
import java.util.List;

public class FauxTemplate_Label extends FauxTemplate_InterfaceElement implements ScriptConvertible, Nodeable {
	public static final String LABELSTRING = "Label";

	public FauxTemplate_Label(ScriptEnvironment env) {
		super(env, ScriptValueType.createType(env, LABELSTRING), ScriptValueType.createType(env, FauxTemplate_InterfaceElement.INTERFACEELEMENTSTRING), new LinkedList<ScriptValueType>(), false);
	}

	public FauxTemplate_Label(ScriptEnvironment env, ScriptValueType type) {
		super(env, type);
		setElement(new InterfaceElement_Label(env, null, null, ""));
	}

	// Function bodies are contained via a series of if statements in execute
	// Template will be null if the object is exactly of this type and is constructing, and thus must be created then
	@Override
	public ScriptValue_Abstract execute(Referenced ref, String name, List<ScriptValue_Abstract> params, ScriptTemplate_Abstract rawTemplate) throws Exception_Nodeable {
		assert Debugger.openNode("Faux Template Executions", "Executing Label Faux Template Function (" + ScriptFunction.getDisplayableFunctionName(name) + ")");
		FauxTemplate_Label template = (FauxTemplate_Label) rawTemplate;
		ScriptValue_Abstract returning;
		assert Debugger.addSnapNode("Template provided", template);
		assert Debugger.addSnapNode("Parameters provided", params);
		if (name == null || name.equals("")) {
			if (template == null) {
				template = (FauxTemplate_Label) createObject(ref, template);
			}
			String label = "";
			if (params.size() > 0) {
				label = Parser.getString(params.get(params.size() - 1));
				params.remove(params.size() - 1);
			}
			template.getLabel().setString(label);
		} else if (name.equals("setString")) {
			template.getLabel().setString(Parser.getString(params.get(0)));
			assert Debugger.closeNode();
			return null;
		} else if (name.equals("getString")) {
			returning = Parser.getRiffString(getEnvironment(), template.getLabel().getString());
			assert Debugger.closeNode();
			return returning;
		}
		returning = getExtendedFauxClass().execute(ref, name, params, template);
		assert Debugger.closeNode();
		return returning;
	}

	public InterfaceElement_Label getLabel() {
		return (InterfaceElement_Label) getElement();
	}

	// addFauxFunction(name,ScriptValueType type,List<ScriptValue_Abstract>params,ScriptKeywordType permission,boolean isAbstract)
	// All functions must be defined here. All function bodies are defined in 'execute'.
	@Override
	public void initialize() throws Exception_Nodeable {
		assert Debugger.openNode("Faux Template Initializations", "Initializing label faux template");
		addConstructor(getType(), ScriptValueType.createEmptyParamList());
		List<ScriptValue_Abstract> fxnParams = new LinkedList<ScriptValue_Abstract>();
		fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.STRING));
		addConstructor(getType(), fxnParams);
		fxnParams = new LinkedList<ScriptValue_Abstract>();
		fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.createType(getEnvironment(), Stylesheet.STYLESHEETSTRING)));
		fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.STRING));
		addConstructor(getType(), fxnParams);
		fxnParams = new LinkedList<ScriptValue_Abstract>();
		fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.createType(getEnvironment(), Stylesheet.STYLESHEETSTRING)));
		fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.createType(getEnvironment(), Stylesheet.STYLESHEETSTRING)));
		fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.STRING));
		addConstructor(getType(), fxnParams);
		disableFullCreation();
		getExtendedClass().initialize();
		fxnParams = FauxTemplate.createEmptyParamList();
		fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.STRING));
		addFauxFunction("setLabel", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		fxnParams = FauxTemplate.createEmptyParamList();
		addFauxFunction("getLabel", ScriptValueType.STRING, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		assert Debugger.closeNode();
	}

	// Define default constructor here
	@Override
	public ScriptTemplate instantiateTemplate() {
		return new FauxTemplate_Label(getEnvironment(), getType());
	}

	// Nodeable and ScriptConvertible implementations
	@Override
	public boolean nodificate() {
		if (getLabel() != null) {
			assert Debugger.openNode("Label Faux Template (" + getLabel().getString() + ")");
		} else {
			assert Debugger.openNode("Label Faux Template");
		}
		assert super.nodificate();
		assert Debugger.closeNode();
		return true;
	}
}
