import java.util.LinkedList;
import java.util.List;

public class FauxTemplate_Path extends FauxTemplate_Point implements ScriptConvertible, Nodeable {
	public static final String PATHSTRING = "Path";

	public FauxTemplate_Path(ScriptEnvironment env) {
		super(env, ScriptValueType.createType(env, PATHSTRING), ScriptValueType.createType(env, FauxTemplate_Point.POINTSTRING), new LinkedList<ScriptValueType>(), false);
	}

	public FauxTemplate_Path(ScriptEnvironment env, ScriptValueType type) {
		super(env, type);
	}

	// Nodeable and ScriptConvertible interfaces
	@Override
	public Object convert() {
		return getPoint();
	}

	// Function bodies are contained via a series of if statements in execute
	// Template will be null if the object is exactly of this type and is constructing, and thus must be created then
	@Override
	public ScriptValue_Abstract execute(Referenced ref, String name, List<ScriptValue_Abstract> params, ScriptTemplate_Abstract rawTemplate) throws Exception_Nodeable {
		assert Debugger.openNode("Faux Template Executions", "Executing Path Faux Template Function (" + ScriptFunction.getDisplayableFunctionName(name) + ")");
		FauxTemplate_Path template = (FauxTemplate_Path) rawTemplate;
		assert Debugger.addSnapNode("Template provided", template);
		assert Debugger.addSnapNode("Parameters provided", params);
		if (name == null || name.equals("")) {
			if (template == null) {
				template = (FauxTemplate_Path) createObject(ref, template);
			}
			if (params.size() == 1) {
				((Point_Path) template.getPoint()).setScenario(Parser.getScenario(params.get(0)));
			}
			params.clear();
		} else if (name.equals("getTotalTime")) {
			ScriptValue_Abstract returning = Parser.getRiffLong(getEnvironment(), ((Point_Path) template.getPoint()).getTotalTime());
			assert Debugger.closeNode();
			return returning;
		}
		ScriptValue_Abstract returning = getExtendedFauxClass().execute(ref, name, params, template);
		assert Debugger.closeNode();
		return returning;
	}

	// All functions must be defined here. All function bodies are defined in 'execute'.
	@Override
	public void initialize() throws Exception_Nodeable {
		assert Debugger.openNode("Faux Template Initializations", "Initializing path faux template");
		addConstructor(getType(), ScriptValueType.createEmptyParamList());
		List<ScriptValue_Abstract> fxnParams = new LinkedList<ScriptValue_Abstract>();
		fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.createType(getEnvironment(), FauxTemplate_Scenario.SCENARIOSTRING)));
		addConstructor(getType(), fxnParams);
		disableFullCreation();
		getExtendedClass().initialize();
		addFauxFunction("getTotalTime", ScriptValueType.LONG, new LinkedList<ScriptValue_Abstract>(), ScriptKeywordType.PUBLIC, false, false);
		assert Debugger.closeNode();
	}

	// Define default constructor here
	@Override
	public ScriptTemplate instantiateTemplate() {
		return new FauxTemplate_Path(getEnvironment(), getType());
	}

	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Path Faux Script-Element");
		assert super.nodificate();
		assert Debugger.closeNode();
		return true;
	}
}
