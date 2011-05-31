package com.dafrito.rfe;

import com.dafrito.rfe.inspect.Inspectable;
import com.dafrito.rfe.script.ScriptConvertible;
import com.dafrito.rfe.script.ScriptEnvironment;
import com.dafrito.rfe.script.ScriptValueType;
import com.dafrito.rfe.script.proxies.FauxTemplate_Ace;

@Inspectable
public class Ace implements ScriptConvertible {
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

	@Inspectable
	public Archetype getArchetype() {
		return this.archetype;
	}

	@Inspectable
	public double getEfficiency() {
		return this.efficiency;
	}

	public ScriptEnvironment getEnvironment() {
		return this.environment;
	}

	public void setEfficiency(double efficiency) {
		this.efficiency = efficiency;
	}
}
