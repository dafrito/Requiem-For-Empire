/**
 * 
 */
package com.dafrito.rfe.inspect;

/**
 * An exception that indicates problems during inspection.
 * 
 * @author Aaron Faanes
 * 
 */
public class InspectionException extends RuntimeException {

	/**
	 * @param cause
	 *            the underlying exception
	 */
	public InspectionException(Throwable cause) {
		super(cause);
	}

	private static final long serialVersionUID = 4962983507416560098L;

}
