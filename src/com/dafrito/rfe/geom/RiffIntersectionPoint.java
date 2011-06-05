/**
 * 
 */
package com.dafrito.rfe.geom;

import com.dafrito.rfe.geom.points.Point;
import com.dafrito.rfe.inspect.Inspectable;

@Inspectable
public class RiffIntersectionPoint {
	private Point intersect;
	private int location;

	public RiffIntersectionPoint(Point intersect, int location) {
		this.intersect = intersect;
		this.location = location;
	}

	@Inspectable
	public Point getIntersection() {
		return this.intersect;
	}

	@Inspectable
	public int getLocation() {
		return this.location;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof RiffIntersectionPoint)) {
			return false;
		}
		return this.getIntersection().equals(((RiffIntersectionPoint) o).getIntersection());
	}

}