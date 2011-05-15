public class ScriptValue_Faux implements Nodeable,ScriptValue_Abstract{
	private final ScriptEnvironment m_environment;
	private final ScriptValueType m_type;
	public ScriptValue_Faux(ScriptEnvironment env,ScriptValueType type){
		m_environment=env;
		m_type=type;
	}
	public ScriptEnvironment getEnvironment(){return m_environment;}
	public ScriptValueType getType(){return m_type;}
	public boolean isConvertibleTo(ScriptValueType type){return ScriptValueType.isConvertibleTo(getEnvironment(),getType(),type);}
	public ScriptValue_Abstract castToType(Referenced ref,ScriptValueType type)throws Exception_Nodeable{throw new Exception_InternalError(getEnvironment(),"Invalid call in ScriptValue_Faux");}
	protected void setType(ScriptValueType type){throw new Exception_InternalError(getEnvironment(),"Invalid call in ScriptValue_Faux");}
	public ScriptValue_Abstract setValue(Referenced ref, ScriptValue_Abstract value)throws Exception_Nodeable{throw new Exception_InternalError(getEnvironment(),"Invalid call in ScriptValue_Faux");}
	public ScriptValue_Abstract getValue()throws Exception_Nodeable{throw new Exception_InternalError(getEnvironment(),"Invalid call in ScriptValue_Faux");}
	public boolean valuesEqual(Referenced ref, ScriptValue_Abstract rhs)throws Exception_Nodeable{throw new Exception_InternalError(getEnvironment(),"Invalid call in ScriptValue_Faux");}
	public int valuesCompare(Referenced ref, ScriptValue_Abstract rhs)throws Exception_Nodeable{throw new Exception_InternalError(getEnvironment(),"Invalid call in ScriptValue_Faux");}
	public boolean nodificate(){
		assert Debugger.addNode("Faux Script-Value ("+getType()+")");
		return true;
	}
}
