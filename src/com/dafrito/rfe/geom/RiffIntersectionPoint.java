/**
 * 
 */
package com.dafrito.rfe.geom;

import com.dafrito.rfe.geom.points.Point;
import com.dafrito.rfe.inspect.Inspectable;

@Inspectable
public class RiffIntersectionPoint {
	private final Point intersect;
	private final int location;

	public RiffIntersectionPoint(final Point intersect, final int location) {
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