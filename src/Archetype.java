import java.util.List;
import java.util.Vector;

public class Archetype implements ScriptConvertible, Nodeable {
	private String m_name;
	private List<Ace> m_parents = new Vector<Ace>();
	private ScriptEnvironment m_environment;

	public Archetype(ScriptEnvironment env, String name) {
		this.m_environment = env;
		this.m_name = name;
	}

	public void addParent(Ace ace) {
		this.m_parents.add(ace);
	}

	@Override
	public Object convert() {
		FauxTemplate_Archetype archetype = new FauxTemplate_Archetype(this.getEnvironment(), ScriptValueType.createType(this.getEnvironment(), FauxTemplate_Archetype.ARCHETYPESTRING));
		archetype.setArchetype(this);
		return archetype;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof String) {
			return this.m_name.equals(o);
		}
		if (o instanceof Archetype) {
			return this.m_name.equals(((Archetype) o).getName());
		}
		return false;
	}

	public ScriptEnvironment getEnvironment() {
		return this.m_environment;
	}

	public String getName() {
		return this.m_name;
	}

	public List<Ace> getParents() {
		return this.m_parents;
	}

	public Archetype getRoot() {
		if (this.m_parents == null || this.m_parents.size() == 0) {
			return this;
		}
		return this.m_parents.get(0).getArchetype().getRoot();
	}

	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Archetype (" + this.m_name + ")");
		assert Debugger.addSnapNode("Aces (" + this.m_parents.size() + " ace(s))", this.m_parents);
		assert Debugger.closeNode();
		return true;
	}
}
