public class Ace implements ScriptConvertible, Nodeable {
	private Archetype archetype;
	private double efficiency;
	private ScriptEnvironment environment;

	public Ace(ScriptEnvironment env, Archetype archetype, double efficiency) {
		this.environment = env;
		this.archetype = archetype;
		this.efficiency = efficiency;
	}

	@Override
	public Object convert() {
		FauxTemplate_Ace ace = new FauxTemplate_Ace(this.getEnvironment(), ScriptValueType.createType(this.getEnvironment(), FauxTemplate_Ace.ACESTRING));
		ace.setAce(this);
		return ace;
	}

	public Archetype getArchetype() {
		return this.archetype;
	}

	public double getEfficiency() {
		return this.efficiency;
	}

	public ScriptEnvironment getEnvironment() {
		return this.environment;
	}

	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Ace");
		assert Debugger.addNode(this.archetype);
		assert Debugger.addNode("Efficiency: " + this.efficiency);
		assert Debugger.closeNode();
		return true;
	}

	public void setEfficiency(double efficiency) {
		this.efficiency = efficiency;
	}
}
