public class FailedActionException extends OrderException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6613983177694847620L;
	private Exception m_exception;

	public FailedActionException(Exception ex) {
		m_exception = ex;
	}

	@Override
	public String getMessage() {
		String string = new String();
		string += "An action failed, returning this exception:";
		string += "\n" + m_exception;
		return string;
	}
}
