package com.dafrito.rfe;
public class FailedActionException extends OrderException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6613983177694847620L;
	private Exception exception;

	public FailedActionException(Exception ex) {
		this.exception = ex;
	}

	@Override
	public String getMessage() {
		String string = new String();
		string += "An action failed, returning this exception:";
		string += "\n" + this.exception;
		return string;
	}
}
