import java.util.LinkedList;
import java.util.List;

public class FauxTemplate_Ace extends FauxTemplate implements ScriptConvertible, Nodeable {
	public static final String ACESTRING = "Ace";
	private Ace m_ace;

	public FauxTemplate_Ace(ScriptEnvironment env) {
		super(env, ScriptValueType.createType(env, ACESTRING), ScriptValueType.getObjectType(env), new LinkedList<ScriptValueType>(), false);
	}

	public FauxTemplate_Ace(ScriptEnvironment env, ScriptValueType type) {
		super(env, type);
	}

	// Nodeable and ScriptConvertible interfaces
	@Override
	public Object convert() {
		return this.m_ace;
	}

	// Function bodies are contained via a series of if statements in execute
	// Template will be null if the object is exactly of this type and is constructing, and thus must be created then
	@Override
	public ScriptValue_Abstract execute(Referenced ref, String name, List<ScriptValue_Abstract> params, ScriptTemplate_Abstract rawTemplate) throws Exception_Nodeable {
		assert Debugger.openNode("Faux Template Executions", "Executing ace faux template function (" + ScriptFunction.getDisplayableFunctionName(name) + ")");
		FauxTemplate_Ace template = (FauxTemplate_Ace) rawTemplate;
		assert Debugger.addSnapNode("Template provided", template);
		assert Debugger.addSnapNode("Parameters provided", params);
		if (name == null || name.equals("")) {
			if (template == null) {
				template = (FauxTemplate_Ace) this.createObject(ref, template);
			}
			template.setAce(new Ace(this.getEnvironment(), Parser.getArchetype(params.get(0)), Parser.getDouble(params.get(1))));
			params.clear();
		} else if (name.equals("setEfficiency")) {
			template.getAce().setEfficiency(Parser.getDouble(params.get(0)));
			assert Debugger.closeNode();
			return null;
		} else if (name.equals("getEfficiency")) {
			ScriptValue_Abstract returning = Parser.getRiffDouble(this.getEnvironment(), template.getAce().getEfficiency());
			assert Debugger.closeNode();
			return returning;
		} else if (name.equals("getArchetype")) {
			ScriptValue_Abstract returning = Parser.getRiffArchetype(template.getAce().getArchetype());
			assert Debugger.closeNode();
			return returning;
		}
		ScriptValue_Abstract returning = this.getExtendedFauxClass().execute(ref, name, params, template);
		assert Debugger.closeNode();
		return returning;
	}

	public Ace getAce() {
		return this.m_ace;
	}

	// All functions must be defined here. All function bodies are defined in 'execute'.
	@Override
	public void initialize() throws Exception_Nodeable {
		assert Debugger.openNode("Faux Template Initializations", "Initializing ace faux template");
		List<ScriptValue_Abstract> fxnParams = new LinkedList<ScriptValue_Abstract>();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.createType(this.getEnvironment(), FauxTemplate_Archetype.ARCHETYPESTRING)));
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.DOUBLE));
		this.addConstructor(this.getType(), fxnParams);
		this.disableFullCreation();
		this.getExtendedClass().initialize();
		fxnParams = new LinkedList<ScriptValue_Abstract>();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.DOUBLE));
		this.addFauxFunction("setEfficiency", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		fxnParams = new LinkedList<ScriptValue_Abstract>();
		this.addFauxFunction("getEfficiency", ScriptValueType.DOUBLE, new LinkedList<ScriptValue_Abstract>(), ScriptKeywordType.PUBLIC, false, false);
		this.addFauxFunction("getArchetype", ScriptValueType.createType(this.getEnvironment(), FauxTemplate_Archetype.ARCHETYPESTRING), fxnParams, ScriptKeywordType.PUBLIC, false, false);
		assert Debugger.closeNode();
	}

	// Define default constructor here
	@Override
	public ScriptTemplate instantiateTemplate() {
		return new FauxTemplate_Ace(this.getEnvironment(), this.getType());
	}

	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Ace Faux Script-Element");
		assert super.nodificate();
		assert Debugger.addNode(this.m_ace);
		assert Debugger.closeNode();
		return true;
	}

	public void setAce(Ace ace) {
		this.m_ace = ace;
	}
}
