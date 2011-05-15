public class ScriptValueType_ValueDeferrer extends ScriptValueType{
	private ScriptValue_Abstract m_value;
	public ScriptValueType_ValueDeferrer(ScriptValue_Abstract value){
		super(value.getEnvironment());
		assert value!=null;
		m_value=value;
	}
	public ScriptValueType getBaseType()throws Exception_Nodeable{return m_value.getType();}
}
