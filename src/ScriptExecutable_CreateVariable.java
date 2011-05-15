public class ScriptExecutable_CreateVariable extends ScriptValue_Variable implements ScriptValue_Abstract,ScriptExecutable,Nodeable,Referenced{
	private ScriptKeywordType m_permission;
	private String m_name;
	private ScriptElement m_reference;
	public ScriptExecutable_CreateVariable(Referenced ref,ScriptValueType type,String name,ScriptKeywordType permission)throws Exception_Nodeable{
		super(ref.getEnvironment(),type,null);
		m_reference=ref.getDebugReference();
		m_name=name;
		m_permission=permission;
	}
	public String getName(){return m_name;}
	// Overloaded ScriptValue_Variable functions
	public ScriptKeywordType getPermission()throws Exception_Nodeable{return m_permission;}
	public ScriptValue_Abstract setReference(Referenced ref,ScriptValue_Abstract value)throws Exception_Nodeable{return ((ScriptValue_Variable)execute()).setReference(ref,value);}
	// ScriptExecutable implementation
	public ScriptValue_Abstract execute()throws Exception_Nodeable{
		assert Debugger.openNode("Creating Variable ("+m_name+")");
		ScriptValue_Variable value;
		getEnvironment().getCurrentObject().addVariable(this,m_name,value=new ScriptValue_Variable(getEnvironment(),getType(),getPermission()));
		assert Debugger.addSnapNode("Variable Created",value);
		assert Debugger.closeNode();
		return value;
	}
	// ScriptValue_Abstract implementation
	public ScriptEnvironment getEnvironment(){return getDebugReference().getEnvironment();}
	public boolean isConvertibleTo(ScriptValueType type){return ScriptValueType.isConvertibleTo(getEnvironment(),getType(),type);}
	public ScriptValue_Abstract castToType(Referenced ref,ScriptValueType type)throws Exception_Nodeable{return getValue().castToType(ref,type);}
	public ScriptValue_Abstract getValue()throws Exception_Nodeable{return execute().getValue();}
	public ScriptValue_Abstract setValue(Referenced ref, ScriptValue_Abstract value)throws Exception_Nodeable{
		return execute().setValue(ref,value);
	}
	public boolean valuesEqual(Referenced ref, ScriptValue_Abstract rhs)throws Exception_Nodeable{return getValue().valuesEqual(ref,rhs);}
	public int valuesCompare(Referenced ref, ScriptValue_Abstract rhs)throws Exception_Nodeable{return getValue().valuesCompare(ref,rhs);}
	// Referenced implementation
	public ScriptElement getDebugReference(){return m_reference;}
	// Nodeable implementation
	public boolean nodificate(){
		assert Debugger.openNode("Variable-Creation Script-Element ("+m_name+")");
		assert super.nodificate();
		assert Debugger.closeNode();
		return true;
	}
}
