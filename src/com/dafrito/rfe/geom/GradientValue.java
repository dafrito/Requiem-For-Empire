package com.dafrito.rfe.geom;

public interface GradientValue<T extends GradientValue<T>> {
	public T diluted(double intensity);

	public String getName();
}
