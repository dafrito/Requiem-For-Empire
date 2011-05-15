import java.io.*;
public class Exception_InternalError extends RuntimeException implements Nodeable{
	private int m_offset,m_lineNumber,m_length;
	private String m_line,m_filename,m_message;
	private final ScriptEnvironment m_environment;
	private final Object m_object;
	public Exception_InternalError(String message){
		this((ScriptEnvironment)null,message);
	}
	public Exception_InternalError(Referenced ref,String message){
		this(ref.getDebugReference().getEnvironment(),ref.getDebugReference(),message);
	}
	public Exception_InternalError(String message,Exception exception){
		this(null,exception,message);
	}
	public Exception_InternalError(ScriptEnvironment env,Exception object){
		this(env,object,"");
	}
	public Exception_InternalError(ScriptEnvironment env,String message){
		m_environment=env;
		m_object=null;
		m_filename=null;
		m_line=null;
		m_offset=0;
		m_lineNumber=0;
		m_length=-1;
		m_message=message;
	}
	public Exception_InternalError(ScriptEnvironment env,Object element,String message){
		m_environment=env;
		m_object=element;
		m_filename=null;
		m_line=null;
		m_offset=0;
		m_lineNumber=-1;
		m_length=-1;
		m_message=message;
	}
	public Exception_InternalError(ScriptEnvironment env,ScriptElement element,String message){
		m_object=null;
		m_environment=env;
		m_message=message;
		if(element!=null){
			m_filename=element.getFilename();
			m_lineNumber=element.getLineNumber();
			m_line=element.getOriginalString();
			m_offset=element.getOffset();
			m_length=element.getLength();
		}else{
			m_offset=0;
			m_line=null;
			m_filename=null;
			m_lineNumber=-1;
			m_length=-1;
		}
	}
	public boolean isAnonymous(){return m_filename==null;}
	public String getOriginalString(){return m_line;}
	public int getLength(){return m_length;}
	public int getOffset(){return m_offset;}
	public String getFilename(){return m_filename;}
	public int getLineNumber(){return m_lineNumber;}
	public String getMessage(){return "(Internal Error) " + getName();}
	public String getName(){return m_message;}
	public ScriptEnvironment getEnvironment(){return m_environment;}
	public String getFragment(){
		return getOriginalString().substring(getOffset(),getOffset()+getLength());
	}
	public boolean nodificate(){
		boolean debug=false;
		assert debug=true;
		if(!debug){return true;}
		assert Debugger.openNode("Exceptions and Errors",getMessage());
		if(m_object!=null){assert Debugger.addNode(m_object);}
		StringWriter writer;
		printStackTrace(new PrintWriter(writer=new StringWriter()));
		String[] messages=writer.toString().split("\n");
		boolean flag=false;
		int added=0;
		for(int i=0;i<messages.length;i++){
			if(!flag&&messages[i].trim().indexOf("at")==0){
				flag=true;
				assert Debugger.openNode("Call-stack");
			}
			if(flag&&added==5){assert Debugger.openNode("Full Call-Stack");}
			if(messages[i].trim().indexOf("^")!=0){
				assert Debugger.addNode(messages[i].trim());
			}
			if(flag){added++;}
		}
		if(added>5){assert Debugger.closeNode();}
		if(flag){assert Debugger.closeNode();}
		assert Debugger.closeNode();
		return true;
	}
	public String toString(){
		if(m_object!=null){
			if(m_object instanceof Exception){
				StringWriter writer;
				((Exception)m_object).printStackTrace(new PrintWriter(writer=new StringWriter()));
				return getMessage()+"\nObject given: "+m_object+"\n"+writer;
			}else{
				return getMessage()+"\nObject given: "+m_object;
			}
		}
		if(m_filename==null){return getMessage();}
		while(m_line.indexOf("\t")==0||m_line.indexOf(" ")==0){
			m_line=m_line.substring(1);
			m_offset--;
			if(m_offset<0){m_offset=0;}
		}
		String string=m_filename+":"+m_lineNumber+": "+getMessage()+"\n\t"+m_line;
		string += "\n\t";
		for(int i=0;i<m_offset;i++){
			string += " ";
		}
		string += "^";
		return string;
	}
}
