/**
 * 
 */
package com.dafrito.rfe.style;

import com.dafrito.rfe.inspect.Inspectable;

/**
 * A stylesheet element that represents absolute height.
 * 
 * @author Aaron Faanes
 * @see StylesheetAbsoluteWidthElement
 * @see StylesheetPercentageHeightElement
 */
@Inspectable
public class StylesheetAbsoluteHeightElement extends StylesheetMagnitude<Integer> {

	public StylesheetAbsoluteHeightElement(int magnitude) {
		super(magnitude);
	}

	@Override
	public String toString() {
		return "n absolute height";
	}
}