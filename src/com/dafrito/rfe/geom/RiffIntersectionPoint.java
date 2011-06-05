/**
 * 
 */
package com.dafrito.rfe.geom;

import com.dafrito.rfe.geom.points.Point;
import com.dafrito.rfe.gui.debug.Debugger;
import com.dafrito.rfe.inspect.Nodeable;

public class RiffIntersectionPoint implements Nodeable {
	private Point intersect;
	private int location;

	public RiffIntersectionPoint(Point intersect, int location) {
		this.intersect = intersect;
		this.location = location;
	}

	public Point getIntersection() {
		return this.intersect;
	}

	public int getLocation() {
		return this.location;
	}

	@Override
	public void nodificate() {
		assert Debugger.openNode("Intersection-Point Struct");
		assert Debugger.addNode("Intersection-Point: " + this.intersect);
		assert Debugger.addNode("Point-list offset of intersection: " + this.location);
		assert Debugger.closeNode();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof RiffIntersectionPoint)) {
			return false;
		}
		return this.getIntersection().equals(((RiffIntersectionPoint) o).getIntersection());
	}

}