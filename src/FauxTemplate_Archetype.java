import java.util.LinkedList;
import java.util.List;

public class FauxTemplate_Archetype extends FauxTemplate implements ScriptConvertible, Nodeable {
	public static final String ARCHETYPESTRING = "Archetype";
	private Archetype m_archetype;

	public FauxTemplate_Archetype(ScriptEnvironment env) {
		super(env, ScriptValueType.createType(env, ARCHETYPESTRING), ScriptValueType.getObjectType(env), new LinkedList<ScriptValueType>(), false);
	}

	public FauxTemplate_Archetype(ScriptEnvironment env, ScriptValueType type) {
		super(env, type);
	}

	// Nodeable and ScriptConvertible interfaces
	@Override
	public Object convert() {
		return this.m_archetype;
	}

	// Function bodies are contained via a series of if statements in execute
	// Template will be null if the object is exactly of this type and is constructing, and thus must be created then
	@Override
	public ScriptValue_Abstract execute(Referenced ref, String name, List<ScriptValue_Abstract> params, ScriptTemplate_Abstract rawTemplate) throws Exception_Nodeable {
		assert Debugger.openNode("Faux Template Executions", "Executing archetype faux template function (" + ScriptFunction.getDisplayableFunctionName(name) + ")");
		FauxTemplate_Archetype template = (FauxTemplate_Archetype) rawTemplate;
		assert Debugger.addSnapNode("Template provided", template);
		assert Debugger.addSnapNode("Parameters provided", params);
		if (name == null || name.equals("")) {
			if (template == null) {
				template = (FauxTemplate_Archetype) this.createObject(ref, template);
			}
			template.setArchetype(new Archetype(this.getEnvironment(), Parser.getString(params.get(0))));
			params.clear();
		} else if (name.equals("addParent")) {
			template.getArchetype().addParent(Parser.getAce(params.get(0)));
			assert Debugger.closeNode();
			return null;
		} else if (name.equals("getName")) {
			ScriptValue_Abstract returning = Parser.getRiffString(this.getEnvironment(), template.getArchetype().getName());
			assert Debugger.closeNode();
			return returning;
		} else if (name.equals("getParents")) {
			List<ScriptValue_Abstract> parents = new LinkedList<ScriptValue_Abstract>();
			for (Ace parent : template.getArchetype().getParents()) {
				parents.add(Parser.getRiffAce(parent));
			}
			ScriptValue_Abstract returning = Parser.getRiffList(this.getEnvironment(), parents);
			assert Debugger.closeNode();
			return null;
		}
		ScriptValue_Abstract returning = this.getExtendedFauxClass().execute(ref, name, params, template);
		assert Debugger.closeNode();
		return returning;
	}

	public Archetype getArchetype() {
		return this.m_archetype;
	}

	// All functions must be defined here. All function bodies are defined in 'execute'.
	@Override
	public void initialize() throws Exception_Nodeable {
		assert Debugger.openNode("Faux Template Initializations", "Initializing archetype faux template");
		List<ScriptValue_Abstract> fxnParams = new LinkedList<ScriptValue_Abstract>();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.STRING));
		this.addConstructor(this.getType(), fxnParams);
		this.disableFullCreation();
		this.getExtendedClass().initialize();
		fxnParams = new LinkedList<ScriptValue_Abstract>();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.createType(this.getEnvironment(), FauxTemplate_Ace.ACESTRING)));
		this.addFauxFunction("addParent", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		this.addFauxFunction("getName", ScriptValueType.STRING, new LinkedList<ScriptValue_Abstract>(), ScriptKeywordType.PUBLIC, false, false);
		this.addFauxFunction("getParents", ScriptValueType.createType(this.getEnvironment(), FauxTemplate_List.LISTSTRING), new LinkedList<ScriptValue_Abstract>(), ScriptKeywordType.PUBLIC, false, false);
		assert Debugger.closeNode();
	}

	// Define default constructor here
	@Override
	public ScriptTemplate instantiateTemplate() {
		return new FauxTemplate_Archetype(this.getEnvironment(), this.getType());
	}

	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Archetype Faux Script-Element");
		assert super.nodificate();
		assert Debugger.addNode(this.m_archetype);
		assert Debugger.closeNode();
		return true;
	}

	public void setArchetype(Archetype archetype) {
		this.m_archetype = archetype;
	}
}
