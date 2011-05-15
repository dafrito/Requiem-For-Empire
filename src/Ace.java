public class Ace implements ScriptConvertible, Nodeable {
	private Archetype m_archetype;
	private double m_efficiency;
	private ScriptEnvironment m_environment;

	public Ace(ScriptEnvironment env, Archetype archetype, double efficiency) {
		m_environment = env;
		m_archetype = archetype;
		m_efficiency = efficiency;
	}

	@Override
	public Object convert() {
		FauxTemplate_Ace ace = new FauxTemplate_Ace(getEnvironment(), ScriptValueType.createType(getEnvironment(), FauxTemplate_Ace.ACESTRING));
		ace.setAce(this);
		return ace;
	}

	public Archetype getArchetype() {
		return m_archetype;
	}

	public double getEfficiency() {
		return m_efficiency;
	}

	public ScriptEnvironment getEnvironment() {
		return m_environment;
	}

	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Ace");
		assert Debugger.addNode(m_archetype);
		assert Debugger.addNode("Efficiency: " + m_efficiency);
		assert Debugger.closeNode();
		return true;
	}

	public void setEfficiency(double efficiency) {
		m_efficiency = efficiency;
	}
}
