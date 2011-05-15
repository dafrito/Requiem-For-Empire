public class ScriptExecutable_EvaluateMathExpression extends ScriptElement implements ScriptValue_Abstract,ScriptExecutable,Nodeable{
	private ScriptValue_Abstract m_lhs, m_rhs;
	private ScriptOperatorType m_operator;
	private ScriptValueType m_type;
	public ScriptExecutable_EvaluateMathExpression(Referenced ref, ScriptValue_Abstract lhs, ScriptValue_Abstract rhs,ScriptOperatorType expressionType){
		super(ref);
		m_type=ScriptValueType.createType(lhs);
		m_lhs=lhs;
		m_rhs=rhs;
		m_operator=expressionType;
	}
	// ScriptExecutable implementation
	public ScriptValue_Abstract execute()throws Exception_Nodeable{return getValue();}
	// ScriptValue_Abstract implementation
	public ScriptValueType getType(){return m_type;}
	public boolean isConvertibleTo(ScriptValueType type){return m_lhs.isConvertibleTo(type);}
	public ScriptValue_Abstract castToType(Referenced ref,ScriptValueType type)throws Exception_Nodeable{return getValue().castToType(ref,type);}
	public ScriptValue_Abstract getValue()throws Exception_Nodeable{
		assert Debugger.openNode("Mathematic Expressions","Executing Mathematic Expression");
		assert Debugger.addNode(this);
		ScriptValue_Numeric left=(ScriptValue_Numeric)m_lhs.getValue();
		ScriptValue_Numeric right=(ScriptValue_Numeric)m_rhs.getValue();
		if((m_operator==ScriptOperatorType.DIVIDE||m_operator==ScriptOperatorType.MODULUS)&&right.getNumericValue().doubleValue()==0.0d){throw new Exception_Nodeable_DivisionByZero(this);}
		ScriptValue_Abstract returning=null;
		switch(m_operator){
			case PLUS:returning=new ScriptValue_Numeric(getEnvironment(),left.increment(this,right));break;
			case MINUS:returning=new ScriptValue_Numeric(getEnvironment(),left.decrement(this,right));break;
			case MODULUS:returning=new ScriptValue_Numeric(getEnvironment(),left.modulus(this,right));break;
			case MULTIPLY:returning=new ScriptValue_Numeric(getEnvironment(),left.multiply(this,right));break;
			case DIVIDE:returning=new ScriptValue_Numeric(getEnvironment(),left.divide(this,right));break;
		}
		assert Debugger.closeNode();
		return returning;
	}
	public ScriptValue_Abstract setValue(Referenced ref, ScriptValue_Abstract value)throws Exception_Nodeable{throw new Exception_InternalError(this,"Unexecuted Variable");}
	public boolean valuesEqual(Referenced ref, ScriptValue_Abstract rhs)throws Exception_Nodeable{return getValue().valuesEqual(ref,rhs.castToType(ref,getType()));}
	public int valuesCompare(Referenced ref, ScriptValue_Abstract rhs)throws Exception_Nodeable{return getValue().valuesCompare(ref,rhs.castToType(ref,getType()));}
	// Nodeable implementation
	public boolean nodificate(){
		assert Debugger.openNode("Mathematical Expression Evaluator ("+ScriptOperator.getName(m_operator)+")");
		assert Debugger.addNode("Left",m_lhs);
		assert Debugger.addNode("Right",m_rhs);
		assert Debugger.closeNode();
		return true;
	}
}
