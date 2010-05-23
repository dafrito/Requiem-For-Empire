package com.dafrito.economy;

import java.util.ArrayList;
import java.util.List;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Nodeable;
import com.dafrito.script.ScriptConvertible;
import com.dafrito.script.ScriptEnvironment;
import com.dafrito.script.templates.FauxTemplate_Archetype;
import com.dafrito.script.types.ScriptValueType;

public class Archetype implements ScriptConvertible, Nodeable {
    private String m_name;
    private List<Ace> m_parents = new ArrayList<Ace>();
    private ScriptEnvironment m_environment;

    public Archetype(ScriptEnvironment env, String name) {
        this.m_environment = env;
        this.m_name = name;
    }

    public ScriptEnvironment getEnvironment() {
        return this.m_environment;
    }

    public void addParent(Ace ace) {
        this.m_parents.add(ace);
    }

    public String getName() {
        return this.m_name;
    }

    public List<Ace> getParents() {
        return this.m_parents;
    }

    public Archetype getRoot() {
        if(this.m_parents == null || this.m_parents.size() == 0) {
            return this;
        }
        return this.m_parents.get(0).getArchetype().getRoot();
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Archetype))
            throw new ClassCastException();
        Archetype otherArchetype = (Archetype)obj;
        return this.getName().equals(otherArchetype.getName());
    }

    public Object convert() {
        FauxTemplate_Archetype archetype = new FauxTemplate_Archetype(getEnvironment(), ScriptValueType.createType(
            getEnvironment(),
            FauxTemplate_Archetype.ARCHETYPESTRING));
        archetype.setArchetype(this);
        return archetype;
    }

    public boolean nodificate() {
        assert LegacyDebugger.open("Archetype (" + this.m_name + ")");
        assert LegacyDebugger.addSnapNode("Aces (" + this.m_parents.size() + " ace(s))", this.m_parents);
        assert LegacyDebugger.close();
        return true;
    }
}
