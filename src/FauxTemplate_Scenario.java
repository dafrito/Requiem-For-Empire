import java.util.LinkedList;
import java.util.List;

public class FauxTemplate_Scenario extends FauxTemplate implements Nodeable, ScriptConvertible {
	public static final String SCENARIOSTRING = "Scenario";
	private Scenario scenario;

	public FauxTemplate_Scenario(ScriptEnvironment env) {
		super(env, ScriptValueType.createType(env, SCENARIOSTRING), ScriptValueType.getObjectType(env), new LinkedList<ScriptValueType>(), false);
	}

	public FauxTemplate_Scenario(ScriptEnvironment env, ScriptValueType type) {
		super(env, type);
		this.scenario = new Scenario(env, new Terrestrial(env, 1), "Scenario");
	}

	// Nodeable implementation
	@Override
	public Object convert() {
		return this.scenario;
	}

	// Function bodies are contained via a series of if statements in execute
	// Template will be null if the object is exactly of this type and is constructing, and thus must be created then
	@Override
	public ScriptValue_Abstract execute(Referenced ref, String name, List<ScriptValue_Abstract> params, ScriptTemplate_Abstract rawTemplate) throws Exception_Nodeable {
		assert Debugger.openNode("Faux Template Executions", "Executing scenario faux template function (" + ScriptFunction.getDisplayableFunctionName(name) + ")");
		FauxTemplate_Scenario template = (FauxTemplate_Scenario) rawTemplate;
		ScriptValue_Abstract returning;
		assert Debugger.addSnapNode("Template provided", template);
		assert Debugger.addSnapNode("Parameters provided", params);
		ScriptValue_Abstract value;
		if (name == null || name.equals("")) {
			if (template == null) {
				template = (FauxTemplate_Scenario) this.createObject(ref, template);
			}
			switch (params.size()) {
			case 2:
				template.getScenario().setTerrestrial(Parser.getTerrestrial(params.get(0)));
				template.getScenario().setName(Parser.getString(params.get(1)));
			case 0:
				assert Debugger.closeNode();
				return template;
			}
		} else if (name.equals("getName")) {
			returning = Parser.getRiffString(ref.getEnvironment(), template.getScenario().getName());
			assert Debugger.closeNode();
			return returning;
		} else if (name.equals("setName")) {
			template.getScenario().setName(Parser.getString(params.get(0)));
			assert Debugger.closeNode();
			return null;
		} else if (name.equals("getTerrestrial")) {
			returning = Parser.getRiffTerrestrial(ref.getEnvironment(), template.getScenario().getTerrestrial());
			assert Debugger.closeNode();
			return returning;
		} else if (name.equals("setTerrestrial")) {
			template.getScenario().setTerrestrial(Parser.getTerrestrial(params.get(0)));
			assert Debugger.closeNode();
			return null;
		} else if (name.equals("getScheduler")) {
			returning = Parser.getRiffScheduler(this.getEnvironment(), template.getScenario().getScheduler());
			assert Debugger.closeNode();
			return returning;
		}
		returning = this.getExtendedFauxClass().execute(ref, name, params, template);
		assert Debugger.closeNode();
		return returning;
	}

	public Scenario getScenario() {
		return this.scenario;
	}

	// addFauxFunction(name,ScriptValueType type,List<ScriptValue_Abstract>params,ScriptKeywordType permission,boolean isAbstract)
	// All functions must be defined here. All function bodies are defined in 'execute'.
	@Override
	public void initialize() throws Exception_Nodeable {
		assert Debugger.openNode("Faux Template Initializations", "Initializing scenario faux template");
		this.addConstructor(this.getType(), ScriptValueType.createEmptyParamList());
		List<ScriptValue_Abstract> fxnParams = new LinkedList<ScriptValue_Abstract>();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.createType(this.getEnvironment(), FauxTemplate_Terrestrial.TERRESTRIALSTRING)));
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.STRING));
		this.addConstructor(this.getType(), fxnParams);
		this.disableFullCreation();
		this.getExtendedClass().initialize();
		this.addFauxFunction("getName", ScriptValueType.STRING, ScriptValueType.createEmptyParamList(), ScriptKeywordType.PUBLIC, false, false);
		fxnParams = new LinkedList<ScriptValue_Abstract>();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.STRING));
		this.addFauxFunction("setName", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		this.addFauxFunction("getTerrestrial", ScriptValueType.createType(this.getEnvironment(), FauxTemplate_Terrestrial.TERRESTRIALSTRING), ScriptValueType.createEmptyParamList(), ScriptKeywordType.PUBLIC, false, false);
		fxnParams = new LinkedList<ScriptValue_Abstract>();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.createType(this.getEnvironment(), FauxTemplate_Terrestrial.TERRESTRIALSTRING)));
		this.addFauxFunction("setTerrestrial", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		this.addFauxFunction("getScheduler", ScriptValueType.createType(this.getEnvironment(), FauxTemplate_Scheduler.SCHEDULERSTRING), ScriptValueType.createEmptyParamList(), ScriptKeywordType.PUBLIC, false, false);
		assert Debugger.closeNode();
	}

	// Define default constructor here
	@Override
	public ScriptTemplate instantiateTemplate() {
		return new FauxTemplate_Scenario(this.getEnvironment(), this.getType());
	}

	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Scenario Faux Template");
		assert super.nodificate();
		assert Debugger.addNode(this.scenario);
		assert Debugger.closeNode();
		return true;
	}

	public void setScenario(Scenario scenario) {
		this.scenario = scenario;
	}
}
