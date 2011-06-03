/**
 * 
 */
package com.dafrito.rfe.gui.style.dimensions;

import com.dafrito.rfe.inspect.Inspectable;

public abstract class StylesheetMagnitude<T> {

	private T magnitude;

	protected StylesheetMagnitude(T magnitude) {
		this.magnitude = magnitude;
	}

	@Inspectable
	public T getMagnitude() {
		return this.magnitude;
	}

	public void setMagnitude(T magnitude) {
		this.magnitude = magnitude;
	}
}