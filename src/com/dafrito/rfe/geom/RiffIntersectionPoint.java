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
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof RiffIntersectionPoint)) {
			return false;
		}
		RiffIntersectionPoint other = (RiffIntersectionPoint) obj;
		return this.getIntersection().equals(other.getIntersection()) &&
				this.getLocation() == other.getLocation();
	}

	@Override
	public int hashCode() {
		int result = 13;
		result = 31 * result + this.getIntersection().hashCode();
		result = 31 * result + this.getLocation();
		return result;
	}

	@Override
	public String toString() {
		return String.format("RiffIntersectionPoint[%s]@%s", this.getLocation(), this.getIntersection());
	}
}