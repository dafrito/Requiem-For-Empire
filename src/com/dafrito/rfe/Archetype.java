package com.dafrito.rfe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.dafrito.rfe.inspect.Inspectable;
import com.dafrito.rfe.script.ScriptConvertible;
import com.dafrito.rfe.script.ScriptEnvironment;
import com.dafrito.rfe.script.ScriptValueType;

@Inspectable
public class Archetype implements ScriptConvertible {

	private final String name;
	private final List<Ace> parents = new ArrayList<Ace>();
	private final ScriptEnvironment environment;

	public Archetype(ScriptEnvironment env, String name) {
		this.environment = env;
		this.name = name;
	}

	public ScriptEnvironment getEnvironment() {
		return this.environment;
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
	public Object convert() {
		FauxTemplate_Archetype archetype = new FauxTemplate_Archetype(this.getEnvironment(), ScriptValueType.createType(this.getEnvironment(), FauxTemplate_Archetype.ARCHETYPESTRING));
		archetype.setArchetype(this);
		return archetype;
	}

	@Override
	public boolean equals(Object o) {
		// XXX This implementation is invalid, since we consider equal strings equal to us.
		if (o instanceof String) {
			return this.name.equals(o);
		}
		if (o instanceof Archetype) {
			return this.name.equals(((Archetype) o).getName());
		}
		return false;
	}

}
