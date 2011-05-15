public class ScriptExecutable_RetrieveCurrentObject extends ScriptValue_Variable implements ScriptExecutable,ScriptValue_Abstract,Nodeable,Referenced{
	private ScriptElement m_reference;
	public ScriptExecutable_RetrieveCurrentObject(Referenced ref,ScriptValueType type)throws Exception_Nodeable{
		super(ref.getEnvironment(),type,null);
		m_reference=ref.getDebugReference();
	}
	public ScriptValue_Variable getVariable()throws Exception_Nodeable{
		assert Debugger.addNode("Executing Current Object Retrieval");
		return new ScriptValue_Variable(getEnvironment(),getType(),getEnvironment().getCurrentObject(),getPermission());
	}
	// Overloaded ScriptValue_Variable functions
	public ScriptKeywordType getPermission()throws Exception_Nodeable{return ScriptKeywordType.PRIVATE;}
	public ScriptValue_Abstract setReference(Referenced ref,ScriptValue_Abstract value)throws Exception_Nodeable{return getVariable().setReference(ref,value);}
	// ScriptExecutable implementation
	public ScriptValue_Abstract execute()throws Exception_Nodeable{return getValue();}
	// Abstract-value implementation
	public boolean isConvertibleTo(ScriptValueType type){return ScriptValueType.isConvertibleTo(getEnvironment(),getType(),type);}
	public ScriptValue_Abstract castToType(Referenced ref,ScriptValueType type)throws Exception_Nodeable{return getVariable().castToType(ref,type);}
	public ScriptValue_Abstract getValue()throws Exception_Nodeable{return getVariable().getValue();}
	public ScriptValue_Abstract setValue(Referenced ref, ScriptValue_Abstract value)throws Exception_Nodeable{return getVariable().setValue(ref,value);}
	public boolean valuesEqual(Referenced ref, ScriptValue_Abstract rhs)throws Exception_Nodeable{return getValue().valuesEqual(ref,rhs);}
	public int valuesCompare(Referenced ref, ScriptValue_Abstract rhs)throws Exception_Nodeable{return getValue().valuesCompare(ref,rhs);}
	// Referenced implementation
	public ScriptElement getDebugReference(){return m_reference;}
	public ScriptEnvironment getEnvironment(){return getDebugReference().getEnvironment();}
	// Nodeable implementation
	public boolean nodificate(){
		assert Debugger.openNode("Current Object Placeholder");
		assert super.nodificate();
		assert Debugger.closeNode();
		return true;
	}
}
