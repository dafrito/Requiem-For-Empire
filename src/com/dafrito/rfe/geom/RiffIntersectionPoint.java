/**
 * 
 */
package com.dafrito.rfe.geom;

import com.dafrito.rfe.geom.points.Point;
import com.dafrito.rfe.inspect.Inspectable;

@Inspectable
public class RiffIntersectionPoint {
	private final Point intersection;
	private final int location;

	public RiffIntersectionPoint(final Point intersection, final int location) {
		if (intersection == null) {
			throw new NullPointerException("intersection must not be null");
		}
		this.intersection = intersection;
		this.location = location;
	}

	@Inspectable
	public Point getIntersection() {
		return this.intersection;
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