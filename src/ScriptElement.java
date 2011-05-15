public class ScriptElement implements Nodeable,Referenced{
	private final ScriptEnvironment m_environment;
	private final int m_lineNumber,m_originalLineOffset,m_length;
	private final String m_original,m_filename;
	public ScriptElement(){
		m_environment=null;
		m_filename="";
		m_original="";
		m_lineNumber=-1;
		m_length=-1;
		m_originalLineOffset=0;
	}
	public ScriptElement(ScriptEnvironment env){
		m_environment=env;
		m_filename="";
		m_original="";
		m_lineNumber=-1;
		m_length=-1;
		m_originalLineOffset=0;
	}
	public ScriptElement(ScriptEnvironment env, String filename, int lineNumber, String original,int length){
		m_environment=env;
		m_filename=filename;
		m_lineNumber=lineNumber;
		m_original=original;
		m_originalLineOffset=0;
		m_length=length;
	}
	public ScriptElement(Referenced element){this(element.getDebugReference());}
	public ScriptElement(ScriptElement element){this(element.getEnvironment(),element,0,element.getLength());}
	public ScriptElement(ScriptElement element,int oLO,int length){this(element.getEnvironment(),element,oLO,length);}
	public ScriptElement(ScriptEnvironment env,ScriptElement element,int oLO,int length){
		m_environment=env;
		if(element!=null){
			m_filename=element.getFilename();
			m_lineNumber=element.getLineNumber();
			m_originalLineOffset=element.getOffset()+oLO;
			m_original=element.getOriginalString();
			m_length=length;
		}else{
			m_filename="";
			m_original="";
			m_lineNumber=-1;
			m_originalLineOffset=0;
			m_length=-1;
		}
	}
	public ScriptElement getDebugReference(){return this;}
	public int getLength(){return m_length;}
	public ScriptEnvironment getEnvironment(){assert m_environment!=null:"Environment is null." +this;return m_environment;}
	public String getFilename(){return m_filename;}
	public String getFragment(){return m_original.substring(getOffset(),getLength());}
	public String getOriginalString(){return m_original;}
	public int getLineNumber(){return m_lineNumber;}
	public int getOffset(){return m_originalLineOffset;}
	public boolean nodificate(){
		if(getLineNumber()==-1){assert Debugger.addNode("ScriptElement: No information provided");return true;}
		assert Debugger.addSnapNode("ScriptElement ("+getFilename()+" @ "+getLineNumber()+")",Debugger.getString(DebugString.ORIGINALSTRING)+getOriginalString()+"'");
		return true;
	}
}
