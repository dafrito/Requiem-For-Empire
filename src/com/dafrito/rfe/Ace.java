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
	private final Archetype archetype;
	private double efficiency;

	public Ace(Archetype archetype, double efficiency) {
		if (archetype == null) {
			throw new NullPointerException("archetype must not be null");
		}
		this.archetype = archetype;
		this.setEfficiency(efficiency);
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
		if (Double.isNaN(efficiency)) {
			throw new IllegalArgumentException("efficiency must not be NaN");
		}
		this.efficiency = efficiency;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Ace)) {
			return false;
		}
		Ace other = (Ace) obj;
		if (!this.getArchetype().equals(other.getArchetype())) {
			return false;
		}
		return this.getEfficiency() == other.getEfficiency();
	}

	@Override
	public int hashCode() {
		int result = 23;
		result = 31 * result + this.getArchetype().hashCode();
		long efficiencyBits = Double.doubleToLongBits(this.getEfficiency());
		result = 31 * result + (int) (efficiencyBits ^ (efficiencyBits >>> 32));
		return result;
	}

	@Override
	public String toString() {
		return String.format("Ace(%s@%f)", this.getArchetype().getName(), this.getEfficiency());
	}
}
