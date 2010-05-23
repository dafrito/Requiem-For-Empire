package com.dafrito.economy;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Nodeable;
import com.dafrito.geom.Point;
import com.dafrito.script.ScriptConvertible;
import com.dafrito.script.ScriptEnvironment;
import com.dafrito.script.templates.FauxTemplate_Asset;

public class Asset implements Nodeable, ScriptConvertible {
    private ScriptEnvironment environment;
    private Map<String, Object> properties = new HashMap<String, Object>();
    private Point location;
    private List<Ace> aces = new LinkedList<Ace>();

    public Asset(ScriptEnvironment environment) {
        this.environment = environment;
    }

    public ScriptEnvironment getEnvironment() {
        return this.environment;
    }

    public void setProperty(String name, Object prop) {
        this.properties.put(name, prop);
    }

    public Object getProperty(String name) {
        return this.properties.get(name);
    }

    public Point getLocation() {
        return this.location;
    }

    public void setLocation(Point location) {
        this.location = location;
    }

    public void addAce(Ace ace) {
        this.aces.add(ace);
    }

    public List<Ace> getAces() {
        return this.aces;
    }

    // ScriptConvertible implementation
    public Object convert() {
        FauxTemplate_Asset asset = new FauxTemplate_Asset(getEnvironment(), getEnvironment().getTemplate(
            FauxTemplate_Asset.ASSETSTRING).getType());
        asset.setAsset(this);
        return asset;
    }

    // Nodeable implementation
    public boolean nodificate() {
        assert LegacyDebugger.open("Asset");
        assert LegacyDebugger.addSnapNode("Location", getLocation());
        if(this.aces != null && this.aces.size() > 0) {
            assert LegacyDebugger.addSnapNode("Archetype Conversion Efficiencies (" + this.aces.size() + " ACE(s))", this.aces);
        }
        if(this.properties.size() == 1) {
            assert LegacyDebugger.addSnapNode("Properties (1 property)", this.properties);
        } else {
            assert LegacyDebugger.addSnapNode("Properties (" + this.properties.size() + " properties)", this.properties);
        }
        assert LegacyDebugger.close();
        return true;
    }
}
