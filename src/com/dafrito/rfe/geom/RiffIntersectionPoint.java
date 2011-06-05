/**
 * 
 */
package com.dafrito.rfe.geom;

import com.dafrito.rfe.geom.points.Point;
import com.dafrito.rfe.inspect.Inspectable;

@Inspectable
public class RiffIntersectionPoint {
	private final Point intersection;
	private final int listOffset;

	public RiffIntersectionPoint(final Point intersection, final int listOffset) {
		if (intersection == null) {
			throw new NullPointerException("intersection must not be null");
		}
		this.intersection = intersection;
		this.listOffset = listOffset;
	}

	@Inspectable
	public Point getIntersection() {
		return this.intersection;
	}

	@Inspectable
	public int getListOffset() {
		return this.listOffset;
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
				this.getListOffset() == other.getListOffset();
	}

	@Override
	public int hashCode() {
		int result = 13;
		result = 31 * result + this.getIntersection().hashCode();
		result = 31 * result + this.getListOffset();
		return result;
	}

	@Override
	public String toString() {
		return String.format("RiffIntersectionPoint[%s]@%s", this.getListOffset(), this.getIntersection());
	}
}