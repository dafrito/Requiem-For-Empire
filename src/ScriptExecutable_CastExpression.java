public class ScriptExecutable_CastExpression extends ScriptElement implements ScriptExecutable,ScriptValue_Abstract,Nodeable{
	private ScriptExecutable m_castExpression;
	private ScriptValueType m_type;
	public ScriptExecutable_CastExpression(Referenced ref, ScriptValueType type, ScriptExecutable exec){
		super(ref);
		m_castExpression=exec;
		m_type=type;
	}
	// ScriptExecutable implementation
	public ScriptValue_Abstract execute()throws Exception_Nodeable{
		ScriptValue_Abstract left=m_castExpression.execute().getValue();
		assert Debugger.openNode("Cast Expression Executions","Executing cast to "+getType());
		assert Debugger.addSnapNode("Value",left);
		ScriptValue_Abstract value=m_castExpression.execute().castToType(this,getType());
		assert Debugger.closeNode();
		return value;
	}
	// Abstract-value implementation
	public ScriptValueType getType(){return m_type;}
	public boolean isConvertibleTo(ScriptValueType type){return ScriptValueType.isConvertibleTo(getEnvironment(),getType(),type);}
	public ScriptValue_Abstract castToType(Referenced ref,ScriptValueType type)throws Exception_Nodeable{return getValue().castToType(ref,type);}
	public ScriptValue_Abstract getValue()throws Exception_Nodeable{return execute();}
	public ScriptValue_Abstract setValue(Referenced ref, ScriptValue_Abstract value)throws Exception_Nodeable{return getValue().setValue(ref,value);}
	public boolean valuesEqual(Referenced ref, ScriptValue_Abstract rhs)throws Exception_Nodeable{return getValue().valuesEqual(ref,rhs);}
	public int valuesCompare(Referenced ref, ScriptValue_Abstract rhs)throws Exception_Nodeable{return getValue().valuesCompare(ref,rhs);}
	// Nodeable implementation
	public boolean nodificate(){
		assert Debugger.openNode("Script Cast Expression (To type: "+getType()+")");
		assert super.nodificate();
		assert Debugger.addSnapNode("Cast Expression",m_castExpression);
		assert Debugger.closeNode();
		return true;
	}
}
