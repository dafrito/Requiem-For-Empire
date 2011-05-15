public class ScriptLine extends ScriptElement implements Nodeable{
	private String m_string;
	public ScriptLine(ScriptEnvironment env, String filename,int num, String string){
		super(env,filename,num,string,string.length());
		m_string=string;
	}
	public ScriptLine(String string, ScriptLine otherLine,int oLO){
		super(otherLine, oLO,string.length());
		m_string=string;
	}
	public void setString(String string){m_string=string;}
	public String getString(){return m_string;}
	public String toString(){return getFilename()+"@"+getLineNumber()+": \"" +m_string+'"';}
	public boolean nodificate(){
		assert Debugger.openNode(Debugger.getString(DebugString.SCRIPTLINE)+m_string);
		assert super.nodificate();
		assert Debugger.closeNode();
		return true;
	}
}
