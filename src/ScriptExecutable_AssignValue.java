public class ScriptExecutable_AssignValue extends ScriptElement implements ScriptExecutable,ScriptValue_Abstract,Nodeable{
	private ScriptValue_Abstract m_variable;
	private ScriptValue_Abstract m_value;
	public ScriptExecutable_AssignValue(Referenced ref, ScriptValue_Abstract lhs,ScriptValue_Abstract rhs){
		super(ref);
		m_variable=lhs;
		m_value=rhs;
	}
	public ScriptValue_Abstract getLeft(){return m_variable;}
	// ScriptExecutable implementation
	public ScriptValue_Abstract execute()throws Exception_Nodeable{
		assert Debugger.openNode("Value-Assignment Expressions","Assigning Value");
		assert Debugger.addSnapNode("Left variable",m_variable);
		assert Debugger.openNode("Retrieving value");
		assert Debugger.addSnapNode("Current value",m_value);
		ScriptValue_Abstract value=m_value.getValue();
		assert Debugger.closeNode();
		assert Debugger.addSnapNode("Right value",value);
		value=m_variable.setValue(this,value);
		assert Debugger.closeNode();
		return value;
	}
	// ScriptValue_Abstract implementation
	public ScriptValueType getType(){return m_variable.getType();}
	public boolean isConvertibleTo(ScriptValueType type){return m_variable.isConvertibleTo(type);}
	public ScriptValue_Abstract castToType(Referenced ref,ScriptValueType type)throws Exception_Nodeable{return m_variable.castToType(ref,type);}
	public ScriptValue_Abstract getValue()throws Exception_Nodeable{return execute();}
	public ScriptValue_Abstract setValue(Referenced ref,ScriptValue_Abstract value)throws Exception_Nodeable{
		assert Debugger.openNode("Value Assignments","Setting assigment-expression's right-side value");
		assert Debugger.addSnapNode("Former value",m_value);
		m_value=value.castToType(this,getType());
		assert Debugger.addSnapNode("New value",m_value);
		ScriptValue_Abstract returning=execute();
		assert Debugger.closeNode();
		return returning;
	}
	public boolean valuesEqual(Referenced ref, ScriptValue_Abstract rhs)throws Exception_Nodeable{
		return m_variable.valuesEqual(ref,rhs);
	}
	public int valuesCompare(Referenced ref, ScriptValue_Abstract rhs)throws Exception_Nodeable{
		return m_variable.valuesCompare(ref,rhs);
	}
	// Nodeable implementation
	public boolean nodificate(){
		assert Debugger.openNode("Assignment Script Expression");
		assert super.nodificate();
		assert Debugger.addSnapNode("Left variable",m_variable);
		assert Debugger.addSnapNode("Right value",m_value);
		assert Debugger.closeNode();
		return true;
	}
}
