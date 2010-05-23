package com.dafrito.economy;
import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Nodeable;
import com.dafrito.script.ScriptConvertible;
import com.dafrito.script.ScriptEnvironment;
import com.dafrito.script.templates.FauxTemplate_Ace;
import com.dafrito.script.types.ScriptValueType;

public class Ace implements ScriptConvertible, Nodeable {
    private final ScriptEnvironment environment;
    private final Archetype archetype;
    private double efficiency;
   
    public Ace(ScriptEnvironment environment, Archetype archetype, double efficiency) {
        this.environment = environment;
        this.archetype = archetype;
        this.efficiency = efficiency;
    }

    public ScriptEnvironment getEnvironment() {
        return this.environment;
    }

    public Archetype getArchetype() {
        return this.archetype;
    }

    public double getEfficiency() {
        return this.efficiency;
    }

    public void setEfficiency(double efficiency) {
        this.efficiency = efficiency;
    }

    public Object convert() {
        FauxTemplate_Ace ace = new FauxTemplate_Ace(getEnvironment(), ScriptValueType.createType(
            getEnvironment(),
            FauxTemplate_Ace.ACESTRING));
        ace.setAce(this);
        return ace;
    }

    public boolean nodificate() {
        assert LegacyDebugger.open("Ace");
        assert LegacyDebugger.addNode(this.archetype);
        assert LegacyDebugger.addNode("Efficiency: " + this.efficiency);
        assert LegacyDebugger.close();
        return true;
    }
}
