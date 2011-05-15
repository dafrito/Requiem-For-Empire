public class FailedActionException extends OrderException{
	private Exception m_exception;
	public FailedActionException(Exception ex){
		m_exception = ex;
	}
	public String getMessage(){
		String string = new String();
		string += "An action failed, returning this exception:";
		string += "\n" + m_exception;
		return string;
	}
}
