package com.dafrito.rfe;

import java.util.List;
import java.util.Vector;

import com.dafrito.rfe.inspect.Nodeable;

public class Archetype implements ScriptConvertible, Nodeable {
	private String name;
	private List<Ace> parents = new Vector<Ace>();
	private ScriptEnvironment environment;

	public Archetype(ScriptEnvironment env, String name) {
		this.environment = env;
		this.name = name;
	}

	public void addParent(Ace ace) {
		this.parents.add(ace);
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
			return this.name.equals(o);
		}
		if (o instanceof Archetype) {
			return this.name.equals(((Archetype) o).getName());
		}
		return false;
	}

	public ScriptEnvironment getEnvironment() {
		return this.environment;
	}

	public String getName() {
		return this.name;
	}

	public List<Ace> getParents() {
		return this.parents;
	}

	public Archetype getRoot() {
		if (this.parents == null || this.parents.size() == 0) {
			return this;
		}
		return this.parents.get(0).getArchetype().getRoot();
	}

	@Override
	public void nodificate() {
		assert Debugger.openNode("Archetype (" + this.name + ")");
		assert Debugger.addSnapNode("Aces (" + this.parents.size() + " ace(s))", this.parents);
		assert Debugger.closeNode();
	}
}
