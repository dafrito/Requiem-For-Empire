package com.dafrito.rfe;

import com.dafrito.rfe.inspect.Inspectable;

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
public class Ace {
	private Archetype archetype;
	private double efficiency;

	public Ace(Archetype archetype, double efficiency) {
		this.archetype = archetype;
		this.efficiency = efficiency;
	}

	@Inspectable
	public Archetype getArchetype() {
		return this.archetype;
	}

	@Inspectable
	public double getEfficiency() {
		return this.efficiency;
	}

	public void setEfficiency(double efficiency) {
		this.efficiency = efficiency;
	}
}
