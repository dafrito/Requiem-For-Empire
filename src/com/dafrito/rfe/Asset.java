package com.dafrito.rfe;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.dafrito.rfe.geom.points.Point;
import com.dafrito.rfe.inspect.Inspectable;

@Inspectable
public class Asset {
	private Map<String, Object> properties = new HashMap<String, Object>();
	private Point location;
	private List<Ace> aces = new LinkedList<Ace>();

	public void addAce(Ace ace) {
		this.aces.add(ace);
	}

	@Inspectable
	public List<Ace> getAces() {
		return Collections.unmodifiableList(this.aces);
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

	public void setLocation(Point location) {
		this.location = location;
	}

	public void setProperty(String name, Object prop) {
		this.properties.put(name, prop);
	}
}
