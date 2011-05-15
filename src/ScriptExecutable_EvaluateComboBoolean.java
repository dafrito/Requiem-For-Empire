public class ScriptExecutable_EvaluateComboBoolean extends ScriptElement implements ScriptValue_Abstract,ScriptExecutable,Nodeable{
	private ScriptValue_Abstract m_lhs, m_rhs;
	private ScriptOperatorType m_operator;
	public ScriptExecutable_EvaluateComboBoolean(Referenced ref,ScriptValue_Abstract lhs, ScriptValue_Abstract rhs, ScriptOperatorType operator){
		super(ref);
		m_lhs=lhs;m_rhs=rhs;
		m_operator=operator;
	}
	// ScriptExecutable implementation
	public ScriptValue_Abstract execute()throws Exception_Nodeable{
		assert Debugger.openNode("Combo-Boolean Evaluations","Evaluating Combo-Boolean Expression ("+ScriptOperator.getName(m_operator)+")");
		assert Debugger.addNode(this);
		if(m_lhs.isConvertibleTo(ScriptValueType.BOOLEAN)){throw new Exception_Nodeable_ClassCast(this,m_lhs,ScriptValueType.BOOLEAN);}
		if(m_rhs.isConvertibleTo(ScriptValueType.BOOLEAN)){throw new Exception_Nodeable_ClassCast(this,m_rhs,ScriptValueType.BOOLEAN);}
		ScriptValue_Boolean lhs=(ScriptValue_Boolean)m_lhs.getValue();
		ScriptValue_Boolean rhs=(ScriptValue_Boolean)m_rhs.getValue();
		switch(m_operator){
			case AND:return new ScriptValue_Boolean(getEnvironment(),(lhs.getBooleanValue()&&rhs.getBooleanValue()));
			case OR:return new ScriptValue_Boolean(getEnvironment(),(lhs.getBooleanValue()||rhs.getBooleanValue()));
		}
		throw new Exception_InternalError("Invalid default");
	}
	// ScriptValue_Abstract implementation
	public ScriptValueType getType(){return m_lhs.getType();}
	public boolean isConvertibleTo(ScriptValueType type){return ScriptValueType.isConvertibleTo(getEnvironment(),getType(),type);}
	public ScriptValue_Abstract castToType(Referenced ref,ScriptValueType type)throws Exception_Nodeable{return getValue().castToType(ref,type);}
	public ScriptValue_Abstract getValue()throws Exception_Nodeable{return execute();}
	public ScriptValue_Abstract setValue(Referenced ref, ScriptValue_Abstract value)throws Exception_Nodeable{return getValue().setValue(ref,value);}
	public boolean valuesEqual(Referenced ref,ScriptValue_Abstract rhs)throws Exception_Nodeable{return getValue().valuesEqual(ref,rhs);}
	public int valuesCompare(Referenced ref,ScriptValue_Abstract rhs)throws Exception_Nodeable{return getValue().valuesCompare(ref,rhs);}
	// Nodeable implementation
	public boolean nodificate(){
		assert Debugger.openNode("Combo Boolean Expression");
		assert super.nodificate();
		assert Debugger.addSnapNode("Left side",m_lhs);
		assert Debugger.addSnapNode("Right side",m_rhs);
		assert Debugger.closeNode();
		return true;
	}
}
