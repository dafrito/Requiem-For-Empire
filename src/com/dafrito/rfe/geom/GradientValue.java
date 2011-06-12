package com.dafrito.rfe.geom;

public interface GradientValue<T extends GradientValue<T>> {
	public T sample(double intensity);

	public String getName();
}
