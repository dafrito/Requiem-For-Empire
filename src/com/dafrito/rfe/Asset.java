package com.dafrito.rfe;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.dafrito.rfe.gui.debug.Debugger;
import com.dafrito.rfe.inspect.Inspectable;
import com.dafrito.rfe.inspect.Nodeable;
import com.dafrito.rfe.points.Point;
import com.dafrito.rfe.script.ScriptConvertible;
import com.dafrito.rfe.script.ScriptEnvironment;
import com.dafrito.rfe.script.proxies.FauxTemplate_Asset;

@Inspectable
public class Asset implements Nodeable, ScriptConvertible<FauxTemplate_Asset> {
	private ScriptEnvironment environment;
	private Map<String, Object> properties = new HashMap<String, Object>();
	private Point location;
	private List<Ace> aces = new LinkedList<Ace>();

	public Asset(ScriptEnvironment environment) {
		this.environment = environment;
	}

	public void addAce(Ace ace) {
		this.aces.add(ace);
	}

	// ScriptConvertible implementation
	@Override
	public FauxTemplate_Asset convert() {
		FauxTemplate_Asset asset = new FauxTemplate_Asset(this.getEnvironment(), this.getEnvironment().getTemplate(FauxTemplate_Asset.ASSETSTRING).getType());
		asset.setAsset(this);
		return asset;
	}

	@Inspectable
	public List<Ace> getAces() {
		return Collections.unmodifiableList(this.aces);
	}

	public ScriptEnvironment getEnvironment() {
		return this.environment;
	}

	@Inspectable
	public Point getLocation() {
		return this.location;
	}

	@Inspectable
	public Map<String, Object> getProperties() {
		return Collections.unmodifiableMap(this.properties);
	}

	public Object getProperty(String name) {
		return this.properties.get(name);
	}

	@Override
	public void nodificate() {
		assert Debugger.openNode("Asset");
		assert Debugger.addSnapNode("Location", this.getLocation());
		if (this.aces != null && this.aces.size() > 0) {
			assert Debugger.addSnapNode("Archetype Conversion Efficiencies (" + this.aces.size() + " ACE(s))", this.aces);
		}
		if (this.properties.size() == 1) {
			assert Debugger.addSnapNode("Properties (1 property)", this.properties);
		} else {
			assert Debugger.addSnapNode("Properties (" + this.properties.size() + " properties)", this.properties);
		}
		assert Debugger.closeNode();
	}

	public void setLocation(Point location) {
		this.location = location;
	}

	public void setProperty(String name, Object prop) {
		this.properties.put(name, prop);
	}
}
