package com.dafrito.rfe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.dafrito.rfe.inspect.Inspectable;

@Inspectable
public class Archetype {

	private final String name;
	private final List<Ace> parents = new ArrayList<Ace>();

	public Archetype(String name) {
		this.name = name;
	}

	public void addParent(Ace ace) {
		this.parents.add(ace);
	}

	@Inspectable
	public String getName() {
		return this.name;
	}

	@Inspectable("Aces")
	public List<Ace> getParents() {
		return Collections.unmodifiableList(this.parents);
	}

	public Archetype getRoot() {
		if (this.parents.isEmpty()) {
			return this;
		}
		return this.parents.get(0).getArchetype().getRoot();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		return o instanceof Archetype &&
				this.name.equals(((Archetype) o).getName());
	}

	@Override
	public int hashCode() {
		return this.name.hashCode();
	}

	@Override
	public String toString() {
		return String.format("Archetype(%s)", this.getName());
	}

}
