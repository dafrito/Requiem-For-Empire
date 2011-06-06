package com.dafrito.rfe;

import com.dafrito.rfe.inspect.Inspectable;
import com.dafrito.rfe.script.ScriptConvertible;
import com.dafrito.rfe.script.ScriptEnvironment;
import com.dafrito.rfe.script.proxies.FauxTemplate_Ace;
import com.dafrito.rfe.script.values.ScriptValueType;

/**
 * Relate the efficiency of some {@link Asset} to represent some
 * {@link Archetype}. For example, if the archetype is "combustible" than an
 * asset with high efficiency is highly combustible. Expressing representation
 * in floating-point terms lets us have situations degrade, such as having
 * poorly combustible things still be combustible.
 * 
 * @author Aaron Faanes
 * 
 */
@Inspectable
public class Ace implements ScriptConvertible<FauxTemplate_Ace> {
	private Archetype archetype;
	private double efficiency;
	private ScriptEnvironment environment;

	public Ace(ScriptEnvironment env, Archetype archetype, double efficiency) {
		this.environment = env;
		this.archetype = archetype;
		this.efficiency = efficiency;
	}

	@Override
	public FauxTemplate_Ace convert(ScriptEnvironment env) {
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
