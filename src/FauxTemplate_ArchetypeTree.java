import java.util.LinkedList;
import java.util.List;

public class FauxTemplate_ArchetypeTree extends FauxTemplate implements ScriptConvertible, Nodeable {
	public static final String ARCHETYPETREESTRING = "ArchetypeTree";
	private ArchetypeMapNode m_tree;

	public FauxTemplate_ArchetypeTree(ScriptEnvironment env) {
		super(env, ScriptValueType.createType(env, ARCHETYPETREESTRING), ScriptValueType.getObjectType(env), new LinkedList<ScriptValueType>(), false);
	}

	public FauxTemplate_ArchetypeTree(ScriptEnvironment env, ScriptValueType type) {
		super(env, type);
	}

	// Nodeable and ScriptConvertible interfaces
	@Override
	public Object convert() {
		return m_tree;
	}

	// Function bodies are contained via a series of if statements in execute
	// Template will be null if the object is exactly of this type and is constructing, and thus must be created then
	@Override
	public ScriptValue_Abstract execute(Referenced ref, String name, List<ScriptValue_Abstract> params, ScriptTemplate_Abstract rawTemplate) throws Exception_Nodeable {
		assert Debugger.openNode("Faux Template Executions", "Executing archetype tree faux template function (" + ScriptFunction.getDisplayableFunctionName(name) + ")");
		FauxTemplate_ArchetypeTree template = (FauxTemplate_ArchetypeTree) rawTemplate;
		assert Debugger.addSnapNode("Template provided", template);
		assert Debugger.addSnapNode("Parameters provided", params);
		if (name == null || name.equals("")) {
			if (template == null) {
				template = (FauxTemplate_ArchetypeTree) createObject(ref, template);
			}
			template.setTree(new ArchetypeMapNode(Parser.getArchetype(params.get(0))));
			params.clear();
		} else if (name.equals("addAsset")) {
			template.getTree().addAsset(Parser.getAsset(params.get(0)));
			assert Debugger.closeNode();
			return null;
		} else if (name.equals("getAssetsOfType")) {
			List<ScriptValue_Abstract> assets = new LinkedList<ScriptValue_Abstract>();
			for (Asset asset : template.getTree().getAssetsOfType(Parser.getArchetype(params.get(0)))) {
				assets.add(Parser.getRiffAsset(getEnvironment(), asset));
			}
			ScriptValue_Abstract returning = Parser.getRiffList(getEnvironment(), assets);
			assert Debugger.closeNode();
			return returning;
		}
		ScriptValue_Abstract returning = getExtendedFauxClass().execute(ref, name, params, template);
		assert Debugger.closeNode();
		return returning;
	}

	public ArchetypeMapNode getTree() {
		return m_tree;
	}

	// All functions must be defined here. All function bodies are defined in 'execute'.
	@Override
	public void initialize() throws Exception_Nodeable {
		assert Debugger.openNode("Faux Template Initializations", "Initializing archetype tree faux template");
		List<ScriptValue_Abstract> fxnParams = new LinkedList<ScriptValue_Abstract>();
		fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.createType(getEnvironment(), FauxTemplate_Archetype.ARCHETYPESTRING)));
		addConstructor(getType(), fxnParams);
		disableFullCreation();
		getExtendedClass().initialize();
		fxnParams = new LinkedList<ScriptValue_Abstract>();
		fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.createType(getEnvironment(), FauxTemplate_Asset.ASSETSTRING)));
		addFauxFunction("addAsset", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		addFauxFunction("getAssetsOfType", ScriptValueType.createType(getEnvironment(), FauxTemplate_List.LISTSTRING), new LinkedList<ScriptValue_Abstract>(), ScriptKeywordType.PUBLIC, false, false);
		assert Debugger.closeNode();
	}

	// Define default constructor here
	@Override
	public ScriptTemplate instantiateTemplate() {
		return new FauxTemplate_Archetype(getEnvironment(), getType());
	}

	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Archetype Tree Faux Script-Element");
		assert super.nodificate();
		assert Debugger.addNode(m_tree);
		assert Debugger.closeNode();
		return true;
	}

	public void setTree(ArchetypeMapNode tree) {
		m_tree = tree;
	}
}
