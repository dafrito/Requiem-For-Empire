/**
 * 
 */
package com.dafrito.rfe.style;

import com.dafrito.rfe.inspect.Inspectable;

/**
 * A stylesheet element that represents a percentage height.
 * 
 * @author Aaron Faanes
 * @see StylesheetAbsoluteHeightElement
 * @see StylesheetPercentageWidthElement
 */
@Inspectable
public class StylesheetPercentageHeightElement extends StylesheetMagnitude<Double> {

	public StylesheetPercentageHeightElement(double magnitude) {
		super(magnitude);
	}

	@Override
	public String toString() {
		return "percentage-height";
	}

}