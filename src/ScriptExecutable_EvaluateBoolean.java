public class ScriptExecutable_EvaluateBoolean extends ScriptElement implements ScriptExecutable,ScriptValue_Abstract{
	private ScriptValue_Abstract m_lhs, m_rhs;
	private ScriptOperatorType m_comparison;
	public ScriptExecutable_EvaluateBoolean(Referenced ref,ScriptValue_Abstract lhs, ScriptValue_Abstract rhs, ScriptOperatorType comparison){
		super(ref);
		m_lhs=lhs;
		m_rhs=rhs;
		m_comparison=comparison;
	}
	// ScriptExecutable implementation
	public ScriptValue_Abstract execute()throws Exception_Nodeable{
		assert Debugger.openNode("Boolean Evaluations","Executing Boolean Evaluation");
		assert Debugger.addNode(this);
		ScriptValue_Abstract returning=null;
		assert Debugger.openNode("Getting left value");
		assert Debugger.addSnapNode("Left before resolution",m_lhs);
		ScriptValue_Abstract lhs=m_lhs.getValue();
		assert Debugger.addSnapNode("Left",lhs);
		assert Debugger.closeNode();
		assert Debugger.openNode("Getting right value");
		assert Debugger.addSnapNode("Right before resolution",m_rhs);
		ScriptValue_Abstract rhs=m_rhs.getValue();
		assert Debugger.addSnapNode("Right",rhs);
		assert Debugger.closeNode();
		switch(m_comparison){
			case NONEQUIVALENCY:returning=new ScriptValue_Boolean(getEnvironment(),!lhs.valuesEqual(this,rhs));break;
			case LESS:returning=new ScriptValue_Boolean(getEnvironment(),(lhs.valuesCompare(this,rhs)<0));break;
			case LESSEQUALS:returning=new ScriptValue_Boolean(getEnvironment(),(lhs.valuesCompare(this,rhs)<=0));break;
			case EQUIVALENCY:returning=new ScriptValue_Boolean(getEnvironment(),lhs.valuesEqual(this,rhs));break;
			case GREATEREQUALS:returning=new ScriptValue_Boolean(getEnvironment(),(lhs.valuesCompare(this,rhs)>=0));break;
			case GREATER:returning=new ScriptValue_Boolean(getEnvironment(),(lhs.valuesCompare(this,rhs)>0));break;
			default:throw new Exception_InternalError("Invalid default");
		}
		assert Debugger.closeNode("Returned value",returning);
		return returning;
	}
	// ScriptValue_Abstract implementation
	public ScriptValueType getType(){return ScriptValueType.BOOLEAN;}
	public boolean isConvertibleTo(ScriptValueType type){return getType().equals(type);}
	public ScriptValue_Abstract castToType(Referenced ref,ScriptValueType type)throws Exception_Nodeable{
		assert Debugger.addNode("Type Casting","Casting ("+getType()+" to "+type+")");
		if(getType().equals(type)){return this;}
		throw new Exception_Nodeable_ClassCast(ref,this,type);
	}
	public ScriptValue_Abstract getValue()throws Exception_Nodeable{return execute();}
	public ScriptValue_Abstract setValue(Referenced ref, ScriptValue_Abstract value)throws Exception_Nodeable{return getValue().setValue(ref,value);}
	public boolean valuesEqual(Referenced ref, ScriptValue_Abstract rhs)throws Exception_Nodeable{return getValue().valuesEqual(ref,rhs);}
	public int valuesCompare(Referenced ref, ScriptValue_Abstract rhs)throws Exception_Nodeable{return getValue().valuesCompare(ref,rhs);}
	// Nodeable implementation
	public boolean nodificate(){
		assert Debugger.openNode("Boolean Expression ("+ScriptOperator.getName(m_comparison)+")");
		assert super.nodificate();
		assert Debugger.addSnapNode("Left hand",m_lhs);
		assert Debugger.addSnapNode("Right hand",m_rhs);
		assert Debugger.closeNode();
		return true;
	}
}
