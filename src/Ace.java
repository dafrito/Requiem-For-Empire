public class Ace implements ScriptConvertible, Nodeable {
	private Archetype m_archetype;
	private double m_efficiency;
	private ScriptEnvironment m_environment;

	public Ace(ScriptEnvironment env, Archetype archetype, double efficiency) {
		this.m_environment = env;
		this.m_archetype = archetype;
		this.m_efficiency = efficiency;
	}

	@Override
	public Object convert() {
		FauxTemplate_Ace ace = new FauxTemplate_Ace(this.getEnvironment(), ScriptValueType.createType(this.getEnvironment(), FauxTemplate_Ace.ACESTRING));
		ace.setAce(this);
		return ace;
	}

	public Archetype getArchetype() {
		return this.m_archetype;
	}

	public double getEfficiency() {
		return this.m_efficiency;
	}

	public ScriptEnvironment getEnvironment() {
		return this.m_environment;
	}

	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Ace");
		assert Debugger.addNode(this.m_archetype);
		assert Debugger.addNode("Efficiency: " + this.m_efficiency);
		assert Debugger.closeNode();
		return true;
	}

	public void setEfficiency(double efficiency) {
		this.m_efficiency = efficiency;
	}
}
