public class ScriptExecutable_InvertBoolean extends ScriptElement implements ScriptExecutable,ScriptValue_Abstract{
	private ScriptExecutable m_value;
	public ScriptExecutable_InvertBoolean(Referenced ref,ScriptExecutable value){
		super(ref);
		m_value=value;
	}
	// ScriptExecutable implementation
	public ScriptValue_Abstract execute()throws Exception_Nodeable{return new ScriptValue_Boolean(getEnvironment(),!((ScriptValue_Boolean)m_value.execute()).getBooleanValue());}
	// ScriptValue_Abstract implementation
	public ScriptValueType getType(){return ScriptValueType.BOOLEAN;}
	public boolean isConvertibleTo(ScriptValueType type){return getType().equals(type);}
	public ScriptValue_Abstract castToType(Referenced ref,ScriptValueType type)throws Exception_Nodeable{return getValue().castToType(ref,type);}
	public ScriptValue_Abstract getValue()throws Exception_Nodeable{return execute();}
	public ScriptValue_Abstract setValue(Referenced ref, ScriptValue_Abstract value)throws Exception_Nodeable{return getValue().setValue(ref,value);}
	public boolean valuesEqual(Referenced ref, ScriptValue_Abstract rhs)throws Exception_Nodeable{return getValue().valuesEqual(ref,rhs);}
	public int valuesCompare(Referenced ref, ScriptValue_Abstract rhs)throws Exception_Nodeable{return getValue().valuesCompare(ref,rhs);}
	// Nodeable implementation
	public boolean nodificate(){
		assert Debugger.openNode("Boolean Inverter");
		assert super.nodificate();
		assert Debugger.addSnapNode("Value",m_value);
		assert Debugger.closeNode();
		return true;
	}
}
