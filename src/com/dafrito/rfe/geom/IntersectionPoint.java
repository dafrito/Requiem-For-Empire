package com.dafrito.rfe.geom;

import com.dafrito.rfe.geom.points.Point;
import com.dafrito.rfe.inspect.Inspectable;

@Inspectable
public class IntersectionPoint {
	private final Point point;
	private final boolean isTangent;

	public IntersectionPoint(final Point point, final boolean isTangent) {
		this.isTangent = isTangent;
		this.point = point;
	}

	@Inspectable
	public Point getPoint() {
		return this.point;
	}

	@Inspectable
	public boolean isTangent() {
		return this.isTangent;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof IntersectionPoint)) {
			return false;
		}
		IntersectionPoint other = (IntersectionPoint) obj;
		return this.getPoint().equals(other.getPoint()) &&
				this.isTangent() == other.isTangent();
	}

	@Override
	public int hashCode() {
		int result = this.isTangent() ? 31 : 19;
		result = 31 * result + this.getPoint().hashCode();
		return result;
	}

	@Override
	public String toString() {
		return String.format("Intersection%s@%s", this.isTangent() ? "[tangent]" : "", this.getPoint());
	}
}
